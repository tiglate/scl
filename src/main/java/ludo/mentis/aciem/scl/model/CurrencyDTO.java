package ludo.mentis.aciem.scl.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CurrencyDTO {

    private Long id;

    @NotNull
    @Size(max = 3)
    @CurrencyIsoCodeUnique
    private String isoCode;

    @NotNull
    @Size(max = 3)
    @CurrencyBacenCodeUnique
    private String bacenCode;

    @NotNull
    @Size(max = 255)
    private String name;
    
    private LocalDate endDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public CurrencyDTO() {
    	
    }
    
    public CurrencyDTO(Long id, String isoCode, String bacenCode, String name, LocalDate endDate,
    		           LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.isoCode = isoCode;
		this.bacenCode = bacenCode;
		this.name = name;
		this.endDate = endDate;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

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
