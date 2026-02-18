package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.FxSettlementLog;
import ludo.mentis.aciem.scl.model.FxSettlementHistoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FxSettlementLogRepository extends JpaRepository<FxSettlementLog, Long> {

    @Query("SELECT new ludo.mentis.aciem.scl.model.FxSettlementHistoryDTO(l.user.name, l.eventDate, CASE WHEN l.flag THEN 'Set' ELSE 'Unset' END, l.step, l.comments, null, null) FROM FxSettlementLog l WHERE l.fxSettlement.trade.id = :fxTradeId ORDER BY l.eventDate DESC")
    List<FxSettlementHistoryDTO> getHistoryByFxTradeId(@Param("fxTradeId") Long fxTradeId);
}