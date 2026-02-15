package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ludo.mentis.aciem.scl.validation.DocumentTypeNameUnique;

import java.time.LocalDateTime;


public class DocumentTypeDTO {

    private Long id;

    @NotBlank
    @Size(max = 255)
    @DocumentTypeNameUnique
    private String name;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public DocumentTypeDTO() {
    	
    }
    
    public DocumentTypeDTO(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
        return id;
    }

    public void setId(final Long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(final String value) {
        this.name = value;
    }

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime value) {
		this.createdAt = value;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime value) {
		this.updatedAt = value;
	}
}
