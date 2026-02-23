package ludo.mentis.aciem.scl.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileValidatorImplTest {

    private final FileValidatorImpl fileValidator = new FileValidatorImpl();

    @Test
    void sanitizeFileName_shouldReplaceSpecialCharacters() {
        assertEquals("test_file_.png", fileValidator.sanitizeFileName("test@file#.png"));
        assertEquals("normal-file_name.pdf", fileValidator.sanitizeFileName("normal-file_name.pdf"));
        assertEquals("space_replaced.zip", fileValidator.sanitizeFileName("space replaced.zip"));
    }

    @Test
    void getFileExtension_shouldReturnExtension() {
        assertEquals("png", fileValidator.getFileExtension("test.png"));
        assertEquals("docx", fileValidator.getFileExtension("test.docx"));
        assertEquals("xlsx", fileValidator.getFileExtension("document.xlsx"));
        assertNull(fileValidator.getFileExtension("no-extension"));
    }

    @Test
    void validateFilesFromResources() throws IOException {
        testFile("test.png", "image/png");
        testFile("test.jpg", "image/jpeg");
        testFile("test.bmp", "image/bmp");
        testFile("test.pdf", "application/pdf");
        testFile("test.zip", "application/zip");
        testFile("test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        testFile("test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        testFile("test.doc", "application/msword");
        testFile("test.xls", "application/vnd.ms-excel");
    }

    private void testFile(String filename, String expectedMimeType) throws IOException {
        byte[] content = Files.readAllBytes(new ClassPathResource("files/" + filename).getFile().toPath());
        MockMultipartFile file = new MockMultipartFile("file", filename, "application/octet-stream", content);

        String detectedMimeType = fileValidator.getMimeType(file);
        assertEquals(expectedMimeType, detectedMimeType, "MIME type mismatch for " + filename);
        assertTrue(fileValidator.isFileTypeAllowed(detectedMimeType), "File type should be allowed: " + detectedMimeType);
        assertTrue(fileValidator.isFileExtensionAllowed(detectedMimeType, fileValidator.getFileExtension(filename)), 
                   "Extension should be allowed for " + filename);
    }

    @Test
    void isFileTypeAllowed_shouldReturnFalseForInvalidType() {
        assertFalse(fileValidator.isFileTypeAllowed("application/x-msdownload"));
        assertFalse(fileValidator.isFileTypeAllowed("text/plain"));
    }

    @Test
    void isFileExtensionAllowed_shouldReturnFalseForMismatch() {
        assertFalse(fileValidator.isFileExtensionAllowed("image/png", "jpg"));
        assertFalse(fileValidator.isFileExtensionAllowed("application/pdf", "docx"));
    }
}
