package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FxTradeRepository extends JpaRepository<FxTrade, Long> {

    Page<FxTrade> findAllById(Long id, Pageable pageable);

    FxTrade findFirstByCounterparty(Counterparty counterparty);

    FxTrade findFirstByBuyCurrency(Currency currency);

    FxTrade findFirstBySellCurrency(Currency currency);

    FxTrade findFirstByUpdatedBy(User user);

}
