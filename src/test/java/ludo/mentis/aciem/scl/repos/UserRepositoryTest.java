package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.Gender;
import ludo.mentis.aciem.scl.model.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(AuditTestConfig.class)
@ImportAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Department department;
    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setName("IT");
        department.setEmail("it@example.com");
        entityManager.persist(department);

        role = new Role();
        role.setCode("ROLE_USER");
        role.setDescription("User role");
        entityManager.persist(role);

        user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setGender(Gender.MALE);
        user.setEnabled(true);
        user.setDepartment(department);
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        user.setResetUID(UUID.randomUUID());

        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void testSave() {
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane.doe@example.com");
        newUser.setUsername("janedoe");
        newUser.setPassword("password");
        newUser.setGender(Gender.FEMALE);
        newUser.setEnabled(true);
        newUser.setDepartment(department);

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("janedoe");
    }

    @Test
    void testFindByUsernameIgnoreCase() {
        User foundUser = userRepository.findByUsernameIgnoreCase("JOHNDOE");
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("johndoe");
        assertThat(foundUser.getRoles()).contains(role);
    }

    @Test
    void testFindFirstByDepartment() {
        User foundUser = userRepository.findFirstByDepartment(department);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindByResetUID() {
        User foundUser = userRepository.findByResetUID(user.getResetUID());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindAllById() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> result = userRepository.findAllById(user.getId(), pageable);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void testExistsByUsernameIgnoreCase() {
        assertThat(userRepository.existsByUsernameIgnoreCase("johndoe")).isTrue();
        assertThat(userRepository.existsByUsernameIgnoreCase("unknown")).isFalse();
    }

    @Test
    void testExistsByEmailIgnoreCase() {
        assertThat(userRepository.existsByEmailIgnoreCase("john.doe@example.com")).isTrue();
        assertThat(userRepository.existsByEmailIgnoreCase("unknown@example.com")).isFalse();
    }

    @Test
    void testFindFirstByRoles() {
        User foundUser = userRepository.findFirstByRoles(Collections.singleton(role));
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindAllBySearchCriteria() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> result = userRepository.findAllBySearchCriteria("john", "Doe", department.getId().intValue(), true, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("johndoe");
    }
}
