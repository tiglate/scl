package ludo.mentis.aciem.scl.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;


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
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

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

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(final String tradeId) {
        this.tradeId = tradeId;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(final LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(final LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(final Product product) {
        this.product = product;
    }

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(final BigDecimal buyAmount) {
        this.buyAmount = buyAmount;
    }

    public BigDecimal getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(final BigDecimal sellAmount) {
        this.sellAmount = sellAmount;
    }

    public String getInvestorManager() {
        return investorManager;
    }

    public void setInvestorManager(final String investorManager) {
        this.investorManager = investorManager;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(final String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public FxTradePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(final FxTradePurpose purpose) {
        this.purpose = purpose;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(final BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Long getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(final Long counterparty) {
        this.counterparty = counterparty;
    }

    public Long getBuyCurrency() {
        return buyCurrency;
    }

    public void setBuyCurrency(final Long buyCurrency) {
        this.buyCurrency = buyCurrency;
    }

    public Long getSellCurrency() {
        return sellCurrency;
    }

    public void setSellCurrency(final Long sellCurrency) {
        this.sellCurrency = sellCurrency;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final Long updatedBy) {
        this.updatedBy = updatedBy;
    }

}
