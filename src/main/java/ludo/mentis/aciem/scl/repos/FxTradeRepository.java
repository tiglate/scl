package ludo.mentis.aciem.scl.repos;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.FxTradeDTO;


public interface FxTradeRepository extends JpaRepository<FxTrade, Long> {

    @Query("SELECT new ludo.mentis.aciem.scl.model.FxTradeDTO(d.id, d.tradeId, d.tradeDate, d.valueDate) " +
            "FROM FxTrade d " +
            "WHERE (:tradeId   IS NULL OR d.tradeId LIKE %:code%) " +
    		"AND   (:tradeDate IS NULL OR d.tradeDate = :tradeDate) " +
            "AND   (:valueDate IS NULL OR d.valueDate = :valueDate)")
    Page<FxTradeDTO> findAllBySearchCriteria(
            @Param("tradeId") String code,
            @Param("tradeDate") LocalDate tradeDate,
            @Param("valueDate") LocalDate valueDate,
            Pageable pageable
    );

    FxTrade findFirstByCounterparty(Counterparty counterparty);

    FxTrade findFirstByBuyCurrency(Currency currency);

    FxTrade findFirstBySellCurrency(Currency currency);

    FxTrade findFirstByUpdatedBy(User user);

}
