package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "tb_fx_settlement")
@EntityListeners(AuditingEntityListener.class)
public class FxSettlement {

    @Id
    @Column(name = "id_fx_settlement", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fx_trade", nullable = false)
    private FxTrade trade;

    @Column(name = "ins_flag", nullable = false)
    private boolean insFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ins_user_id")
    private User insUser;

    @Column(name = "ins_timestamp", columnDefinition = "datetime2")
    private LocalDateTime insTimestamp;

    @Column(name = "ins_comments", length = 255)
    private String insComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ins_file_id")
    private FileContent insFile;

    @Column(name = "g10_flag", nullable = false)
    private boolean g10Flag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "g10_user_id")
    private User g10User;

    @Column(name = "g10_timestamp", columnDefinition = "datetime2")
    private LocalDateTime g10Timestamp;

    @Column(name = "g10_comments", length = 255)
    private String g10Comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "g10_file_id")
    private FileContent g10File;

    @Column(name = "brl_flag", nullable = false)
    private boolean brlFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brl_user_id")
    private User brlUser;

    @Column(name = "brl_timestamp", columnDefinition = "datetime2")
    private LocalDateTime brlTimestamp;

    @Column(name = "brl_comments", length = 255)
    private String brlComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brl_file_id")
    private FileContent brlFile;

    @Column(name = "ion_flag", nullable = false)
    private boolean ionFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ion_user_id")
    private User ionUser;

    @Column(name = "ion_timestamp", columnDefinition = "datetime2")
    private LocalDateTime ionTimestamp;

    @Column(name = "ion_comments", length = 255)
    private String ionComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ion_file_id")
    private FileContent ionFile;

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

    public FxTrade getTrade() {
        return trade;
    }

    public void setTrade(final FxTrade trade) {
        this.trade = trade;
    }

    public boolean isInsFlag() {
        return insFlag;
    }

    public void setInsFlag(boolean insFlag) {
        this.insFlag = insFlag;
    }

    public User getInsUser() {
        return insUser;
    }

    public void setInsUser(User insUser) {
        this.insUser = insUser;
    }

    public LocalDateTime getInsTimestamp() {
        return insTimestamp;
    }

    public void setInsTimestamp(LocalDateTime insTimestamp) {
        this.insTimestamp = insTimestamp;
    }

    public String getInsComments() {
        return insComments;
    }

    public void setInsComments(String insComments) {
        this.insComments = insComments;
    }

    public FileContent getInsFile() {
        return insFile;
    }

    public void setInsFile(FileContent insFile) {
        this.insFile = insFile;
    }

    public boolean isG10Flag() {
        return g10Flag;
    }

    public void setG10Flag(boolean g10Flag) {
        this.g10Flag = g10Flag;
    }

    public User getG10User() {
        return g10User;
    }

    public void setG10User(User g10User) {
        this.g10User = g10User;
    }

    public LocalDateTime getG10Timestamp() {
        return g10Timestamp;
    }

    public void setG10Timestamp(LocalDateTime g10Timestamp) {
        this.g10Timestamp = g10Timestamp;
    }

    public String getG10Comments() {
        return g10Comments;
    }

    public void setG10Comments(String g10Comments) {
        this.g10Comments = g10Comments;
    }

    public FileContent getG10File() {
        return g10File;
    }

    public void setG10File(FileContent g10File) {
        this.g10File = g10File;
    }

    public boolean isBrlFlag() {
        return brlFlag;
    }

    public void setBrlFlag(boolean brlFlag) {
        this.brlFlag = brlFlag;
    }

    public User getBrlUser() {
        return brlUser;
    }

    public void setBrlUser(User brlUser) {
        this.brlUser = brlUser;
    }

    public LocalDateTime getBrlTimestamp() {
        return brlTimestamp;
    }

    public void setBrlTimestamp(LocalDateTime brlTimestamp) {
        this.brlTimestamp = brlTimestamp;
    }

    public String getBrlComments() {
        return brlComments;
    }

    public void setBrlComments(String brlComments) {
        this.brlComments = brlComments;
    }

    public FileContent getBrlFile() {
        return brlFile;
    }

    public void setBrlFile(FileContent brlFile) {
        this.brlFile = brlFile;
    }

    public boolean isIonFlag() {
        return ionFlag;
    }

    public void setIonFlag(boolean ionFlag) {
        this.ionFlag = ionFlag;
    }

    public User getIonUser() {
        return ionUser;
    }

    public void setIonUser(User ionUser) {
        this.ionUser = ionUser;
    }

    public LocalDateTime getIonTimestamp() {
        return ionTimestamp;
    }

    public void setIonTimestamp(LocalDateTime ionTimestamp) {
        this.ionTimestamp = ionTimestamp;
    }

    public String getIonComments() {
        return ionComments;
    }

    public void setIonComments(String ionComments) {
        this.ionComments = ionComments;
    }

    public FileContent getIonFile() {
        return ionFile;
    }

    public void setIonFile(FileContent ionFile) {
        this.ionFile = ionFile;
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
