package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FxSettlementView;
import ludo.mentis.aciem.scl.model.FxSettlementHistoryDTO;
import ludo.mentis.aciem.scl.model.FxSettlementStepDTO;
import ludo.mentis.aciem.scl.util.StepAlreadyTaken;

import java.time.LocalDate;
import java.util.List;


public interface FxSettlementService {

    List<FxSettlementView> findAllBySearchCriteria(LocalDate startDate, LocalDate endDate);

    LocalDate getLastTradeDate();

    void save(FxSettlementStepDTO dto) throws StepAlreadyTaken;

    FxSettlementHistoryDTO viewStep(Long fxSettlementId, String step);

    List<FxSettlementHistoryDTO> getHistoryByFxSettlementId(Long id);

    void rollbackStep(Long fxSettlementId, String step, Long userId);
}
