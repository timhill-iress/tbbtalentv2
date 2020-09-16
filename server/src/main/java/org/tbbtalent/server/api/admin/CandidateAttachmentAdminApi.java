package org.tbbtalent.server.api.admin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tbbtalent.server.exception.NoSuchObjectException;
import org.tbbtalent.server.model.db.AttachmentType;
import org.tbbtalent.server.model.db.CandidateAttachment;
import org.tbbtalent.server.model.db.User;
import org.tbbtalent.server.request.attachment.CreateCandidateAttachmentRequest;
import org.tbbtalent.server.request.attachment.SearchByIdCandidateAttachmentRequest;
import org.tbbtalent.server.request.attachment.SearchCandidateAttachmentsRequest;
import org.tbbtalent.server.request.attachment.UpdateCandidateAttachmentRequest;
import org.tbbtalent.server.security.UserContext;
import org.tbbtalent.server.service.db.CandidateAttachmentService;
import org.tbbtalent.server.service.db.GoogleFileSystemService;
import org.tbbtalent.server.util.dto.DtoBuilder;

@RestController()
@RequestMapping("/api/admin/candidate-attachment")
public class CandidateAttachmentAdminApi {

    private final CandidateAttachmentService candidateAttachmentService;
    private final UserContext userContext;

    @Autowired
    public CandidateAttachmentAdminApi(CandidateAttachmentService candidateAttachmentService,
                                       UserContext userContext) {
        this.candidateAttachmentService = candidateAttachmentService;
        this.userContext = userContext;
    }

    @PostMapping("search")
    public List<Map<String, Object>> search(@RequestBody SearchByIdCandidateAttachmentRequest request) {
        List<CandidateAttachment> candidateAttachments;
        if (request.isCvOnly()) {
            candidateAttachments = this.candidateAttachmentService.listCandidateCvs(request.getCandidateId());
        } else {
            candidateAttachments = this.candidateAttachmentService.listCandidateAttachments(request.getCandidateId());
        }
        return candidateAttachmentDto().buildList(candidateAttachments);
    }

    @PostMapping("search-paged")
    public Map<String, Object> searchPaged(@RequestBody SearchCandidateAttachmentsRequest request) {
        Page<CandidateAttachment> candidateAttachments = this.candidateAttachmentService.searchCandidateAttachments(request);
        User user = userContext.getLoggedInUser();
        return candidateAttachmentDto().buildPage(candidateAttachments);
    }

    /**
     * This was called for attachments which were uploaded to AWS S3.
     * <p/> 
     * @deprecated {@link #uploadAttachment} is used now instead. It is
     * called now that attachments are first uploaded to this server,
     * then uploaded to Google Drive from here. 
     * @param request Details about attachment record to be created. 
     * @return Candidate attachment
     */
    @PostMapping()
    public Map<String, Object> createCandidateAttachment(@RequestBody CreateCandidateAttachmentRequest request) {
        CandidateAttachment candidateAttachment = candidateAttachmentService.createCandidateAttachment(request);
        return candidateAttachmentDto().build(candidateAttachment);
    }

    /**
     * Downloads (to the browser computer) the given Google attachment.
     * @param id ID of attachment to be downloaded
     * @throws NoSuchObjectException if no Google attachment with that id exists
     * @throws IOException if there is a problem retrieving the attachment
     */
    @GetMapping("{id}/download")
    public void downloadAttachment(
            @PathVariable("id") long id, HttpServletResponse response ) 
            throws IOException {
        CandidateAttachment attachment = 
                candidateAttachmentService.getCandidateAttachment(id);
        if (attachment.getType() != AttachmentType.googlefile) {
            throw new NoSuchObjectException(GoogleFileSystemService.class, id);
        }
        
        response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + attachment.getName() + "\"");
        response.setContentType("application/octet-stream");
        
        candidateAttachmentService.downloadCandidateAttachment(
                attachment, response.getOutputStream());
        response.flushBuffer();
    }

    /**
     * Upload an attachment associated with the given candidate and
     * creates a CandidateAttachment record on the database.
     * <p/>
     * Processes uploaded file and then uploads it again to Google Drive.
     * This replaces the old {@link #createCandidateAttachment} 
     * @param id ID of candidate associated with file attachment
     * @param cv True if the attachment is a CV (in which case its text is 
     *           extracted for keywords).
     * @param file Attachment file           
     * @return Candidate attachment
     */
    @PostMapping("{id}/upload")
    public Map<String, Object> uploadAttachment(
            @PathVariable("id") long id, @RequestParam("cv") Boolean cv,
            @RequestParam("file") MultipartFile file ) 
            throws IOException {
        CandidateAttachment candidateAttachment = 
                candidateAttachmentService.uploadAttachment(id, cv, file);
        return candidateAttachmentDto().build(candidateAttachment);
    }

    @PutMapping()
    public Map<String, Object> update(
            @RequestBody UpdateCandidateAttachmentRequest request) 
            throws IOException {
        CandidateAttachment candidateAttachment = 
                this.candidateAttachmentService.updateCandidateAttachment(request);
        return candidateAttachmentDto().build(candidateAttachment);
    }

    /**
     * Deletes the attachment with the given id.
     * @param id Id of attachment to be deleted.
     * @return ???
     */
    @DeleteMapping("{id}")
    public ResponseEntity deleteCandidateAttachment(@PathVariable("id") Long id) {
        candidateAttachmentService.deleteCandidateAttachment(id);
        return ResponseEntity.ok().build();
    }


    private DtoBuilder candidateAttachmentDto() {
        return new DtoBuilder()
                .add("id")
                .add("type")
                .add("name")
                .add("location")
                .add("fileType")
                .add("migrated")
                .add("cv")
                .add("createdBy", userDto())
                .add("createdDate")
                .add("updatedBy", userDto())
                .add("updatedDate")
                ;
    }


    private DtoBuilder userDto() {
        return new DtoBuilder()
                .add("id")
                .add("firstName")
                .add("lastName")
                ;
    }


}
