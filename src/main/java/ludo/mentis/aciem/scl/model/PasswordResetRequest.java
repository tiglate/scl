package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ludo.mentis.aciem.scl.util.WebUtils;
import ludo.mentis.aciem.scl.validation.PasswordResetRequestUsernameExists;


public class PasswordResetRequest {

    @NotNull
    @Size(max = 30)
    @Email(regexp = WebUtils.EMAIL_PATTERN)
    @PasswordResetRequestUsernameExists
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

}
