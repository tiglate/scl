package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import java.util.Set;
import ludo.mentis.aciem.scl.model.FxSettlementFailure;


@Entity
public class FxSettlement {

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
            name = "FxSettlementSteps",
            joinColumns = @JoinColumn(name = "fxSettlementId"),
            inverseJoinColumns = @JoinColumn(name = "fxSettlementStepId")
    )
    private Set<FxSettlementStep> steps;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private FxTrade trade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by_id")
    private User completedBy;

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

}
