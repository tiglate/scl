package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FxSettlementView;
import ludo.mentis.aciem.scl.model.FileContentDTO;
import ludo.mentis.aciem.scl.model.FxSettlementHistoryDTO;
import ludo.mentis.aciem.scl.model.FxSettlementStepDTO;
import ludo.mentis.aciem.scl.util.FileUploadException;
import ludo.mentis.aciem.scl.util.StepAlreadyTaken;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface FxSettlementService {

    List<FxSettlementView> findAllBySearchCriteria(LocalDate startDate, LocalDate endDate);

    LocalDate getLastTradeDate();

    void save(FxSettlementStepDTO dto, MultipartFile file) throws StepAlreadyTaken, FileUploadException;

    FxSettlementHistoryDTO viewStep(Long fxSettlementId, String step);

    List<FxSettlementHistoryDTO> getHistoryByFxSettlementId(Long id);

    void rollbackStep(Long fxSettlementId, String step, Long userId);

    FileContentDTO getFile(UUID id);
}
