package ludo.mentis.aciem.scl.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ludo.mentis.aciem.scl.validation.FieldsNotEqual;


@FieldsNotEqual(
    field = "buyCurrencyId", 
    secondField = "sellCurrencyId", 
    message = "The bought and the sold currencies cannot be the same."
)
public class FxTradeDTO {

    private Long id;

    @Size(max = 255)
    private String tradeId;

    @NotNull
    private LocalDate tradeDate;

    @NotNull
    private LocalDate valueDate;

    @NotNull
    private Product product;

    private FxTradePurpose purpose;

    @NotNull
    private Long buyCurrencyId;

    @NotNull
    @Min(0)
    @Digits(integer = 20, fraction = 6)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal buyAmount;

    @NotNull
    private Long sellCurrencyId;

    @NotNull
    @Min(0)
    @Digits(integer = 20, fraction = 6)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal sellAmount;
    
    @NotNull
    @Min(0)
    @Digits(integer = 20, fraction = 10)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal exchangeRate;

    @NotNull
    private Long counterpartyId;

    @Size(max = 255)
    private String investorManager;

    @Size(max = 255)
    private String beneficiary;

    private Long updatedById;
    
    private String updatedByName;
    
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long value) {
		this.id = value;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String value) {
		this.tradeId = value;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product value) {
		this.product = value;
	}

	public FxTradePurpose getPurpose() {
		return purpose;
	}

	public void setPurpose(FxTradePurpose value) {
		this.purpose = value;
	}

	public Long getBuyCurrencyId() {
		return buyCurrencyId;
	}

	public void setBuyCurrencyId(Long value) {
		this.buyCurrencyId = value;
	}

	public BigDecimal getBuyAmount() {
		return buyAmount;
	}

	public void setBuyAmount(BigDecimal value) {
		this.buyAmount = value;
	}

	public Long getSellCurrencyId() {
		return sellCurrencyId;
	}

	public void setSellCurrencyId(Long value) {
		this.sellCurrencyId = value;
	}

	public BigDecimal getSellAmount() {
		return sellAmount;
	}

	public void setSellAmount(BigDecimal value) {
		this.sellAmount = value;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal value) {
		this.exchangeRate = value;
	}

	public Long getCounterpartyId() {
		return counterpartyId;
	}

	public void setCounterpartyId(Long value) {
		this.counterpartyId = value;
	}

	public String getInvestorManager() {
		return investorManager;
	}

	public void setInvestorManager(String value) {
		this.investorManager = value;
	}

	public String getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(String value) {
		this.beneficiary = value;
	}

	public Long getUpdatedById() {
		return updatedById;
	}

	public void setUpdatedById(Long value) {
		this.updatedById = value;
	}

	public String getUpdatedByName() {
		return updatedByName;
	}

	public void setUpdatedByName(String value) {
		this.updatedByName = value;
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
