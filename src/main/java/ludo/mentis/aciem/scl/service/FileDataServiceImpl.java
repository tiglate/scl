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

    private final FileValidator fileValidator;
    private final FileContentRepository fileContentRepository;
    private static final Logger log = LoggerFactory.getLogger(FileDataServiceImpl.class);

    public FileDataServiceImpl(final FileContentRepository fileContentRepository,
                               final FileValidator fileValidator) {
        this.fileContentRepository = fileContentRepository;
        this.fileValidator = fileValidator;
    }

    @Override
    public FileContent create(final MultipartFile uploadFile) throws FileUploadException {
        if (uploadFile == null || uploadFile.isEmpty()) {
            return null;
        }

        var originalFilename = uploadFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isBlank()) {
            throw new FileUploadException("File name is required");
        }
        var sanitizedFilename = fileValidator.sanitizeFileName(originalFilename);

        var extension = fileValidator.getFileExtension(sanitizedFilename);
        if (extension == null) {
            throw new FileUploadException("File must have an extension");
        }

        var detectedMimeType = fileValidator.getMimeType(uploadFile);
        if (!fileValidator.isFileTypeAllowed(detectedMimeType)) {
            throw new FileUploadException("File type not allowed: " + detectedMimeType);
        }

        if (!fileValidator.isFileExtensionAllowed(detectedMimeType, extension)) {
            throw new FileUploadException("File extension '%s' does not match detected content type '%s'".formatted(extension, detectedMimeType));
        }

        var fileContent = new FileContent();
        fileContent.setFileName(sanitizedFilename);
        fileContent.setFileType(detectedMimeType);
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
            log.warn("File ID is null, nothing to delete");
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
