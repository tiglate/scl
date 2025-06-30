package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Set;


@Audited
@Entity
@Table(name = "tb_counterparty")
@EntityListeners(AuditingEntityListener.class)
public class Counterparty {

    @Id
    @Column(name = "id_counterparty", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer originId;

    @Column(nullable = false)
    private String longName;

    @Column
    private String shortName;

    @Column(nullable = false)
    private Boolean isActive;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "datetime2")
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "datetime2")
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_updated_by")
    private User updatedBy;

    @ManyToMany
    @JoinTable(
            name = "tb_counterparty_document",
            joinColumns = @JoinColumn(name = "id_counterparty"),
            inverseJoinColumns = @JoinColumn(name = "id_document")
    )
    private Set<Document> documents;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Integer getOriginId() {
        return originId;
    }

    public void setOriginId(final Integer originId) {
        this.originId = originId;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(final String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(final Boolean isActive) {
        this.isActive = isActive;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(final Set<Document> documents) {
        this.documents = documents;
    }

}
