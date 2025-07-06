package ludo.mentis.aciem.scl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ludo.mentis.aciem.scl.model.CounterpartyDTO;
import ludo.mentis.aciem.scl.util.ReferencedWarning;


public interface CounterpartyService {

	Page<CounterpartyDTO> findAll(CounterpartyDTO searchDTO, Pageable pageable);

    CounterpartyDTO get(Long id);

    Long create(CounterpartyDTO counterpartyDTO);

    void update(Long id, CounterpartyDTO counterpartyDTO);

    void delete(Long id);

    ReferencedWarning getReferencedWarning(Long id);

}
