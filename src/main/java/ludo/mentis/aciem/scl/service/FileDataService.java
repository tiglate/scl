package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.model.FileData;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


public interface FileDataService {

    FileData saveUpload(MultipartFile uploadFile);

    void persistUpload(FileData fileData);

    void removeFileContent(FileData fileData);

    void handleUpdate(FileData oldFileData, FileData newFileData);

    ResponseEntity<InputStreamResource> provideDownload(FileData fileData);

    void cleanUploadDir();

}
