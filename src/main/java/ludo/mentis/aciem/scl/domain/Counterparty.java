package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
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
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "datetime2")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_updated_by")
    private User updatedBy;

    @OneToMany(mappedBy = "counterparty", cascade = CascadeType.ALL)
    private Set<Document> documents = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(final Long value) {
        this.id = value;
    }

    public Integer getOriginId() {
        return originId;
    }

    public void setOriginId(final Integer value) {
        this.originId = value;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(final String value) {
        this.longName = value;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(final String value) {
        this.shortName = value;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(final Boolean value) {
        this.isActive = value;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime value) {
        this.createdAt = value;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final LocalDateTime value) {
        this.updatedAt = value;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final User value) {
        this.updatedBy = value;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(final Set<Document> value) {
        this.documents = value;
    }

}
