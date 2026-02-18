package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FileContent;
import ludo.mentis.aciem.scl.model.FileData;
import ludo.mentis.aciem.scl.repos.FileContentRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;


@Service
public class FileDataServiceImpl implements FileDataService {

    private static final Logger log = LoggerFactory.getLogger(FileDataServiceImpl.class);
    public static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    private final FileContentRepository fileContentRepository;

    public FileDataServiceImpl(final FileContentRepository fileContentRepository) {
        this.fileContentRepository = fileContentRepository;
    }

    private String encodeFileName(final String fileName) {
        return fileName.replaceAll("[^0-9a-zA-Z.!\\-_\\[\\]]", "-");
    }

    @Override
    public FileData saveUpload(final MultipartFile uploadFile) {
        if (uploadFile.isEmpty()) {
            // no file submitted or no content
            return null;
        }

        log.info("saving uploaded file {}", uploadFile.getOriginalFilename());

        final String uid = UUID.randomUUID().toString();
        final String encodedFileName = encodeFileName(uploadFile.getOriginalFilename());
        final File tempDir = new File(UPLOAD_DIRECTORY + "/" + uid);
        if (!tempDir.mkdirs()) {
            throw new RuntimeException("could not prepare temporary directory " + tempDir.getPath());
        }
        final File tempFile = new File(tempDir, encodedFileName);
        try {
            uploadFile.transferTo(tempFile);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }

        final FileData fileData = new FileData();
        fileData.setUid(uid);
        fileData.setFileName(encodedFileName);
        return fileData;
    }

    @Override
    @Transactional(
            propagation = Propagation.MANDATORY
    )
    public void persistUpload(final FileData fileData) {
        if (fileData == null) {
            return;
        }

        log.info("persisting file upload {}", fileData.getUid());

        final File tempFile = new File(UPLOAD_DIRECTORY + "/" + fileData.getUid() + "/" + fileData.getFileName());
        final FileContent fileContent = new FileContent();
        fileContent.setId(UUID.fromString(fileData.getUid()));
        try {
            fileContent.setContent(new SerialBlob(FileCopyUtils.copyToByteArray(tempFile)));
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        fileContentRepository.save(fileContent);

        if (!tempFile.delete()) {
            log.error("could not delete file {}", tempFile.getPath());
        }
    }

    @Override
    @Transactional(
            propagation = Propagation.MANDATORY
    )
    public void removeFileContent(final FileData fileData) {
        if (fileData != null) {
            fileContentRepository.deleteById(fileData.getUid());
        }
    }

    @Override
    @Transactional(
            propagation = Propagation.MANDATORY
    )
    public void handleUpdate(final FileData oldFileData, final FileData newFileData) {
        if (oldFileData != null && newFileData != null && oldFileData.getUid().equals(newFileData.getUid())) {
            // no change
            return;
        }
        if (oldFileData != null) {
            removeFileContent(oldFileData);
        }
        if (newFileData != null) {
            persistUpload(newFileData);
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> provideDownload(final FileData fileData) {
        if (fileData == null) {
            throw new NotFoundException();
        }
        final Blob fileContent = fileContentRepository.findById(fileData.getUid())
                .map(FileContent::getContent)
                .orElseThrow(NotFoundException::new);
        try {
            final InputStreamResource inputStreamResource = new InputStreamResource(fileContent.getBinaryStream());
            String contentType = URLConnection.guessContentTypeFromName(fileData.getFileName());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileData.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(fileContent.length())
                    .body(inputStreamResource);
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Scheduled(cron = "0 0 0/2 * * *")
    public void cleanUploadDir() {
        log.info("cleaning upload dir");
        final File uploadDir = new File(UPLOAD_DIRECTORY);
        final File[] subDirs = uploadDir.listFiles();
        if (subDirs == null) {
            return;
        }
        final long cutoff = System.currentTimeMillis() - Duration.ofHours(1).toMillis();
        for (final File subDir : subDirs) {
            if (subDir.lastModified() < cutoff) {
                if (!FileSystemUtils.deleteRecursively(subDir)) {
                    log.error("could not delete directory {}", subDir.getPath());
                }
            }
        }
    }

}
