package ludo.mentis.aciem.scl.repos;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.model.CurrencyDTO;


public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    @Query("SELECT new ludo.mentis.aciem.scl.model.CurrencyDTO(d.id, d.isoCode, d.bacenCode, d.name, d.endDate, d.createdAt, d.updatedAt) " +
            "FROM Currency d " +
            "WHERE (:name IS NULL OR d.name LIKE %:name%) " +
    		"AND (:isoCode IS NULL OR d.isoCode LIKE %:isoCode%) " +
            "AND (:bacenCode IS NULL OR d.bacenCode LIKE %:bacenCode%)")
    Page<CurrencyDTO> findAllBySearchCriteria(
            @Param("name") String name,
            @Param("isoCode") String isoCode,
            @Param("bacenCode") String bacenCode,
            Pageable pageable
    );

    boolean existsByIsoCodeIgnoreCase(String isoCode);

    boolean existsByBacenCodeIgnoreCase(String bacenCode);
    
    Optional<Currency> findByIsoCodeIgnoreCase(String name);

}
