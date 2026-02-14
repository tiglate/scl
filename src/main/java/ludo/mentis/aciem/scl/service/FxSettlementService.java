package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FxSettlementView;
import ludo.mentis.aciem.scl.model.FxSettlementDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;


public interface FxSettlementService {

    List<FxSettlementView> findAllBySearchCriteria(LocalDate startDate, LocalDate endDate);

    Page<FxSettlementDTO> findAll(String filter, Pageable pageable);

    FxSettlementDTO get(Long id);

    Long create(FxSettlementDTO fxSettlementDTO);

    void update(Long id, FxSettlementDTO fxSettlementDTO);

    void delete(Long id);

}
