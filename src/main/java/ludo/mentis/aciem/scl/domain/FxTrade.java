package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import ludo.mentis.aciem.scl.model.FxTradePurpose;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;


@Audited
@Entity
@Table(name = "tb_fx_trade")
@EntityListeners(AuditingEntityListener.class)
public class FxTrade extends Trade {

    @Id
    @Column(name = "id_fx_trade", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(precision = 20, scale = 10)
    private BigDecimal exchangeRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_buy_currency")
    private Currency buyCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sell_currency")
    private Currency sellCurrency;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_updated_by")
    private User updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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
