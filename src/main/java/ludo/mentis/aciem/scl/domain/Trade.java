package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDate;
import ludo.mentis.aciem.scl.model.Product;


@MappedSuperclass
public abstract class Trade {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

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
    @JoinColumn(name = "counterparty_id", nullable = false)
    private Counterparty counterparty;

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

    public Counterparty getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(final Counterparty counterparty) {
        this.counterparty = counterparty;
    }

}
