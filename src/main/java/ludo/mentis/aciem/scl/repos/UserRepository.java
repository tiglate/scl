package ludo.mentis.aciem.scl.repos;

import java.util.List;
import java.util.UUID;

import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    User findByUsernameIgnoreCase(String username);

    User findFirstByDepartment(Department department);

    User findByResetUID(UUID resetUID);

    Page<User> findAllById(Long id, Pageable pageable);

    boolean existsByUsernameIgnoreCase(String username);

    User findFirstByRoles(Role role);

    List<User> findAllByRoles(Role role);

    boolean existsByEmailIgnoreCase(String email);

}
