package ludo.mentis.aciem.scl.repos;

import jakarta.persistence.LockModeType;
import ludo.mentis.aciem.scl.domain.FxSettlement;
import ludo.mentis.aciem.scl.domain.FxSettlementView;
import ludo.mentis.aciem.scl.domain.FxTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface FxSettlementRepository extends JpaRepository<FxSettlement, Long> {

    @Query("SELECT v FROM FxSettlementView v WHERE v.tradeDate BETWEEN :startDate AND :endDate ORDER BY v.tradeDate DESC")
    List<FxSettlementView> findAllBySearchCriteria(LocalDate startDate, LocalDate endDate);

    FxSettlement findFirstByTrade(FxTrade fxTrade);

    @Query("SELECT MAX(t.tradeDate) FROM FxSettlementView t")
    LocalDate findLastTradeDate();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM FxSettlement s WHERE s.trade.id = :tradeId")
    Optional<FxSettlement> findFirstByTradeIdWithLock(@Param("tradeId") Long tradeId);
}
