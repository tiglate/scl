package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.model.CurrencyDTO;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CurrencyService {

    Page<CurrencyDTO> findAll(String filter, Pageable pageable);

    CurrencyDTO get(Long id);

    Long create(CurrencyDTO currencyDTO);

    void update(Long id, CurrencyDTO currencyDTO);

    void delete(Long id);

    boolean isoCodeExists(String isoCode);

    boolean bacenCodeExists(String bacenCode);

    ReferencedWarning getReferencedWarning(Long id);

}
