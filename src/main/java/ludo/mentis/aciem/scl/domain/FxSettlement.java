package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;


@Audited
@Entity
@Table(name = "tb_fx_settlement")
@EntityListeners(AuditingEntityListener.class)
public class FxSettlement {

    @Id
    @Column(name = "id_fx_settlement", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "tb_fx_settlement_steps",
            joinColumns = @JoinColumn(name = "id_fx_settlement"),
            inverseJoinColumns = @JoinColumn(name = "id_fx_settlement_step")
    )
    private Set<FxSettlementStep> steps;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fx_trade", nullable = false)
    private FxTrade trade;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "datetime2")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "datetime2")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Set<FxSettlementStep> getSteps() {
        return steps;
    }

    public void setSteps(final Set<FxSettlementStep> steps) {
        this.steps = steps;
    }

    public FxTrade getTrade() {
        return trade;
    }

    public void setTrade(final FxTrade trade) {
        this.trade = trade;
    }

    @SuppressWarnings("unused")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @SuppressWarnings("unused")
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @SuppressWarnings("unused")
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @SuppressWarnings("unused")
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
