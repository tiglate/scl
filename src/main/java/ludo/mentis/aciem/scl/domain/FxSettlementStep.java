package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import ludo.mentis.aciem.scl.model.FxSettlementStepType;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Audited
@Entity
@Table(name = "tb_fx_settlement_step")
@EntityListeners(AuditingEntityListener.class)
public class FxSettlementStep {

    @Id
    @Column(name = "id_fx_settlement_step", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FxSettlementStepType step;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evidence", unique = true)
    private FxStepEvidence evidence;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public FxSettlementStepType getStep() {
        return step;
    }

    public void setStep(final FxSettlementStepType step) {
        this.step = step;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(final LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public FxStepEvidence getEvidence() {
        return evidence;
    }

    public void setEvidence(final FxStepEvidence evidence) {
        this.evidence = evidence;
    }

}
