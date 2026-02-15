package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.*;
import ludo.mentis.aciem.scl.model.FxTradeSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FxTradeRepository extends JpaRepository<FxTrade, Long> {

    /**
     * Finds a paginated list of FX trades using a DTO to encapsulate search criteria.
     * This method returns the full FxTradeView entity.
     *
     * @param criteria The DTO containing all optional search parameters.
     * @param pageable The pagination and sorting information.
     * @return A Page of FxTradeView entities matching the criteria.
     */
    @Query("SELECT v FROM FxTradeView v " +
            "WHERE (:#{#criteria.id}             IS NULL OR v.id             = :#{#criteria.id}) " +
            "AND   (:#{#criteria.tradeDate}      IS NULL OR v.tradeDate      = :#{#criteria.tradeDate}) " +
            "AND   (:#{#criteria.valueDate}      IS NULL OR v.valueDate      = :#{#criteria.valueDate}) " +
            "AND   (:#{#criteria.buyCurrencyId}  IS NULL OR v.buyCurrencyId  = :#{#criteria.buyCurrencyId}) " +
            "AND   (:#{#criteria.sellCurrencyId} IS NULL OR v.sellCurrencyId = :#{#criteria.sellCurrencyId}) " +
            "AND   (:#{#criteria.counterpartyId} IS NULL OR v.counterpartyId = :#{#criteria.counterpartyId}) " +
            "AND   (:#{#criteria.product}        IS NULL OR LOWER(v.product) LIKE LOWER(CONCAT('%', :#{#criteria.product}, '%'))) " +
            "AND   (:#{#criteria.purpose}        IS NULL OR LOWER(v.purpose) LIKE LOWER(CONCAT('%', :#{#criteria.purpose}, '%'))) " +
            "AND   (:#{#criteria.tradeId}        IS NULL OR LOWER(v.tradeId) LIKE LOWER(CONCAT('%', :#{#criteria.tradeId}, '%')))")
    Page<FxTradeView> findAllBySearchCriteria(
            @Param("criteria") FxTradeSearchDTO criteria,
            Pageable pageable
    );

    FxTrade findFirstByCounterparty(Counterparty counterparty);

    FxTrade findFirstByBuyCurrency(Currency currency);

    FxTrade findFirstBySellCurrency(Currency currency);

    FxTrade findFirstByUpdatedBy(User user);

}
