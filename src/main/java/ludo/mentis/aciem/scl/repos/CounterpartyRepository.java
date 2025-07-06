package ludo.mentis.aciem.scl.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.CounterpartyDTO;


public interface CounterpartyRepository extends JpaRepository<Counterparty, Long> {

    @Query("SELECT new ludo.mentis.aciem.scl.model.CounterpartyDTO(c.id, c.originId, c.longName, c.shortName, c.isActive, c.createdAt, c.updatedAt, u.id, u.name) " +
            "FROM Counterparty c " +
            "LEFT JOIN c.updatedBy u " +
            "WHERE (:code      IS NULL OR c.originId  =     :code) " +
            "AND   (:isActive  IS NULL OR c.isActive  =     :isActive) " +
            "AND   (:shortName IS NULL OR c.shortName LIKE %:shortName%) " +
            "AND   (:longName  IS NULL OR c.longName  LIKE %:longName%)")
    Page<CounterpartyDTO> findAllBySearchCriteria(
            @Param("code") Integer code,
            @Param("shortName") String shortName,
            @Param("longName") String longName,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    Counterparty findFirstByUpdatedBy(User user);

}
