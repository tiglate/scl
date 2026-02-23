package ludo.mentis.aciem.scl.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

    String getMimeType(final MultipartFile uploadFile);

    boolean isFileTypeAllowed(String mimeType);

    boolean isFileExtensionAllowed(String mimeType, String extension);

    String getFileExtension(String fileName);

    String sanitizeFileName(String fileName);
}
