package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Page<Currency> findAllById(Long id, Pageable pageable);

    boolean existsByIsoCodeIgnoreCase(String isoCode);

    boolean existsByBacenCodeIgnoreCase(String bacenCode);

}
