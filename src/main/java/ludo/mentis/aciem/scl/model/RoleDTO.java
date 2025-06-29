package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class RoleDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    @RoleCodeUnique
    private String code;

    @Size(max = 255)
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

}
