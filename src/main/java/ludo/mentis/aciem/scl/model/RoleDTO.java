package ludo.mentis.aciem.scl.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class RoleDTO {

    private Long id;

    @NotBlank
    @Size(max = 50)
    @RoleCodeUnique
    private String code;

    @Size(max = 255)
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public RoleDTO() {
    	
    }

    public RoleDTO(Long id, String code, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.code = code;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
        return id;
    }

    public void setId(final Long value) {
        this.id = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String value) {
        this.code = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String value) {
        this.description = value;
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
