package ludo.mentis.aciem.scl.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class FxTradeDTO {

    private Long id;

    @Size(max = 255)
    private String tradeId;

    private LocalDate tradeDate;

    private LocalDate valueDate;

    private Product product;

    @Digits(integer = 20, fraction = 6)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal buyAmount;

    @Digits(integer = 20, fraction = 6)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal sellAmount;

    @Size(max = 255)
    private String investorManager;

    @Size(max = 255)
    private String beneficiary;

    private FxTradePurpose purpose;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Digits(integer = 20, fraction = 10)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal exchangeRate;

    @NotNull
    private Long counterparty;

    private Long buyCurrency;

    private Long sellCurrency;

    private Long updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(final Long value) {
        this.id = value;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(final String value) {
        this.tradeId = value;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(final LocalDate value) {
        this.tradeDate = value;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(final LocalDate value) {
        this.valueDate = value;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(final Product value) {
        this.product = value;
    }

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(final BigDecimal value) {
        this.buyAmount = value;
    }

    public BigDecimal getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(final BigDecimal value) {
        this.sellAmount = value;
    }

    public String getInvestorManager() {
        return investorManager;
    }

    public void setInvestorManager(final String value) {
        this.investorManager = value;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(final String value) {
        this.beneficiary = value;
    }

    public FxTradePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(final FxTradePurpose value) {
        this.purpose = value;
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

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(final BigDecimal value) {
        this.exchangeRate = value;
    }

    public Long getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(final Long value) {
        this.counterparty = value;
    }

    public Long getBuyCurrency() {
        return buyCurrency;
    }

    public void setBuyCurrency(final Long value) {
        this.buyCurrency = value;
    }

    public Long getSellCurrency() {
        return sellCurrency;
    }

    public void setSellCurrency(final Long value) {
        this.sellCurrency = value;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final Long value) {
        this.updatedBy = value;
    }

}
