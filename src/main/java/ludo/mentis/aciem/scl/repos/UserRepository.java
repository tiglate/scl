package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    User findByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = "roles")
    User findByEmailIgnoreCase(String username);

    User findFirstByDepartment(Department department);

    User findByResetUID(UUID resetUID);

    Page<User> findAllById(Long id, Pageable pageable);

    boolean existsByUsernameIgnoreCase(String username);

    User findFirstByRoles(Set<Role> roles);

    List<User> findAllByRoles(Set<Role> roles);

    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT new ludo.mentis.aciem.scl.model.UserDTO(u.id, u.name, u.email, u.gender, u.username, u.password, u.enabled, d.id, d.name, u.createdAt, u.updatedAt) " +
            "FROM User u " +
            "LEFT JOIN u.department d " +
            "WHERE (:username IS NULL OR u.username LIKE %:username%) " +
            "AND (:name IS NULL OR u.name LIKE %:name%) " +
            "AND (:department IS NULL OR d.id = :department) " +
            "AND (:enabled IS NULL OR u.enabled = :enabled) ")
    Page<UserDTO> findAllBySearchCriteria(
            @Param("username") String username,
            @Param("name") String name,
            @Param("department") Integer department,
            @Param("enabled") Boolean enabled,
            Pageable pageable
    );
}
