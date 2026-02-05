package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.model.DepartmentDTO;
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
class DepartmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department1;

    @BeforeEach
    void setUp() {
        department1 = new Department();
        department1.setName("Human Resources");
        department1.setEmail("hr@example.com");

        Department department2 = new Department();
        department2.setName("Information Technology");
        department2.setEmail("it@example.com");

        entityManager.persist(department1);
        entityManager.persist(department2);
        entityManager.flush();
    }

    @Test
    void testSave() {
        Department department = new Department();
        department.setName("Finance");
        department.setEmail("finance@example.com");

        Department savedDepartment = departmentRepository.save(department);

        assertThat(savedDepartment.getId()).isNotNull();
        assertThat(savedDepartment.getName()).isEqualTo("Finance");
    }

    @Test
    void testFindById() {
        Optional<Department> foundDepartment = departmentRepository.findById(department1.getId());

        assertThat(foundDepartment).isPresent();
        assertThat(foundDepartment.get().getName()).isEqualTo("Human Resources");
    }

    @Test
    void testDelete() {
        departmentRepository.delete(department1);
        Optional<Department> deletedDepartment = departmentRepository.findById(department1.getId());

        assertThat(deletedDepartment).isEmpty();
    }

    @Test
    void testFindAllBySearchCriteria_WithName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DepartmentDTO> result = departmentRepository.findAllBySearchCriteria("Human", null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Human Resources");
    }

    @Test
    void testFindAllBySearchCriteria_WithEmail() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DepartmentDTO> result = departmentRepository.findAllBySearchCriteria(null, "it@example.com", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Information Technology");
    }

    @Test
    void testFindAllBySearchCriteria_WithBoth() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DepartmentDTO> result = departmentRepository.findAllBySearchCriteria("Resources", "hr@", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Human Resources");
    }

    @Test
    void testFindAllBySearchCriteria_WithNoMatches() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DepartmentDTO> result = departmentRepository.findAllBySearchCriteria("Marketing", null, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void testExistsByNameIgnoreCase() {
        assertThat(departmentRepository.existsByNameIgnoreCase("human resources")).isTrue();
        assertThat(departmentRepository.existsByNameIgnoreCase("HUMAN RESOURCES")).isTrue();
        assertThat(departmentRepository.existsByNameIgnoreCase("Marketing")).isFalse();
    }

    @Test
    void testFindByNameIgnoreCase() {
        Optional<Department> result = departmentRepository.findByNameIgnoreCase("information technology");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Information Technology");
    }
}
