package org.tbbtalent.server.request.attachment;

import javax.validation.constraints.NotNull;

public class SearchByIdCandidateAttachmentRequest {

    @NotNull
    private Long candidateId;

    private boolean cvOnly;

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public boolean isCvOnly() { return cvOnly; }

    public void setCvOnly(boolean cvOnly) { this.cvOnly = cvOnly; }
}