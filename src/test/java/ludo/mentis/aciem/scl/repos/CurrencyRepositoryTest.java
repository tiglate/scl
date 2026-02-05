package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.model.CurrencyDTO;
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
class CurrencyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CurrencyRepository currencyRepository;

    private Currency currencyBRL;

    @BeforeEach
    void setUp() {
        currencyBRL = new Currency();
        currencyBRL.setIsoCode("BRL");
        currencyBRL.setBacenCode("986");
        currencyBRL.setName("Real");
        currencyBRL.setEndDate(null);

        var currencyUSD = new Currency();
        currencyUSD.setIsoCode("USD");
        currencyUSD.setBacenCode("220");
        currencyUSD.setName("US Dollar");
        currencyUSD.setEndDate(null);

        entityManager.persist(currencyBRL);
        entityManager.persist(currencyUSD);
        entityManager.flush();
    }

    @Test
    void testSave() {
        Currency currency = new Currency();
        currency.setIsoCode("EUR");
        currency.setBacenCode("978");
        currency.setName("Euro");

        Currency savedCurrency = currencyRepository.save(currency);

        assertThat(savedCurrency.getId()).isNotNull();
        assertThat(savedCurrency.getIsoCode()).isEqualTo("EUR");
    }

    @Test
    void testFindById() {
        Optional<Currency> foundCurrency = currencyRepository.findById(currencyBRL.getId());

        assertThat(foundCurrency).isPresent();
        assertThat(foundCurrency.get().getIsoCode()).isEqualTo("BRL");
    }

    @Test
    void testDelete() {
        currencyRepository.delete(currencyBRL);
        Optional<Currency> deletedCurrency = currencyRepository.findById(currencyBRL.getId());

        assertThat(deletedCurrency).isEmpty();
    }

    @Test
    void testFindAllBySearchCriteria_WithName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CurrencyDTO> result = currencyRepository.findAllBySearchCriteria("Real", null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Real");
    }

    @Test
    void testFindAllBySearchCriteria_WithIsoCode() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CurrencyDTO> result = currencyRepository.findAllBySearchCriteria(null, "USD", null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsoCode()).isEqualTo("USD");
    }

    @Test
    void testFindAllBySearchCriteria_WithBacenCode() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CurrencyDTO> result = currencyRepository.findAllBySearchCriteria(null, null, "986", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsoCode()).isEqualTo("BRL");
    }

    @Test
    void testExistsByIsoCodeIgnoreCase() {
        assertThat(currencyRepository.existsByIsoCodeIgnoreCase("brl")).isTrue();
        assertThat(currencyRepository.existsByIsoCodeIgnoreCase("USD")).isTrue();
        assertThat(currencyRepository.existsByIsoCodeIgnoreCase("EUR")).isFalse();
    }

    @Test
    void testExistsByBacenCodeIgnoreCase() {
        assertThat(currencyRepository.existsByBacenCodeIgnoreCase("986")).isTrue();
        assertThat(currencyRepository.existsByBacenCodeIgnoreCase("220")).isTrue();
        assertThat(currencyRepository.existsByBacenCodeIgnoreCase("978")).isFalse();
    }

    @Test
    void testFindByIsoCodeIgnoreCase() {
        Optional<Currency> result = currencyRepository.findByIsoCodeIgnoreCase("usd");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("US Dollar");
    }
}
