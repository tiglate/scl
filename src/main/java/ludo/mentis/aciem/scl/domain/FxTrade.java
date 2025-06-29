package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import ludo.mentis.aciem.scl.model.FxTradePurpose;


@Entity
public class FxTrade extends Trade {

    @Column(precision = 20, scale = 6)
    private BigDecimal buyAmount;

    @Column(precision = 20, scale = 6)
    private BigDecimal sellAmount;

    @Column
    private String investorManager;

    @Column
    private String beneficiary;

    @Column
    @Enumerated(EnumType.STRING)
    private FxTradePurpose purpose;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(precision = 20, scale = 10)
    private BigDecimal exchangeRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_currency_id")
    private Currency buyCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sell_currency_id")
    private Currency sellCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(final BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Currency getBuyCurrency() {
        return buyCurrency;
    }

    public void setBuyCurrency(final Currency buyCurrency) {
        this.buyCurrency = buyCurrency;
    }

    public Currency getSellCurrency() {
        return sellCurrency;
    }

    public void setSellCurrency(final Currency sellCurrency) {
        this.sellCurrency = sellCurrency;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final User updatedBy) {
        this.updatedBy = updatedBy;
    }

}
