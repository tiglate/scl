package ludo.mentis.aciem.scl.model;

import java.time.LocalDate;

public class DocumentDTO {
	
	private Long id;
	private String action;
    private String value;
    private LocalDate expiration;
    private Long documentTypeId;

	public Long getId() {
		return id;
	}

	public void setId(Long value) {
		this.id = value;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String value) {
		this.action = value;
	}

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public LocalDate getExpiration() {
		return expiration;
	}
	
	public void setExpiration(LocalDate value) {
		this.expiration = value;
	}
	
	public Long getDocumentTypeId() {
		return documentTypeId;
	}
	
	public void setDocumentTypeId(Long value) {
		this.documentTypeId = value;
	}
}
