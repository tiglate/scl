package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.FxSettlementStep;
import ludo.mentis.aciem.scl.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FxSettlementStepRepository extends JpaRepository<FxSettlementStep, Long> {

    FxSettlementStep findFirstByUser(User user);

}
