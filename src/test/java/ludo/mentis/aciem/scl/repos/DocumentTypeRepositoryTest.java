package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.DocumentType;
import ludo.mentis.aciem.scl.model.DocumentTypeDTO;
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
class DocumentTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    private DocumentType docType1;

    @BeforeEach
    void setUp() {
        docType1 = new DocumentType();
        docType1.setName("Passport");

        var docType2 = new DocumentType();
        docType2.setName("Driver License");

        entityManager.persist(docType1);
        entityManager.persist(docType2);
        entityManager.flush();
    }

    @Test
    void testSave() {
        DocumentType docType = new DocumentType();
        docType.setName("Identity Card");

        DocumentType savedDocType = documentTypeRepository.save(docType);

        assertThat(savedDocType.getId()).isNotNull();
        assertThat(savedDocType.getName()).isEqualTo("Identity Card");
    }

    @Test
    void testFindById() {
        Optional<DocumentType> foundDocType = documentTypeRepository.findById(docType1.getId());

        assertThat(foundDocType).isPresent();
        assertThat(foundDocType.get().getName()).isEqualTo("Passport");
    }

    @Test
    void testDelete() {
        documentTypeRepository.delete(docType1);
        Optional<DocumentType> deletedDocType = documentTypeRepository.findById(docType1.getId());

        assertThat(deletedDocType).isEmpty();
    }

    @Test
    void testFindAllBySearchCriteria_WithName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DocumentTypeDTO> result = documentTypeRepository.findAllBySearchCriteria("Pass", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Passport");
    }

    @Test
    void testExistsByNameIgnoreCase() {
        assertThat(documentTypeRepository.existsByNameIgnoreCase("passport")).isTrue();
        assertThat(documentTypeRepository.existsByNameIgnoreCase("PASSPORT")).isTrue();
        assertThat(documentTypeRepository.existsByNameIgnoreCase("Visa")).isFalse();
    }

    @Test
    void testFindByNameIgnoreCase() {
        Optional<DocumentType> result = documentTypeRepository.findByNameIgnoreCase("driver license");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Driver License");
    }
}
