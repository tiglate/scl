package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;


public class FxSettlementDTO {

    private Long id;

    @Size(max = 1000)
    private String comments;

    @NotNull
    private FxSettlementFailure failureMotive;

    @Size(max = 1000)
    private String failureDetails;

    @NotNull
    private LocalDateTime completedAt;

    private List<Long> steps;

    @NotNull
    private Long trade;

    private Long completedBy;

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

    public List<Long> getSteps() {
        return steps;
    }

    public void setSteps(final List<Long> steps) {
        this.steps = steps;
    }

    public Long getTrade() {
        return trade;
    }

    public void setTrade(final Long trade) {
        this.trade = trade;
    }

    public Long getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(final Long completedBy) {
        this.completedBy = completedBy;
    }

}
