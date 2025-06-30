package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.time.LocalDateTime;

@Entity
@RevisionEntity(CustomRevisionListener.class)
@Table(name = "tb_revision")
public class CustomRevisionEntity {

    @Id
    @Column(name = "id_revision")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private Integer id;

    @RevisionTimestamp
    @Column(name = "event_timestamp", columnDefinition = "DATETIME2")
    private LocalDateTime timestamp;

    @Column(name = "id_user")
    private Long userId;

    @SuppressWarnings("unused")
    public Integer getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(Integer id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @SuppressWarnings("unused")
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @SuppressWarnings("unused")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
