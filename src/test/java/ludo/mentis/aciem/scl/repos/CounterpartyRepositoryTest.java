package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.CounterpartyDTO;
import ludo.mentis.aciem.scl.model.Gender;
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
class CounterpartyRepositoryTest {

    @Autowired
    private AuditTestConfig auditTestConfig;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CounterpartyRepository counterpartyRepository;

    private Counterparty counterparty1;
    private User user;

    @BeforeEach
    void setUp() {
        Department department = new Department();
        department.setName("Test Dept");
        entityManager.persist(department);

        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setName("Test User");
        user.setGender(Gender.MALE);
        user.setDepartment(department);
        user.setEnabled(true);
        entityManager.persist(user);
        entityManager.flush();

        auditTestConfig.setAuditor(user);

        counterparty1 = new Counterparty();
        counterparty1.setOriginId(100);
        counterparty1.setLongName("Counterparty One Long Name");
        counterparty1.setShortName("CP1");
        counterparty1.setIsActive(true);
        counterparty1.setUpdatedBy(user);
        entityManager.persist(counterparty1);

        Counterparty counterparty2 = new Counterparty();
        counterparty2.setOriginId(200);
        counterparty2.setLongName("Another Counterparty");
        counterparty2.setShortName("CP2");
        counterparty2.setIsActive(false);
        counterparty2.setUpdatedBy(user);
        entityManager.persist(counterparty2);

        entityManager.flush();
    }

    @Test
    void testSave() {
        Counterparty counterparty = new Counterparty();
        counterparty.setLongName("New Counterparty");
        counterparty.setIsActive(true);

        Counterparty saved = counterpartyRepository.save(counterparty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLongName()).isEqualTo("New Counterparty");
    }

    @Test
    void testFindById() {
        Optional<Counterparty> found = counterpartyRepository.findById(counterparty1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getLongName()).isEqualTo("Counterparty One Long Name");
    }

    @Test
    void testDelete() {
        counterpartyRepository.delete(counterparty1);
        Optional<Counterparty> deleted = counterpartyRepository.findById(counterparty1.getId());

        assertThat(deleted).isEmpty();
    }

    @Test
    void testFindAllBySearchCriteria_WithCode() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CounterpartyDTO> result = counterpartyRepository.findAllBySearchCriteria(100, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getOriginId()).isEqualTo(100);
    }

    @Test
    void testFindAllBySearchCriteria_WithShortName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CounterpartyDTO> result = counterpartyRepository.findAllBySearchCriteria(null, "CP1", null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getShortName()).isEqualTo("CP1");
    }

    @Test
    void testFindAllBySearchCriteria_WithLongName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CounterpartyDTO> result = counterpartyRepository.findAllBySearchCriteria(null, null, "One Long", null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLongName()).isEqualTo("Counterparty One Long Name");
    }

    @Test
    void testFindAllBySearchCriteria_WithIsActive() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CounterpartyDTO> result = counterpartyRepository.findAllBySearchCriteria(null, null, null, false, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsActive()).isFalse();
    }

    @Test
    void testFindFirstByUpdatedBy() {
        Counterparty result = counterpartyRepository.findFirstByUpdatedBy(user);

        assertThat(result).isNotNull();
        assertThat(result.getUpdatedBy().getId()).isEqualTo(user.getId());
    }
}
