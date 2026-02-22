package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FileContent;
import ludo.mentis.aciem.scl.model.FileContentDTO;
import ludo.mentis.aciem.scl.util.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


public interface FileDataService {

    FileContent create(MultipartFile uploadFile) throws FileUploadException;

    void delete(UUID id) throws FileUploadException;

    FileContentDTO get(UUID id);
}
