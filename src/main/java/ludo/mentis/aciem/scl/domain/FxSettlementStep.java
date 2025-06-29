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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import ludo.mentis.aciem.scl.model.FxSettlementStepType;


@Entity
public class FxSettlementStep {

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FxSettlementStepType step;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", unique = true)
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
