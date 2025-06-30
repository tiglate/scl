package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.OffsetDateTime;


@Audited
@Entity
@Table(name = "tb_document")
@EntityListeners(AuditingEntityListener.class)
public class Document {

    @Id
    @Column(name = "id_document", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column
    private LocalDate expiration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_document_type")
    private DocumentType documentType;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "datetime2")
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "datetime2")
    private OffsetDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public void setExpiration(final LocalDate expiration) {
        this.expiration = expiration;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(final DocumentType documentType) {
        this.documentType = documentType;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @SuppressWarnings("unused")
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @SuppressWarnings("unused")
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
