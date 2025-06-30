package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;

import java.sql.Blob;


@Entity
@Table(name = "tb_file_content")
public class FileContent {

    @Id
    @Column(nullable = false, updatable = false)
    private String uid;

    @Column(nullable = false)
    @Lob
    private Blob content;

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public Blob getContent() {
        return content;
    }

    public void setContent(final Blob content) {
        this.content = content;
    }

}
