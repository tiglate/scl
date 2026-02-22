package ludo.mentis.aciem.scl.model;

import java.sql.Blob;
import java.util.UUID;

public class FileContentDTO {
    private UUID id;
    private String fileName;
    private String fileType;
    private Blob content;

    public FileContentDTO() {
    }

    public FileContentDTO(UUID id, String fileName, String fileType, Blob content) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Blob getContent() {
        return content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }
}
