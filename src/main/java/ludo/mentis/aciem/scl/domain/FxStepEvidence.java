package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import ludo.mentis.aciem.scl.model.FileData;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;


@Audited
@Entity
@Table(name = "tb_fx_step_evidence")
public class FxStepEvidence {

    @Id
    @Column(name = "id_fx_step_evidence", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "nvarchar(max)", name = "\"file\"")
    @JdbcTypeCode(SqlTypes.JSON)
    private FileData file;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public FileData getFile() {
        return file;
    }

    public void setFile(final FileData file) {
        this.file = file;
    }

}
