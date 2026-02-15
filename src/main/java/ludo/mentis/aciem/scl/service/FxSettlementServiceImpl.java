package ludo.mentis.aciem.scl.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import ludo.mentis.aciem.scl.domain.*;
import ludo.mentis.aciem.scl.model.FxSettlementDTO;
import ludo.mentis.aciem.scl.repos.FxSettlementRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementStepRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
}
