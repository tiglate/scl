package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.model.RoleDTO;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(AuditTestConfig.class)
@ImportAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    private Role roleAdmin;

    @BeforeEach
    void setUp() {
        roleAdmin = new Role();
        roleAdmin.setCode("ROLE_ADMIN");
        roleAdmin.setDescription("Administrator");

        var roleUser = new Role();
        roleUser.setCode("ROLE_USER");
        roleUser.setDescription("Standard User");

        entityManager.persist(roleAdmin);
        entityManager.persist(roleUser);
        entityManager.flush();
    }

    @Test
    void testSave() {
        Role role = new Role();
        role.setCode("ROLE_MANAGER");
        role.setDescription("Manager");

        Role savedRole = roleRepository.save(role);

        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getCode()).isEqualTo("ROLE_MANAGER");
    }

    @Test
    void testFindById() {
        Optional<Role> foundRole = roleRepository.findById(roleAdmin.getId());

        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getCode()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void testDelete() {
        roleRepository.delete(roleAdmin);
        Optional<Role> deletedRole = roleRepository.findById(roleAdmin.getId());

        assertThat(deletedRole).isEmpty();
    }

    @Test
    void testFindAllBySearchCriteria_WithCode() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoleDTO> result = roleRepository.findAllBySearchCriteria("ADMIN", null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void testFindAllBySearchCriteria_WithDescription() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoleDTO> result = roleRepository.findAllBySearchCriteria(null, "Standard", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo("ROLE_USER");
    }

    @Test
    void testFindByCode() {
        Role result = roleRepository.findByCode("ROLE_USER");

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("ROLE_USER");
    }

    @Test
    void testFindAllById() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> result = roleRepository.findAllById(roleAdmin.getId(), pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void testExistsByCodeIgnoreCase() {
        assertThat(roleRepository.existsByCodeIgnoreCase("role_admin")).isTrue();
        assertThat(roleRepository.existsByCodeIgnoreCase("ROLE_ADMIN")).isTrue();
        assertThat(roleRepository.existsByCodeIgnoreCase("ROLE_GUEST")).isFalse();
    }
}
