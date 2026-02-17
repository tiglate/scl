package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FxSettlement;
import ludo.mentis.aciem.scl.domain.FxSettlementStep;
import ludo.mentis.aciem.scl.domain.FxSettlementView;
import ludo.mentis.aciem.scl.model.FxSettlementStepDTO;
import ludo.mentis.aciem.scl.model.FxSettlementStepType;
import ludo.mentis.aciem.scl.repos.FxSettlementRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementStepRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.StepAlreadyTaken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class FxSettlementServiceImpl implements FxSettlementService {

    private final FxSettlementRepository fxSettlementRepository;
    private final FxTradeRepository fxTradeRepository;
    private final UserRepository userRepository;
    private final FxSettlementStepRepository fxSettlementStepRepository;

    public FxSettlementServiceImpl(final FxSettlementRepository fxSettlementRepository,
                                   final FxTradeRepository fxTradeRepository,
                                   final UserRepository userRepository,
                                   final FxSettlementStepRepository fxSettlementStepRepository) {
        this.fxSettlementRepository = fxSettlementRepository;
        this.fxTradeRepository = fxTradeRepository;
        this.userRepository = userRepository;
        this.fxSettlementStepRepository = fxSettlementStepRepository;
    }

    @Override
    public List<FxSettlementView> findAllBySearchCriteria(LocalDate startDate, LocalDate endDate) {
        return fxSettlementRepository.findAllBySearchCriteria(startDate, endDate);
    }

    @Override
    public LocalDate getLastTradeDate() {
        return fxSettlementRepository.findLastTradeDate();
    }

    @Override
    public void save(FxSettlementStepDTO dto) throws StepAlreadyTaken {
        if (dto == null) {
            throw new IllegalArgumentException("dto must not be null");
        }
        if (dto.getFxTradeId() == null) {
            throw new IllegalArgumentException("fxTradeId must not be null");
        }
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (dto.getCurrentStep() == null) {
            throw new IllegalArgumentException("currentStep must not be null");
        }

        final var settlement = fxSettlementRepository.findFirstByTradeIdWithLock(dto.getFxTradeId())
                .orElseGet(FxSettlement::new);

        final var stepType = FxSettlementStepType.translateStringToEnum(dto.getCurrentStep());
        if (stepType == null) {
            throw new IllegalArgumentException("Unknown step type: " + dto.getCurrentStep());
        }

        if (settlement.getSteps() == null) {
            settlement.setSteps(new HashSet<>());
        }

        final var steps = settlement.getSteps();
        final boolean alreadyTaken = steps.stream()
                .anyMatch(step -> stepType.equals(step.getStep()));

        if (alreadyTaken) {
            throw new StepAlreadyTaken("Step " + stepType + " already taken for trade #" + dto.getFxTradeId() + ".");
        }

        if (settlement.getTrade() == null) {
            settlement.setTrade(fxTradeRepository.getReferenceById(dto.getFxTradeId()));
        }

        final var newStep = new FxSettlementStep();
        newStep.setStep(stepType);
        newStep.setEventDate(LocalDateTime.now());
        newStep.setComments(dto.getComments());
        newStep.setUser(userRepository.getReferenceById(dto.getUserId()));

        final var savedNewStep = this.fxSettlementStepRepository.save(newStep);

        steps.add(savedNewStep);

        fxSettlementRepository.save(settlement);
    }
}
