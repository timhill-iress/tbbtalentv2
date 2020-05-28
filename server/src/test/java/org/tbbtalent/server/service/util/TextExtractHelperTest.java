package org.tbbtalent.server.service.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tbbtalent.server.model.CandidateAttachment;
import org.tbbtalent.server.repository.CandidateAttachmentRepository;
import org.tbbtalent.server.service.aws.S3ResourceHelper;
import org.tbbtalent.server.util.textExtract.TextExtractHelper;

import javax.transaction.Transactional;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TextExtractHelperTest {
    private static final Logger log = LoggerFactory.getLogger(TextExtractHelperTest.class);

    @Autowired
    private CandidateAttachmentRepository candidateAttachmentRepository;

    @Autowired
    private S3ResourceHelper s3ResourceHelper;

    private TextExtractHelper textExtractHelper = new TextExtractHelper(candidateAttachmentRepository, s3ResourceHelper);

    /**
     * Test Text Extract Helper on different file types
     * @throws IOException from PDFBox extraction methods
     */
    @Transactional
    @Test
    void testDifferentFilesTextExtractHelper() throws IOException {
        assertNotNull(textExtractHelper);

        // Test pdf file
        File pdfFile = new File("src/test/resources/text/EnglishPdf.pdf");
        assertNotNull(pdfFile);
        String pdfExtract = textExtractHelper.getTextExtractFromFile(pdfFile, "pdf");
        assertNotEquals("", pdfExtract);

        // Test pdf file that can't be read (scanned)
        File pdfFileFail = new File("src/test/resources/text/ScannedPdfNoTextExt.pdf");
        assertNotNull(pdfFileFail);
        String pdfExtractFail = textExtractHelper.getTextExtractFromFile(pdfFileFail, "pdf");
        assertEquals("", pdfExtractFail);

        // Test when wrong params and catch the exception
        try {
            String wrongFileType = textExtractHelper.getTextExtractFromFile(pdfFile, "docx");
        } catch (Exception e) {
            log.error(e.getMessage());
            assertNotNull(e);
        }

        try {
            String noFile = textExtractHelper.getTextExtractFromFile(null ,"pdf");
        } catch (Exception e) {
            log.error(e.getMessage());
            assertNotNull(e);
        }

        // Testing Docx files
        File docxFile = new File("src/test/resources/text/EnglishDocx.docx");
        assertNotNull(docxFile);
        String docxExtract = textExtractHelper.getTextExtractFromFile(docxFile, "docx");
        assertNotEquals("", docxExtract);

        // Testing Doc files
        File docFile = new File("src/test/resources/text/EnglishDoc.doc");
        assertNotNull(docFile);
        String docExtract = textExtractHelper.getTextExtractFromFile(docFile, "doc");
        assertNotEquals("", docExtract);

        // Testing Txt files
        File txtFile = new File("src/test/resources/text/EnglishTxt.txt");
        assertNotNull(txtFile);
        String txtExtract = textExtractHelper.getTextExtractFromFile(txtFile, "txt");
        assertNotEquals("", txtExtract);

    }

    /**
     * Test findByFileTypeAndMigrated query to get the file types that were left when the migration was done (newly added files that weren't in the migration S3 Bucket)
     */
    @Transactional
    @Test
    void testRepoFindByTextExtractAndMigrated() {
        List<String> types = Arrays.asList("pdf", "docx", "doc", "txt");
        List<CandidateAttachment> migratedFiles = candidateAttachmentRepository.findByFileTypesAndMigrated(types, true);
        assertNotNull(migratedFiles);
        List<CandidateAttachment> newFiles = candidateAttachmentRepository.findByFileTypesAndMigrated(types, false);
        assertNotNull(newFiles);
    }

    /**
     * Test text extraction on migrated files (in migrated s3 bucket) using Text Extract Helper method
     * @throws IOException
     */
    @Transactional
    @Test
    void testTextExtractMigratedFiles() {
        List<String> types = Arrays.asList("pdf", "docx", "doc", "txt");
        List<CandidateAttachment> files = candidateAttachmentRepository.findByFileTypesAndMigrated(types, true);
        assertNotNull(files);

        // Test with 10 from List
        Set<CandidateAttachment> candidateAttachmentSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            CandidateAttachment candidateAttachment = files.get(i);
            candidateAttachmentSet.add(candidateAttachment);
        }
        assertEquals(10, candidateAttachmentSet.size());

        // Use test set to loop through
        for(CandidateAttachment file : candidateAttachmentSet) {
            try {
                String uniqueFilename = file.getLocation();
                String destination = "candidate/migrated/" + uniqueFilename;
                File srcFile = this.s3ResourceHelper.downloadFile(this.s3ResourceHelper.getS3Bucket(), destination);
                String extractedText = textExtractHelper.getTextExtractFromFile(srcFile, file.getFileType());
                if (StringUtils.isNotBlank(extractedText)) {
                    file.setTextExtract(extractedText);
                }
            } catch (Exception e) {
                log.error("Could not extract text from " + file.getLocation(), e.getMessage());
            }
        }
    }

    /**
     * Test text extraction on newly added files (not in migrated s3 bucket) using Text Extract Helper method
     * @throws IOException
     */
    @Transactional
    @Test
    void testTextExtractMigrateNewFiles() throws IOException {
        List<String> types = Arrays.asList("pdf", "docx", "doc", "txt");
        List<CandidateAttachment> files = candidateAttachmentRepository.findByFileTypesAndMigrated(types, false);
        assertNotNull(files);

        // Test with 10 from List
        Set<CandidateAttachment> candidateAttachmentSet = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            CandidateAttachment candidateAttachment = files.get(i);
            candidateAttachmentSet.add(candidateAttachment);
        }
        assertEquals(5, candidateAttachmentSet.size());

        // Use test set to loop through
        for(CandidateAttachment file : candidateAttachmentSet) {
            try {
                String uniqueFilename = file.getLocation();
                String destination = "candidate/" + file.getCandidate().getCandidateNumber() + "/" + uniqueFilename;
                File srcFile = this.s3ResourceHelper.downloadFile(this.s3ResourceHelper.getS3Bucket(), destination);
                String extractedText = textExtractHelper.getTextExtractFromFile(srcFile, file.getFileType());
                if (StringUtils.isNotBlank(extractedText)) {
                    file.setTextExtract(extractedText);
                }
            } catch (Exception e) {
                log.error("Could not extract text from " + file.getLocation(), e.getMessage());
            }
        }
    }

    /**
     * Test text extraction from migrated Pdf file using PDFBox
     * @throws IOException
     */
    @Transactional
    @Test
    void extractTextFromMigratedPdf() throws IOException {
        // Get all Pdf files
        List<CandidateAttachment> candidatePdfs = candidateAttachmentRepository.findByFileType("pdf");
        assertNotNull(candidatePdfs);

        // Test with 10 from List
        Set<CandidateAttachment> candidateAttachmentSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            CandidateAttachment candidateAttachment;
            candidateAttachment = candidatePdfs.get(i);
            candidateAttachmentSet.add(candidateAttachment);
        }
        assertEquals(10, candidateAttachmentSet.size());

        // Loop through test set using pdf text extract helper methods
        for(CandidateAttachment pdf : candidateAttachmentSet){
            try {
                String uniqueFilename = pdf.getLocation();
                String destination = "candidate/migrated/" + uniqueFilename;
                File srcFile = this.s3ResourceHelper.downloadFile(this.s3ResourceHelper.getS3Bucket(), destination);
                String pdfExtract = textExtractHelper.getTextFromPDFFile(srcFile);
                assertNotNull(pdfExtract);
            } catch (Exception e) {
                log.error("Could not extract text from " + pdf.getLocation(), e.getMessage());
            }
        }
    }

    /**
     * Test text extraction from migrated Docx file using IText
     * @throws IOException
     */
    @Transactional
    @Test
    void extractTextFromMigratedDocx() throws IOException {
        // Get all docx files
        List<CandidateAttachment> candidateDocs = candidateAttachmentRepository.findByFileType("docx");
        assertNotNull(candidateDocs);

        // Create test set of docx files
        Set<CandidateAttachment> candidateAttachmentSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            CandidateAttachment candidateAttachment;
            candidateAttachment = candidateDocs.get(i);
            candidateAttachmentSet.add(candidateAttachment);
        }

        // Loop through test set using docx text extract helper methods
        for(CandidateAttachment docx : candidateAttachmentSet) {
            try {
                String uniqueFilename = docx.getLocation();
                String destination = "candidate/migrated/" + uniqueFilename;
                File srcFile = this.s3ResourceHelper.downloadFile(this.s3ResourceHelper.getS3Bucket(), destination);
                String pdfExtract = textExtractHelper.getTextFromDocxFile(srcFile);
                assertNotNull(pdfExtract);
            } catch (Exception e) {
                log.error("Could not extract text from " + docx.getLocation(), e.getMessage());
            }
        }
    }

    /**
     * Test text extraction from migrated Doc file using IText
     * @throws IOException
     */
    @Transactional
    @Test
    void extractTextFromMigratedDoc() {
        // Get all doc files
        List<CandidateAttachment> candidateDocs = candidateAttachmentRepository.findByFileType("doc");
        assertNotNull(candidateDocs);

        // Create test set of doc files
        Set<CandidateAttachment> candidateAttachmentSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            CandidateAttachment candidateAttachment;
            candidateAttachment = candidateDocs.get(i);
            candidateAttachmentSet.add(candidateAttachment);
        }

        // Loop through test set using docx text extract helper methods
        for(CandidateAttachment doc : candidateAttachmentSet) {
            try{
                String uniqueFilename = doc.getLocation();
                String destination = "candidate/migrated/" + uniqueFilename;
                File srcFile = this.s3ResourceHelper.downloadFile(this.s3ResourceHelper.getS3Bucket(), destination);
                String pdfExtract = textExtractHelper.getTextFromDocFile(srcFile);
                assertNotNull(pdfExtract);
            } catch (Exception e) {
                log.error("Could not extract text from " + doc.getLocation(), e.getMessage());
            }
        }

    }
}
