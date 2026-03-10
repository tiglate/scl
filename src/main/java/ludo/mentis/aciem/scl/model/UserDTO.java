package ludo.mentis.aciem.scl.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.validation.UserEmailUnique;
import ludo.mentis.aciem.scl.validation.UserUsernameUnique;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


public class UserDTO {

    private Long id;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(max = 255)
    private String name;
    
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(max = 255)
    @Email
    @UserEmailUnique
    private String email;
    
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Gender gender;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(max = 50)
    @UserUsernameUnique
    private String username;

    @NotBlank(groups = OnCreate.class)
    @Size(max = 255)
    private String password;

    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Boolean enabled;

    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Boolean useAD;

    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Long departmentId;

    private UUID resetUID;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime resetStart;

    private String departmentName;

    private List<Long> roles;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public UserDTO() {
    }

    public UserDTO(User user, Department department) {
        this.id = user.getId();
        this.name = user.getEmail();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.getEnabled();
        this.useAD = user.getUseAD();
        this.departmentId = department.getId();
        this.departmentName = department.getName();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final Boolean isActive) {
        this.enabled = isActive;
    }

    public Boolean getUseAD() {
        return useAD;
    }

    public void setUseAD(final Boolean useAD) {
        this.useAD = useAD;
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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(final Long departmentId) {
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
