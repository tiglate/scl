package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ludo.mentis.aciem.scl.util.WebUtils;


public class AuthenticationRequest {

    @NotNull
    @Size(max = 30)
    @Email(regexp = WebUtils.EMAIL_PATTERN)
    private String username;

    @NotNull
    @Size(max = 72)
    private String password;

    @NotNull
    private Boolean rememberMe;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(final Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

}
