package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;


public class PasswordResetCompleteRequest {

    @NotNull
    private UUID uid;

    @NotNull
    @Size(max = 255)
    private String newPassword;

    public UUID getUid() {
        return uid;
    }

    public void setUid(final UUID uid) {
        this.uid = uid;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }

}
