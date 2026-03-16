package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.*;
import ludo.mentis.aciem.scl.model.FxTradePurpose;
import ludo.mentis.aciem.scl.model.FxTradeSearchDTO;
import ludo.mentis.aciem.scl.model.Gender;
import ludo.mentis.aciem.scl.model.Product;
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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(AuditTestConfig.class)
@ImportAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FxTradeRepositoryTest {

    @Autowired
    private AuditTestConfig auditTestConfig;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FxTradeRepository fxTradeRepository;

    @Autowired
    private jakarta.persistence.EntityManager nativeEntityManager;

    private User user;
    private Currency currencyUSD;
    private Currency currencyBRL;
    private Counterparty counterparty;
    private FxTrade fxTrade1;

    @BeforeEach
    void setUp() {
        Department department = new Department();
        department.setName("Finance");
        entityManager.persist(department);

        user = new User();
        user.setEmail("trader@example.com");
        user.setUsername("trader");
        user.setPassword("pass");
        user.setName("John Trader");
        user.setGender(Gender.MALE);
        user.setDepartment(department);
        user.setEnabled(true);
        user.setUseAD(false);
        entityManager.persist(user);

        auditTestConfig.setAuditor(user);

        currencyUSD = new Currency();
        currencyUSD.setIsoCode("USD");
        currencyUSD.setBacenCode("220");
        currencyUSD.setName("US Dollar");
        entityManager.persist(currencyUSD);

        currencyBRL = new Currency();
        currencyBRL.setIsoCode("BRL");
        currencyBRL.setBacenCode("986");
        currencyBRL.setName("Real");
        entityManager.persist(currencyBRL);

        counterparty = new Counterparty();
        counterparty.setLongName("Bank of Test");
        counterparty.setIsActive(true);
        entityManager.persist(counterparty);

        fxTrade1 = new FxTrade();
        fxTrade1.setTradeId("T1001");
        fxTrade1.setTradeDate(LocalDate.now());
        fxTrade1.setValueDate(LocalDate.now().plusDays(2));
        fxTrade1.setProduct(Product.FX_SPOT);
        fxTrade1.setCounterparty(counterparty);
        fxTrade1.setBuyCurrency(currencyUSD);
        fxTrade1.setSellCurrency(currencyBRL);
        fxTrade1.setBuyAmount(new BigDecimal("1000.00"));
        fxTrade1.setSellAmount(new BigDecimal("5000.00"));
        fxTrade1.setExchangeRate(new BigDecimal("5.00"));
        fxTrade1.setPurpose(FxTradePurpose.EQ);
        fxTrade1.setUpdatedBy(user);
        entityManager.persist(fxTrade1);

        entityManager.flush();

        // Hibernate creates a table for FxTradeView because it's an @Entity.
        // We manually populate it since the real database view isn't available without Flyway.
        nativeEntityManager.createNativeQuery(
                "INSERT INTO vw_fx_trade (id_fx_trade, trade_id, id_buy_currency, id_sell_currency, id_counterparty, id_updated_by, " +
                "buy_currency_iso_code, sell_currency_iso_code, counterparty_short_name, counterparty_long_name, updated_by_name, " +
                "trade_date, value_date, product, buy_amount, sell_amount, exchange_rate, purpose) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .setParameter(1, fxTrade1.getId())
                .setParameter(2, fxTrade1.getTradeId())
                .setParameter(3, currencyUSD.getId())
                .setParameter(4, currencyBRL.getId())
                .setParameter(5, counterparty.getId())
                .setParameter(6, user.getId())
                .setParameter(7, currencyUSD.getIsoCode())
                .setParameter(8, currencyBRL.getIsoCode())
                .setParameter(9, counterparty.getShortName())
                .setParameter(10, counterparty.getLongName())
                .setParameter(11, user.getName())
                .setParameter(12, fxTrade1.getTradeDate())
                .setParameter(13, fxTrade1.getValueDate())
                .setParameter(14, fxTrade1.getProduct().name())
                .setParameter(15, fxTrade1.getBuyAmount())
                .setParameter(16, fxTrade1.getSellAmount())
                .setParameter(17, fxTrade1.getExchangeRate())
                .setParameter(18, fxTrade1.getPurpose().name())
                .executeUpdate();
    }

    @Test
    void testSave() {
        FxTrade fxTrade = new FxTrade();
        fxTrade.setTradeId("T1002");
        fxTrade.setCounterparty(counterparty);
        fxTrade.setBuyCurrency(currencyBRL);
        fxTrade.setSellCurrency(currencyUSD);
        fxTrade.setProduct(Product.FX_FORWARD);

        FxTrade saved = fxTradeRepository.save(fxTrade);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTradeId()).isEqualTo("T1002");
    }

    @Test
    void testFindById() {
        Optional<FxTrade> found = fxTradeRepository.findById(fxTrade1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTradeId()).isEqualTo("T1001");
    }

    @Test
    void testDelete() {
        fxTradeRepository.delete(fxTrade1);
        Optional<FxTrade> deleted = fxTradeRepository.findById(fxTrade1.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void testFindFirstByCounterparty() {
        FxTrade result = fxTradeRepository.findFirstByCounterparty(counterparty);
        assertThat(result).isNotNull();
        assertThat(result.getTradeId()).isEqualTo("T1001");
    }

    @Test
    void testFindFirstByBuyCurrency() {
        FxTrade result = fxTradeRepository.findFirstByBuyCurrency(currencyUSD);
        assertThat(result).isNotNull();
        assertThat(result.getTradeId()).isEqualTo("T1001");
    }

    @Test
    void testFindFirstBySellCurrency() {
        FxTrade result = fxTradeRepository.findFirstBySellCurrency(currencyBRL);
        assertThat(result).isNotNull();
        assertThat(result.getTradeId()).isEqualTo("T1001");
    }

    @Test
    void testFindFirstByUpdatedBy() {
        FxTrade result = fxTradeRepository.findFirstByUpdatedBy(user);
        assertThat(result).isNotNull();
        assertThat(result.getTradeId()).isEqualTo("T1001");
    }

    @Test
    void testFindAllBySearchCriteria_WithTradeId() {
        FxTradeSearchDTO criteria = new FxTradeSearchDTO();
        criteria.setTradeId("T1001");
        Page<FxTradeView> result = fxTradeRepository.findAllBySearchCriteria(criteria, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTradeId()).isEqualTo("T1001");
    }

    @Test
    void testFindAllBySearchCriteria_WithCounterparty() {
        FxTradeSearchDTO criteria = new FxTradeSearchDTO();
        criteria.setCounterpartyId(counterparty.getId());
        Page<FxTradeView> result = fxTradeRepository.findAllBySearchCriteria(criteria, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCounterpartyId()).isEqualTo(counterparty.getId());
    }

    @Test
    void testFindAllBySearchCriteria_WithBuyCurrency() {
        FxTradeSearchDTO criteria = new FxTradeSearchDTO();
        criteria.setBuyCurrencyId(currencyUSD.getId());
        Page<FxTradeView> result = fxTradeRepository.findAllBySearchCriteria(criteria, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBuyCurrencyId()).isEqualTo(currencyUSD.getId());
    }

    @Test
    void testFindAllBySearchCriteria_WithSellCurrency() {
        FxTradeSearchDTO criteria = new FxTradeSearchDTO();
        criteria.setSellCurrencyId(currencyBRL.getId());
        Page<FxTradeView> result = fxTradeRepository.findAllBySearchCriteria(criteria, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSellCurrencyId()).isEqualTo(currencyBRL.getId());
    }

    @Test
    void testFindAllBySearchCriteria_WithProduct() {
        FxTradeSearchDTO criteria = new FxTradeSearchDTO();
        criteria.setProduct(Product.FX_SPOT.name());
        Page<FxTradeView> result = fxTradeRepository.findAllBySearchCriteria(criteria, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getProduct()).isEqualTo(Product.FX_SPOT);
    }

    @Test
    void testFindAllBySearchCriteria_WithPurpose() {
        FxTradeSearchDTO criteria = new FxTradeSearchDTO();
        criteria.setPurpose(FxTradePurpose.EQ.name());
        Page<FxTradeView> result = fxTradeRepository.findAllBySearchCriteria(criteria, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPurpose()).isEqualTo(FxTradePurpose.EQ);
    }

    @Test
    void testFindAllBySearchCriteria_WithTradeDate() {
        FxTradeSearchDTO criteria = new FxTradeSearchDTO();
        criteria.setTradeDate(fxTrade1.getTradeDate());
        Page<FxTradeView> result = fxTradeRepository.findAllBySearchCriteria(criteria, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTradeDate()).isEqualTo(fxTrade1.getTradeDate());
    }

    @Test
    void testFindAllBySearchCriteria_WithValueDate() {
        FxTradeSearchDTO criteria = new FxTradeSearchDTO();
        criteria.setValueDate(fxTrade1.getValueDate());
        Page<FxTradeView> result = fxTradeRepository.findAllBySearchCriteria(criteria, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getValueDate()).isEqualTo(fxTrade1.getValueDate());
    }
}
