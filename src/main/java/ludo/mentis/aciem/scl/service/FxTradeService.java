package ludo.mentis.aciem.scl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ludo.mentis.aciem.scl.domain.FxTradeView;
import ludo.mentis.aciem.scl.model.FxTradeDTO;
import ludo.mentis.aciem.scl.model.FxTradeSearchDTO;
import ludo.mentis.aciem.scl.util.ReferencedWarning;


public interface FxTradeService {

	Page<FxTradeView> findAll(FxTradeSearchDTO criteria, Pageable pageable);

    FxTradeDTO get(Long id);

    Long create(FxTradeDTO fxTradeDTO);

    void update(Long id, FxTradeDTO fxTradeDTO);

    void delete(Long id);

    ReferencedWarning getReferencedWarning(Long id);

}
