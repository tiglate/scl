package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;


@Audited
@Entity
@Table(name = "tb_currency")
@EntityListeners(AuditingEntityListener.class)
public class Currency {

    @Id
    @Column(name = "id_currency", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 3)
    private String isoCode;

    @Column(nullable = false, unique = true, length = 3)
    private String bacenCode;

    @Column(nullable = false)
    private String name;

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

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(final String isoCode) {
        this.isoCode = isoCode;
    }

    public String getBacenCode() {
        return bacenCode;
    }

    public void setBacenCode(final String bacenCode) {
        this.bacenCode = bacenCode;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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
