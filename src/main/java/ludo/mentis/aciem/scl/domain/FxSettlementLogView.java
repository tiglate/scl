package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "vw_fx_settlement_log")
public class FxSettlementLogView {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "fx_settlement_id")
    private Long fxSettlementId;

    @Column(name = "fx_trade_id")
    private Long fxTradeId;

    @Column(name = "user_name")
    private String userName;

    // Column name in the view is "timestamp" (alias), so map it explicitly.
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "action")
    private String action;

    @Column(name = "step")
    private String step;

    @Column(name = "comments")
    private String comments;

    @Column(name = "file_id", columnDefinition = "uniqueidentifier")
    private UUID fileId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFxSettlementId() {
        return fxSettlementId;
    }

    public void setFxSettlementId(Long fxSettlementId) {
        this.fxSettlementId = fxSettlementId;
    }

    public Long getFxTradeId() {
        return fxTradeId;
    }

    public void setFxTradeId(Long fxTradeId) {
        this.fxTradeId = fxTradeId;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}