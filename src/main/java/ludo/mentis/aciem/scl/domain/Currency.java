package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;


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

    @Column
    private LocalDate endDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "datetime2")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "datetime2")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(final Long value) {
        this.id = value;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(final String value) {
        this.isoCode = value;
    }

    public String getBacenCode() {
        return bacenCode;
    }

    public void setBacenCode(final String value) {
        this.bacenCode = value;
    }

    public String getName() {
        return name;
    }

    public void setName(final String value) {
        this.name = value;
    }

    public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate value) {
		this.endDate = value;
	}

	public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @SuppressWarnings("unused")
    public void setCreatedAt(LocalDateTime value) {
        this.createdAt = value;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @SuppressWarnings("unused")
    public void setUpdatedAt(LocalDateTime value) {
        this.updatedAt = value;
    }

}
