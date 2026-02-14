package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.FxSettlement;
import ludo.mentis.aciem.scl.domain.FxSettlementView;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface FxSettlementRepository extends JpaRepository<FxSettlement, Long> {

    @Query("SELECT v FROM FxSettlementView v")
    List<FxSettlementView> findAllBySearchCriteria();

    Page<FxSettlement> findAllById(Long id, Pageable pageable);

    FxSettlement findFirstByTrade(FxTrade fxTrade);

    FxSettlement findFirstByCompletedBy(User user);

}
