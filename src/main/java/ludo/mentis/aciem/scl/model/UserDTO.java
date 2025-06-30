package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import ludo.mentis.aciem.scl.util.WebUtils;
import org.springframework.format.annotation.DateTimeFormat;


public class UserDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    @UserEmailUnique
    private String email;

    @NotNull
    @Size(max = 30)
    @Email(regexp = WebUtils.EMAIL_PATTERN)
    @UserUsernameUnique
    private String username;

    @NotNull
    @Size(max = 255)
    private String password;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private Gender gender;

    @NotNull
    private Boolean isActive;

    private UUID resetUID;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime resetStart;

    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Integer departmentId;

    private String departmentName;

    private List<Long> roles;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(final Gender gender) {
        this.gender = gender;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(final Boolean isActive) {
        this.isActive = isActive;
    }

    public UUID getResetUID() {
        return resetUID;
    }

    public void setResetUID(final UUID resetUID) {
        this.resetUID = resetUID;
    }

    public OffsetDateTime getResetStart() {
        return resetStart;
    }

    public void setResetStart(final OffsetDateTime resetStart) {
        this.resetStart = resetStart;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(final Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public List<Long> getRoles() {
        return roles;
    }

    public void setRoles(final List<Long> roles) {
        this.roles = roles;
    }

}
