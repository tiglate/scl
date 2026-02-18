package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.FxSettlementLog;
import ludo.mentis.aciem.scl.model.FxSettlementHistoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FxSettlementLogRepository extends JpaRepository<FxSettlementLog, Long> {

    @Query("SELECT new ludo.mentis.aciem.scl.model.FxSettlementHistoryDTO(l) FROM FxSettlementLogView l WHERE l.fxTradeId = :fxTradeId ORDER BY l.timestamp DESC")
    List<FxSettlementHistoryDTO> getHistoryByFxTradeId(@Param("fxTradeId") Long fxTradeId);

    @Query("SELECT new ludo.mentis.aciem.scl.model.FxSettlementHistoryDTO(l) FROM FxSettlementLogView l WHERE l.fxTradeId = :fxTradeId ORDER BY l.timestamp DESC LIMIT 1")
    Optional<FxSettlementHistoryDTO> findHistoryByTradeId(@Param("fxTradeId") Long fxTradeId);
}