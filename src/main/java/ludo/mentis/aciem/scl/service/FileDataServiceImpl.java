package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FileContent;
import ludo.mentis.aciem.scl.model.FileContentDTO;
import ludo.mentis.aciem.scl.repos.FileContentRepository;
import ludo.mentis.aciem.scl.util.FileUploadException;
import ludo.mentis.aciem.scl.util.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;


@Service
@Transactional(rollbackFor = Exception.class)
public class FileDataServiceImpl implements FileDataService {

    private static final Logger log = LoggerFactory.getLogger(FileDataServiceImpl.class);

    private final FileContentRepository fileContentRepository;

    public FileDataServiceImpl(final FileContentRepository fileContentRepository) {
        this.fileContentRepository = fileContentRepository;
    }

    @Override
    public FileContent create(final MultipartFile uploadFile) throws FileUploadException {
        if (uploadFile.isEmpty()) {
            return null;
        }

        var fileContent = new FileContent();
        fileContent.setFileName(uploadFile.getOriginalFilename());
        fileContent.setFileType(uploadFile.getContentType());
        try {
            fileContent.setContent(new SerialBlob(uploadFile.getBytes()));
        } catch (IOException | SQLException e) {
            throw new FileUploadException("Failed to save file", e);
        }

        return fileContentRepository.save(fileContent);
    }

    @Override
    public void delete(final UUID id) throws FileUploadException {
        if (id == null) {
            log.warn("File ID <{}> is null, nothing to delete", id.toString());
            return;
        }
        var fileContent = fileContentRepository
                .findById(id)
                .orElseThrow(() -> new FileUploadException("File not found for ID: " + id));
        fileContentRepository.delete(fileContent);
    }

    @Override
    public FileContentDTO get(UUID id) {
        return fileContentRepository
                .findById(id)
                .map(fileContent -> new FileContentDTO(
                        fileContent.getId(),
                        fileContent.getFileName(),
                        fileContent.getFileType(),
                        fileContent.getContent()))
                .orElseThrow(() -> new NotFoundException("File not found for ID: " + id));
    }
}
