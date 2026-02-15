package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "vw_fx_settlement")
public class FxSettlementView {
    @Id
    @Column(name = "id_fx_settlement")
    private Long id;

    @Column(name = "id_fx_trade")
    private Long idFxTrade;

    @Column(name = "id_counterparty")
    private Long idCounterparty;

    @Column(name = "counterparty")
    private String counterparty;

    @Column(name = "investor_manager")
    private String investorManager;

    @Column(name = "contract_id")
    private String contractId;

    @Column(name = "currency")
    private String currency;

    @Column(name = "trade_type")
    private String tradeType;

    @Column(name = "g10_amount")
    private BigDecimal g10Amount;

    @Column(name = "brl_amount")
    private BigDecimal brlAmount;

    @Column(name = "trade_date")
    private LocalDate tradeDate;

    @Column(name = "beneficiary")
    private String beneficiary;

    @Column(name = "instruction")
    private boolean instruction;

    @Column(name = "g10")
    private boolean g10;

    @Column(name = "brl")
    private boolean brl;

    @Column(name = "ion")
    private boolean ion;

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getInvestorManager() {
        return investorManager;
    }

    public void setInvestorManager(String investorManager) {
        this.investorManager = investorManager;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getG10Amount() {
        return g10Amount;
    }

    public void setG10Amount(BigDecimal g10Amount) {
        this.g10Amount = g10Amount;
    }

    public BigDecimal getBrlAmount() {
        return brlAmount;
    }

    public void setBrlAmount(BigDecimal brlAmount) {
        this.brlAmount = brlAmount;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public boolean isInstruction() {
        return instruction;
    }

    public void setInstruction(boolean instruction) {
        this.instruction = instruction;
    }

    public boolean isG10() {
        return g10;
    }

    public void setG10(boolean g10) {
        this.g10 = g10;
    }

    public boolean isBrl() {
        return brl;
    }

    public void setBrl(boolean brl) {
        this.brl = brl;
    }

    public boolean isIon() {
        return ion;
    }

    public void setIon(boolean ion) {
        this.ion = ion;
    }

    public Long getIdCounterparty() {
        return idCounterparty;
    }

    public void setIdCounterparty(Long idCounterparty) {
        this.idCounterparty = idCounterparty;
    }

    public Long getIdFxTrade() {
        return idFxTrade;
    }

    public void setIdFxTrade(Long idFxTrade) {
        this.idFxTrade = idFxTrade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
