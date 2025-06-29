package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CounterpartyRepository extends JpaRepository<Counterparty, Long> {

    Page<Counterparty> findAllById(Long id, Pageable pageable);

    Counterparty findFirstByUpdatedBy(User user);

}
