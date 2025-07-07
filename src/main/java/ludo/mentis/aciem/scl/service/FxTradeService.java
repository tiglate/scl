package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.model.FxTradeDTO;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FxTradeService {

	Page<FxTradeDTO> findAll(FxTradeDTO searchDTO, Pageable pageable);

    FxTradeDTO get(Long id);

    Long create(FxTradeDTO fxTradeDTO);

    void update(Long id, FxTradeDTO fxTradeDTO);

    void delete(Long id);

    ReferencedWarning getReferencedWarning(Long id);

}
