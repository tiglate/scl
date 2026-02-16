package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class FxSettlementStepDTO {

    @NotBlank
    private String currentStep;

    @NotNull
    private Integer fxTradeId;

    @Size(max = 255)
    private String comments;

    private MultipartFile fileUpload;

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(final String currentStep) {
        this.currentStep = currentStep;
    }

    public Integer getFxTradeId() {
        return fxTradeId;
    }

    public void setFxTradeId(final Integer fxTradeId) {
        this.fxTradeId = fxTradeId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    public MultipartFile getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(final MultipartFile fileUpload) {
        this.fileUpload = fileUpload;
    }
}
