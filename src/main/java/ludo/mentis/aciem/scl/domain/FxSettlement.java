package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import ludo.mentis.aciem.scl.model.FxSettlementFailure;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    @Column(length = 1000)
    private String comments;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FxSettlementFailure failureMotive;

    @Column(length = 1000)
    private String failureDetails;

    @Column(nullable = false)
    private LocalDateTime completedAt;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_completed_by")
    private User completedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "datetime2")
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "datetime2")
    private OffsetDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    public FxSettlementFailure getFailureMotive() {
        return failureMotive;
    }

    public void setFailureMotive(final FxSettlementFailure failureMotive) {
        this.failureMotive = failureMotive;
    }

    public String getFailureDetails() {
        return failureDetails;
    }

    public void setFailureDetails(final String failureDetails) {
        this.failureDetails = failureDetails;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(final LocalDateTime completedAt) {
        this.completedAt = completedAt;
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

    public User getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(final User completedBy) {
        this.completedBy = completedBy;
    }

    @SuppressWarnings("unused")
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @SuppressWarnings("unused")
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @SuppressWarnings("unused")
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @SuppressWarnings("unused")
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
