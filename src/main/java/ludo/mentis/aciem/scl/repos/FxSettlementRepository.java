package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.FxSettlement;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FxSettlementRepository extends JpaRepository<FxSettlement, Long> {

    Page<FxSettlement> findAllById(Long id, Pageable pageable);

    FxSettlement findFirstByTrade(FxTrade fxTrade);

    FxSettlement findFirstByCompletedBy(User user);

}
