package ludo.mentis.aciem.scl.service;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class FileValidatorImpl implements FileValidator {

    private static final Logger log = LoggerFactory.getLogger(FileValidatorImpl.class);
    private static final Map<String, Set<String>> ALLOWED_EXTENSIONS_BY_MIME;
    private static final Pattern FILENAME_SANITIZER = Pattern.compile("[^a-zA-Z0-9._-]");
    private final Tika tika = new Tika();

    static {
        ALLOWED_EXTENSIONS_BY_MIME = Map.ofEntries(
                //Common image formats
                Map.entry("image/png", Set.of("png")),
                Map.entry("image/jpeg", Set.of("jpg", "jpeg")),
                Map.entry("image/bmp", Set.of("bmp")),
                //PDF
                Map.entry("application/pdf", Set.of("pdf")),
                //Zip file
                Map.entry("application/zip", Set.of("zip", "docx", "xlsx", "pptx")),
                //Old Office format files
                Map.entry("application/msword", Set.of("doc")),
                Map.entry("application/vnd.ms-excel", Set.of("xls")),
                Map.entry("application/vnd.ms-powerpoint", Set.of("ppt")),
                //New Office format files
                Map.entry("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", Set.of("xlsx")),
                Map.entry("application/vnd.openxmlformats-officedocument.wordprocessingml.document", Set.of("docx")),
                Map.entry("application/vnd.openxmlformats-officedocument.presentationml.presentation", Set.of("pptx")),
                //Microsoft Outlook e-mail file
                Map.entry("application/vnd.ms-outlook", Set.of("msg"))
        );
    }

    @Override
    public String getMimeType(final MultipartFile uploadFile) {
        final var fileName = sanitizeFileName(uploadFile.getOriginalFilename());
        final var extension = getFileExtension(fileName);
        String detectedMimeType;
        try {
            detectedMimeType = tika.detect(uploadFile.getInputStream(), fileName);
            if ("application/zip".equals(detectedMimeType)) {
                detectedMimeType = switch (extension) {
                    case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                    case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                    default -> detectedMimeType;
                };
            }
        } catch (IOException e) {
            detectedMimeType = uploadFile.getContentType();
            log.error("Failed to detect file type. File: {}", fileName, e);
        }
        return detectedMimeType;
    }

    @Override
    public boolean isFileTypeAllowed(final String mimeType) {
        return ALLOWED_EXTENSIONS_BY_MIME.containsKey(mimeType);
    }

    @Override
    public boolean isFileExtensionAllowed(final String mimeType, final String extension) {
        final var allowedExtensions = ALLOWED_EXTENSIONS_BY_MIME.get(mimeType);
        return allowedExtensions.contains(extension);
    }

    @Override
    public String getFileExtension(final String fileName) {
        final var lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex == -1 ? null : fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    @Override
    public String sanitizeFileName(final String fileName) {
        return FILENAME_SANITIZER.matcher(fileName).replaceAll("_");
    }
}
