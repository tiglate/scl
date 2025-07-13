package ludo.mentis.aciem.scl.model;

import java.time.LocalDate;

public class FxTradeSearchDTO {

	private Long id;
	private LocalDate tradeDate;
	private LocalDate valueDate;
	private Long buyCurrencyId;
	private Long sellCurrencyId;
	private String product;
	private String purpose;
	private Long counterpartyId;
	private String tradeId;

	public Long getId() {
		return id;
	}

	public void setId(Long value) {
		this.id = value;
	}

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate value) {
		this.tradeDate = value;
	}

	public LocalDate getValueDate() {
		return valueDate;
	}

	public void setValueDate(LocalDate value) {
		this.valueDate = value;
	}

	public Long getBuyCurrencyId() {
		return buyCurrencyId;
	}

	public void setBuyCurrencyId(Long value) {
		this.buyCurrencyId = value;
	}

	public Long getSellCurrencyId() {
		return sellCurrencyId;
	}

	public void setSellCurrencyId(Long value) {
		this.sellCurrencyId = value;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String value) {
		this.product = value;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String value) {
		this.purpose = value;
	}

	public Long getCounterpartyId() {
		return counterpartyId;
	}

	public void setCounterpartyId(Long value) {
		this.counterpartyId = value;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String value) {
		this.tradeId = value;
	}
}