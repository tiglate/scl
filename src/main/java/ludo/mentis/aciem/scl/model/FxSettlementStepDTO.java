package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class FxSettlementStepDTO {

    @NotBlank
    private String currentStep;

    @NotNull
    private Long fxTradeId;

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
