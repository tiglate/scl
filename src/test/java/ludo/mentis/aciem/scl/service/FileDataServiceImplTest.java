package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FileContent;
import ludo.mentis.aciem.scl.model.FileContentDTO;
import ludo.mentis.aciem.scl.repos.FileContentRepository;
import ludo.mentis.aciem.scl.exception.FileUploadException;
import ludo.mentis.aciem.scl.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileDataServiceImplTest {

    @Mock
    private FileContentRepository fileContentRepository;

    @Mock
    private FileValidator fileValidator;

    @InjectMocks
    private FileDataServiceImpl fileDataService;

    @BeforeEach
    void setUp() {
        // This method is intentionally empty as all test dependencies are initialized
        // via @Mock and @InjectMocks annotations, requiring no additional setup
    }

    @Test
    void create_shouldSanitizeFileName() throws FileUploadException {
        MockMultipartFile file = new MockMultipartFile("file", "test@file#.png", "image/png", "content".getBytes());
        when(fileValidator.sanitizeFileName(anyString())).thenReturn("test_file_.png");
        when(fileValidator.getFileExtension(anyString())).thenReturn("png");
        when(fileValidator.getMimeType(any())).thenReturn("image/png");
        when(fileValidator.isFileTypeAllowed(anyString())).thenReturn(true);
        when(fileValidator.isFileExtensionAllowed(anyString(), anyString())).thenReturn(true);
        when(fileContentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        FileContent result = fileDataService.create(file);

        assertNotNull(result);
        assertEquals("test_file_.png", result.getFileName());
        verify(fileValidator).sanitizeFileName("test@file#.png");
    }

    @Test
    void create_shouldAllowValidFiles() throws FileUploadException {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());
        when(fileValidator.sanitizeFileName(anyString())).thenReturn("test.pdf");
        when(fileValidator.getFileExtension(anyString())).thenReturn("pdf");
        when(fileValidator.getMimeType(any())).thenReturn("application/pdf");
        when(fileValidator.isFileTypeAllowed(anyString())).thenReturn(true);
        when(fileValidator.isFileExtensionAllowed(anyString(), anyString())).thenReturn(true);
        when(fileContentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        FileContent result = fileDataService.create(file);

        assertNotNull(result);
        assertEquals("test.pdf", result.getFileName());
        assertEquals("application/pdf", result.getFileType());
    }

    @Test
    void create_shouldThrowException_whenFileTypeNotAllowed() {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "application/x-msdownload", "content".getBytes());
        when(fileValidator.sanitizeFileName(anyString())).thenReturn("test.exe");
        when(fileValidator.getFileExtension(anyString())).thenReturn("exe");
        when(fileValidator.getMimeType(any())).thenReturn("application/x-msdownload");
        when(fileValidator.isFileTypeAllowed(anyString())).thenReturn(false);

        FileUploadException exception = assertThrows(FileUploadException.class, () -> fileDataService.create(file));

        assertTrue(exception.getMessage().contains("File type not allowed"));
    }

    @Test
    void create_shouldThrowException_whenExtensionDoesNotMatchContentType() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        when(fileValidator.sanitizeFileName(anyString())).thenReturn("test.png");
        when(fileValidator.getFileExtension(anyString())).thenReturn("png");
        when(fileValidator.getMimeType(any())).thenReturn("application/pdf");
        when(fileValidator.isFileTypeAllowed(anyString())).thenReturn(true);
        when(fileValidator.isFileExtensionAllowed(anyString(), anyString())).thenReturn(false);

        FileUploadException exception = assertThrows(FileUploadException.class, () -> fileDataService.create(file));

        assertTrue(exception.getMessage().contains("does not match detected content type"));
    }

    @Test
    void create_shouldThrowException_whenNoExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "testfile", "image/png", "content".getBytes());
        when(fileValidator.sanitizeFileName(anyString())).thenReturn("testfile");
        when(fileValidator.getFileExtension(anyString())).thenReturn(null);

        FileUploadException exception = assertThrows(FileUploadException.class, () -> fileDataService.create(file));

        assertEquals("File must have an extension", exception.getMessage());
    }

    @Test
    void create_shouldAllowDocxFile() throws FileUploadException {
        // Mocking a docx file (ZIP-based)
        byte[] docxContent = new byte[]{'P', 'K', 3, 4, 0, 0, 0, 0};
        MockMultipartFile file = new MockMultipartFile("file", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", docxContent);
        when(fileValidator.sanitizeFileName(anyString())).thenReturn("test.docx");
        when(fileValidator.getFileExtension(anyString())).thenReturn("docx");
        when(fileValidator.getMimeType(any())).thenReturn("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        when(fileValidator.isFileTypeAllowed(anyString())).thenReturn(true);
        when(fileValidator.isFileExtensionAllowed(anyString(), anyString())).thenReturn(true);
        when(fileContentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        FileContent result = fileDataService.create(file);

        assertNotNull(result);
        assertEquals("test.docx", result.getFileName());
    }

    @Test
    void create_shouldAllowXlsxFile() throws FileUploadException {
        // Mocking a xlsx file (ZIP based)
        byte[] xlsxContent = new byte[]{'P', 'K', 3, 4, 0, 0, 0, 0};
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", xlsxContent);
        when(fileValidator.sanitizeFileName(anyString())).thenReturn("test.xlsx");
        when(fileValidator.getFileExtension(anyString())).thenReturn("xlsx");
        when(fileValidator.getMimeType(any())).thenReturn("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        when(fileValidator.isFileTypeAllowed(anyString())).thenReturn(true);
        when(fileValidator.isFileExtensionAllowed(anyString(), anyString())).thenReturn(true);
        when(fileContentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        FileContent result = fileDataService.create(file);

        assertNotNull(result);
        assertEquals("test.xlsx", result.getFileName());
    }

    @Test
    void create_shouldAllowOldDocFile() throws FileUploadException {
        // OLE2 / Compound File Binary Format signature
        byte[] docContent = new byte[]{(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, 0x1A, (byte) 0xE1};
        MockMultipartFile file = new MockMultipartFile("file", "test.doc", "application/msword", docContent);
        when(fileValidator.sanitizeFileName(anyString())).thenReturn("test.doc");
        when(fileValidator.getFileExtension(anyString())).thenReturn("doc");
        when(fileValidator.getMimeType(any())).thenReturn("application/msword");
        when(fileValidator.isFileTypeAllowed(anyString())).thenReturn(true);
        when(fileValidator.isFileExtensionAllowed(anyString(), anyString())).thenReturn(true);
        when(fileContentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        FileContent result = fileDataService.create(file);

        assertNotNull(result);
        assertEquals("test.doc", result.getFileName());
    }

    @Test
    void create_shouldAllowOldXlsFile() throws FileUploadException {
        // OLE2 signature
        byte[] xlsContent = new byte[]{(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, 0x1A, (byte) 0xE1};
        MockMultipartFile file = new MockMultipartFile("file", "test.xls", "application/vnd.ms-excel", xlsContent);
        when(fileValidator.sanitizeFileName(anyString())).thenReturn("test.xls");
        when(fileValidator.getFileExtension(anyString())).thenReturn("xls");
        when(fileValidator.getMimeType(any())).thenReturn("application/vnd.ms-excel");
        when(fileValidator.isFileTypeAllowed(anyString())).thenReturn(true);
        when(fileValidator.isFileExtensionAllowed(anyString(), anyString())).thenReturn(true);
        when(fileContentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        FileContent result = fileDataService.create(file);

        assertNotNull(result);
        assertEquals("test.xls", result.getFileName());
    }

    @Test
    void create_shouldReturnNull_whenFileIsNull() throws FileUploadException {
        assertNull(fileDataService.create(null));
    }

    @Test
    void create_shouldReturnNull_whenFileIsEmpty() throws FileUploadException {
        MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", new byte[0]);
        assertNull(fileDataService.create(file));
    }

    @Test
    void create_shouldThrowException_whenFileNameIsBlank() {
        MockMultipartFile file = new MockMultipartFile("file", "  ", "text/plain", "content".getBytes());
        FileUploadException exception = assertThrows(FileUploadException.class, () -> fileDataService.create(file));
        assertEquals("File name is required", exception.getMessage());
    }

    @Test
    void create_shouldThrowException_whenIOExceptionOccurs() throws IOException {
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getBytes()).thenThrow(new IOException("Read error"));
        when(fileValidator.sanitizeFileName(anyString())).thenReturn("test.txt");
        when(fileValidator.getFileExtension(anyString())).thenReturn("txt");
        when(fileValidator.getMimeType(any())).thenReturn("text/plain");
        when(fileValidator.isFileTypeAllowed(anyString())).thenReturn(true);
        when(fileValidator.isFileExtensionAllowed(anyString(), anyString())).thenReturn(true);

        FileUploadException exception = assertThrows(FileUploadException.class, () -> fileDataService.create(file));
        assertEquals("Failed to save file", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void delete_shouldDoNothing_whenIdIsNull() throws FileUploadException {
        fileDataService.delete(null);
        verifyNoInteractions(fileContentRepository);
    }

    @Test
    void delete_shouldDeleteFile_whenExists() throws FileUploadException {
        UUID id = UUID.randomUUID();
        FileContent fileContent = new FileContent();
        when(fileContentRepository.findById(id)).thenReturn(Optional.of(fileContent));

        fileDataService.delete(id);

        verify(fileContentRepository).delete(fileContent);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(fileContentRepository.findById(id)).thenReturn(Optional.empty());

        FileUploadException exception = assertThrows(FileUploadException.class, () -> fileDataService.delete(id));
        assertTrue(exception.getMessage().contains("File not found for ID: " + id));
    }

    @Test
    void get_shouldReturnDTO_whenExists() throws SQLException {
        UUID id = UUID.randomUUID();
        FileContent fileContent = new FileContent();
        fileContent.setId(id);
        fileContent.setFileName("test.txt");
        fileContent.setFileType("text/plain");
        fileContent.setContent(new SerialBlob("content".getBytes()));
        when(fileContentRepository.findById(id)).thenReturn(Optional.of(fileContent));

        FileContentDTO result = fileDataService.get(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("test.txt", result.getFileName());
        assertEquals("text/plain", result.getFileType());
    }

    @Test
    void get_shouldThrowNotFoundException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(fileContentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> fileDataService.get(id));
    }
}
