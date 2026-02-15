package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FxSettlementView;
import ludo.mentis.aciem.scl.repos.FxSettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class FxSettlementServiceImpl implements FxSettlementService {

    private final FxSettlementRepository fxSettlementRepository;

    public FxSettlementServiceImpl(final FxSettlementRepository fxSettlementRepository) {
        this.fxSettlementRepository = fxSettlementRepository;
    }

    @Override
    public List<FxSettlementView> findAllBySearchCriteria(LocalDate startDate, LocalDate endDate) {
        return fxSettlementRepository.findAllBySearchCriteria(startDate, endDate);
    }

    @Override
    public LocalDate getLastTradeDate() {
        return fxSettlementRepository.findLastTradeDate();
    }
}
