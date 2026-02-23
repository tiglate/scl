package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FxSettlementStepDTO {

    @NotBlank
    private String currentStep;

    private Long fxTradeId;

    private Long fxSettlementId;

    @Size(max = 255)
    private String comments;

    private Long userId;

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(final String currentStep) {
        this.currentStep = currentStep;
    }

    public Long getFxTradeId() {
        return fxTradeId;
    }

    public void setFxTradeId(final Long fxTradeId) {
        this.fxTradeId = fxTradeId;
    }

    public Long getFxSettlementId() {
        return fxSettlementId;
    }

    public void setFxSettlementId(Long fxSettlementId) {
        this.fxSettlementId = fxSettlementId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
