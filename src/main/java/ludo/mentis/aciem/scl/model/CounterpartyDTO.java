package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;


public class CounterpartyDTO {

    private Long id;

    private Integer originId;

    @NotBlank
    @Size(max = 255)
    private String longName;

    @Size(max = 255)
    private String shortName;

    @NotNull
    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long updatedById;
    
    private String updatedByName;

    private List<DocumentDTO> documents;
    
    public CounterpartyDTO() {

    }

    public CounterpartyDTO(Long id, Integer originId, String longName, String shortName, Boolean isActive,
    		               LocalDateTime createdAt, LocalDateTime updatedAt, Long updatedById, String updatedByName) {
		this.id = id;
		this.originId = originId;
		this.longName = longName;
		this.shortName = shortName;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.updatedById = updatedById;
		this.updatedByName = updatedByName;
	}

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

    public Long getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(final Long value) {
        this.updatedById = value;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(final String value) {
        this.updatedByName = value;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(final List<DocumentDTO> value) {
        this.documents = value;
    }
}
