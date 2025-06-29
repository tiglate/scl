package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.model.FxSettlementDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FxSettlementService {

    Page<FxSettlementDTO> findAll(String filter, Pageable pageable);

    FxSettlementDTO get(Long id);

    Long create(FxSettlementDTO fxSettlementDTO);

    void update(Long id, FxSettlementDTO fxSettlementDTO);

    void delete(Long id);

}
