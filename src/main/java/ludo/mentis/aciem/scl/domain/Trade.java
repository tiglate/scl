package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import ludo.mentis.aciem.scl.model.Product;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.OffsetDateTime;


@MappedSuperclass
public abstract class Trade {

    @Column
    private String tradeId;

    @Column
    private LocalDate tradeDate;

    @Column
    private LocalDate valueDate;

    @Column
    @Enumerated(EnumType.STRING)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_counterparty", nullable = false)
    private Counterparty counterparty;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "datetime2")
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "datetime2")
    private OffsetDateTime updatedAt;

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

    public Counterparty getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(final Counterparty counterparty) {
        this.counterparty = counterparty;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @SuppressWarnings("unused")
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @SuppressWarnings("unused")
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
