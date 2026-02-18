package ludo.mentis.aciem.scl.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class FxSettlementHistoryDTO {

    private String userName;
    private LocalDateTime timestamp;
    private String action;
    private String step;
    private String comments;
    private UUID fileId;
    private String fileName;

    public FxSettlementHistoryDTO() {
    }

    public FxSettlementHistoryDTO(String userName, LocalDateTime timestamp, String action, String step,
                                  String comments, UUID fileId, String fileName) {
        this.userName = userName;
        this.timestamp = timestamp;
        this.action = action;
        this.step = step;
        this.comments = comments;
        this.fileId = fileId;
        this.fileName = fileName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
