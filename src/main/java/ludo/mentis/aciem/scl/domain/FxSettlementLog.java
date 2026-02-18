package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_fx_settlement_log")
public class FxSettlementLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fx_settlement_log", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fx_settlement", nullable = false)
    private FxSettlement fxSettlement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_file_content")
    private FileContent file;

    @Column(name = "step", nullable = false, length = 30)
    private String step;

    @Column(name = "flag", nullable = false)
    private boolean flag;

    @Column(name = "comments", length = 255)
    private String comments;

    @Column(name = "event_date", nullable = false, columnDefinition = "datetime2")
    private LocalDateTime eventDate;

    @PrePersist
    void prePersist() {
        if (eventDate == null) {
            eventDate = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public FxSettlement getFxSettlement() {
        return fxSettlement;
    }

    public void setFxSettlement(FxSettlement fxSettlement) {
        this.fxSettlement = fxSettlement;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FileContent getFile() {
        return file;
    }

    public void setFile(FileContent file) {
        this.file = file;
    }
}