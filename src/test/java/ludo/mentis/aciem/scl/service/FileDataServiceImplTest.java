package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FileContent;
import ludo.mentis.aciem.scl.model.FileData;
import ludo.mentis.aciem.scl.repos.FileContentRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileDataServiceImplTest {

    @Mock
    private FileContentRepository fileContentRepository;

    @Mock
    private MultipartFile multipartFile;

    private FileDataServiceImpl fileDataService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        fileDataService = new FileDataServiceImpl(fileContentRepository);
        // Ensure the upload directory exists for tests
        new File(FileDataServiceImpl.UPLOAD_DIRECTORY).mkdirs();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        // Clean up the upload directory after each test
        FileSystemUtils.deleteRecursively(new File(FileDataServiceImpl.UPLOAD_DIRECTORY));
    }

    @Test
    void testSaveUpload_EmptyFile() {
        when(multipartFile.isEmpty()).thenReturn(true);

        FileData result = fileDataService.saveUpload(multipartFile);

        assertThat(result).isNull();
    }

    @Test
    void testSaveUpload_Success() throws IOException {
        String originalFilename = "test.txt";
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        doAnswer(invocation -> {
            File dest = invocation.getArgument(0);
            dest.createNewFile();
            return null;
        }).when(multipartFile).transferTo(any(File.class));

        FileData result = fileDataService.saveUpload(multipartFile);

        assertThat(result).isNotNull();
        assertThat(result.getUid()).isNotNull();
        assertThat(result.getFileName()).isEqualTo(originalFilename);

        File uploadedFile = new File(FileDataServiceImpl.UPLOAD_DIRECTORY + "/" + result.getUid() + "/" + originalFilename);
        assertThat(uploadedFile).exists();
        verify(multipartFile).transferTo(uploadedFile);
    }

    @Test
    void testPersistUpload_NullData() {
        fileDataService.persistUpload(null);
        verifyNoInteractions(fileContentRepository);
    }

    @Test
    void testPersistUpload_Success() throws IOException, SQLException {
        String uid = "test-uid";
        String fileName = "test.txt";
        FileData fileData = new FileData();
        fileData.setUid(uid);
        fileData.setFileName(fileName);

        // Create the dummy file to be persisted
        Path tempDirPath = Path.of(FileDataServiceImpl.UPLOAD_DIRECTORY, uid);
        Files.createDirectories(tempDirPath);
        Path tempFilePath = tempDirPath.resolve(fileName);
        Files.write(tempFilePath, "test content".getBytes());

        fileDataService.persistUpload(fileData);

        ArgumentCaptor<FileContent> captor = ArgumentCaptor.forClass(FileContent.class);
        verify(fileContentRepository).save(captor.capture());

        FileContent persisted = captor.getValue();
        assertThat(persisted.getUid()).isEqualTo(uid);
        assertThat(persisted.getContent()).isNotNull();
        assertThat(persisted.getContent().length()).isEqualTo("test content".length());

        // Verify file was deleted
        assertThat(Files.exists(tempFilePath)).isFalse();
    }

    @Test
    void testRemoveFileContent_NullData() {
        fileDataService.removeFileContent(null);
        verifyNoInteractions(fileContentRepository);
    }

    @Test
    void testRemoveFileContent_Success() {
        FileData fileData = new FileData();
        fileData.setUid("test-uid");

        fileDataService.removeFileContent(fileData);

        verify(fileContentRepository).deleteById("test-uid");
    }

    @Test
    void testHandleUpdate_NoChange() {
        FileData oldFileData = new FileData();
        oldFileData.setUid("same-uid");
        FileData newFileData = new FileData();
        newFileData.setUid("same-uid");

        fileDataService.handleUpdate(oldFileData, newFileData);

        verifyNoInteractions(fileContentRepository);
    }

    @Test
    void testHandleUpdate_Change() throws IOException {
        FileData oldFileData = new FileData();
        oldFileData.setUid("old-uid");
        FileData newFileData = new FileData();
        newFileData.setUid("new-uid");
        newFileData.setFileName("new.txt");

        // Prepare new file for persistUpload
        Path tempDirPath = Path.of(FileDataServiceImpl.UPLOAD_DIRECTORY, "new-uid");
        Files.createDirectories(tempDirPath);
        Path tempFilePath = tempDirPath.resolve("new.txt");
        Files.write(tempFilePath, "new content".getBytes());

        fileDataService.handleUpdate(oldFileData, newFileData);

        verify(fileContentRepository).deleteById("old-uid");
        verify(fileContentRepository).save(any(FileContent.class));
    }

    @Test
    void testProvideDownload_NullData() {
        assertThatThrownBy(() -> fileDataService.provideDownload(null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testProvideDownload_NotFound() {
        FileData fileData = new FileData();
        fileData.setUid("non-existent");
        when(fileContentRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileDataService.provideDownload(fileData))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testProvideDownload_Success() throws SQLException {
        FileData fileData = new FileData();
        fileData.setUid("test-uid");
        fileData.setFileName("test.pdf");

        FileContent fileContent = new FileContent();
        fileContent.setUid("test-uid");
        byte[] bytes = "pdf content".getBytes();
        Blob blob = new SerialBlob(bytes);
        fileContent.setContent(blob);

        when(fileContentRepository.findById("test-uid")).thenReturn(Optional.of(fileContent));

        ResponseEntity<InputStreamResource> response = fileDataService.provideDownload(fileData);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getContentType().toString()).isEqualTo("application/pdf");
        assertThat(response.getHeaders().getContentDisposition().getFilename()).isEqualTo("test.pdf");
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testCleanUploadDir() throws IOException, InterruptedException {
        // Create an old directory and a new directory
        Path oldDir = Path.of(FileDataServiceImpl.UPLOAD_DIRECTORY, "old-dir");
        Files.createDirectories(oldDir);
        // Set last modified to 2 hours ago
        long twoHoursAgo = System.currentTimeMillis() - (2 * 60 * 60 * 1000);
        new File(oldDir.toString()).setLastModified(twoHoursAgo);

        Path newDir = Path.of(FileDataServiceImpl.UPLOAD_DIRECTORY, "new-dir");
        Files.createDirectories(newDir);
        // last modified is now

        fileDataService.cleanUploadDir();

        assertThat(Files.exists(oldDir)).isFalse();
        assertThat(Files.exists(newDir)).isTrue();
    }
}
