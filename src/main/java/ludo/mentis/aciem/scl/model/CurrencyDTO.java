package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CurrencyDTO {

    private Long id;

    @NotNull
    @Size(max = 3)
    @CurrencyIsoCodeUnique
    private String isoCode;

    @NotNull
    @Size(max = 3)
    @CurrencyBacenCodeUnique
    private String bacenCode;

    @NotNull
    @Size(max = 255)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(final String isoCode) {
        this.isoCode = isoCode;
    }

    public String getBacenCode() {
        return bacenCode;
    }

    public void setBacenCode(final String bacenCode) {
        this.bacenCode = bacenCode;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
