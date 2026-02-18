package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.sql.Blob;
import java.util.UUID;


@Audited
@Entity
@Table(name = "tb_file_content")
public class FileContent {

    @Id
    @Column(name = "id_file_content", columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(nullable = false)
    @Lob
    private Blob content;

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

    public void setContent(final Blob content) {
        this.content = content;
    }

}
