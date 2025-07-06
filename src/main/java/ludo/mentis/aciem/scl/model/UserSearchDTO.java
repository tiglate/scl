package ludo.mentis.aciem.scl.model;

public class UserSearchDTO {

    private String username;
    private String name;
    private Integer department;
    private Boolean isActive;

    public String getUsername() {
        return username;
    }

    public void setUsername(String value) {
        this.username = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public Integer getDepartment() {
        return department;
    }

    public void setDepartment(Integer value) {
        this.department = value;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean value) {
        this.isActive = value;
    }
}