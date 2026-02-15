package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.FxSettlement;
import ludo.mentis.aciem.scl.domain.FxSettlementView;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface FxSettlementRepository extends JpaRepository<FxSettlement, Long> {

    @Query("SELECT v FROM FxSettlementView v WHERE v.tradeDate BETWEEN :startDate AND :endDate")
    List<FxSettlementView> findAllBySearchCriteria(LocalDate startDate, LocalDate endDate);

    FxSettlement findFirstByTrade(FxTrade fxTrade);

    FxSettlement findFirstByCompletedBy(User user);

    @Query("SELECT MAX(t.tradeDate) FROM FxSettlementView t")
    LocalDate findLastTradeDate();
}
