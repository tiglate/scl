package ludo.mentis.aciem.scl.service;

import java.util.HashSet;
import java.util.List;
import ludo.mentis.aciem.scl.domain.FxSettlement;
import ludo.mentis.aciem.scl.domain.FxSettlementStep;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.User;
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
    private final FxSettlementStepRepository fxSettlementStepRepository;
    private final FxTradeRepository fxTradeRepository;
    private final UserRepository userRepository;

    public FxSettlementServiceImpl(final FxSettlementRepository fxSettlementRepository,
            final FxSettlementStepRepository fxSettlementStepRepository,
            final FxTradeRepository fxTradeRepository, final UserRepository userRepository) {
        this.fxSettlementRepository = fxSettlementRepository;
        this.fxSettlementStepRepository = fxSettlementStepRepository;
        this.fxTradeRepository = fxTradeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<FxSettlementDTO> findAll(final String filter, final Pageable pageable) {
        Page<FxSettlement> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = fxSettlementRepository.findAllById(longFilter, pageable);
        } else {
            page = fxSettlementRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(fxSettlement -> mapToDTO(fxSettlement, new FxSettlementDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    @Override
    public FxSettlementDTO get(final Long id) {
        return fxSettlementRepository.findById(id)
                .map(fxSettlement -> mapToDTO(fxSettlement, new FxSettlementDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final FxSettlementDTO fxSettlementDTO) {
        final FxSettlement fxSettlement = new FxSettlement();
        mapToEntity(fxSettlementDTO, fxSettlement);
        return fxSettlementRepository.save(fxSettlement).getId();
    }

    @Override
    public void update(final Long id, final FxSettlementDTO fxSettlementDTO) {
        final FxSettlement fxSettlement = fxSettlementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(fxSettlementDTO, fxSettlement);
        fxSettlementRepository.save(fxSettlement);
    }

    @Override
    public void delete(final Long id) {
        fxSettlementRepository.deleteById(id);
    }

    private FxSettlementDTO mapToDTO(final FxSettlement fxSettlement,
            final FxSettlementDTO fxSettlementDTO) {
        fxSettlementDTO.setId(fxSettlement.getId());
        fxSettlementDTO.setComments(fxSettlement.getComments());
        fxSettlementDTO.setFailureMotive(fxSettlement.getFailureMotive());
        fxSettlementDTO.setFailureDetails(fxSettlement.getFailureDetails());
        fxSettlementDTO.setCompletedAt(fxSettlement.getCompletedAt());
        fxSettlementDTO.setSteps(fxSettlement.getSteps().stream()
                .map(fxSettlementStep -> fxSettlementStep.getId())
                .toList());
        fxSettlementDTO.setTrade(fxSettlement.getTrade() == null ? null : fxSettlement.getTrade().getId());
        fxSettlementDTO.setCompletedBy(fxSettlement.getCompletedBy() == null ? null : fxSettlement.getCompletedBy().getId());
        return fxSettlementDTO;
    }

    private FxSettlement mapToEntity(final FxSettlementDTO fxSettlementDTO,
            final FxSettlement fxSettlement) {
        fxSettlement.setComments(fxSettlementDTO.getComments());
        fxSettlement.setFailureMotive(fxSettlementDTO.getFailureMotive());
        fxSettlement.setFailureDetails(fxSettlementDTO.getFailureDetails());
        fxSettlement.setCompletedAt(fxSettlementDTO.getCompletedAt());
        final List<FxSettlementStep> steps = fxSettlementStepRepository.findAllById(
                fxSettlementDTO.getSteps() == null ? List.of() : fxSettlementDTO.getSteps());
        if (steps.size() != (fxSettlementDTO.getSteps() == null ? 0 : fxSettlementDTO.getSteps().size())) {
            throw new NotFoundException("one of steps not found");
        }
        fxSettlement.setSteps(new HashSet<>(steps));
        final FxTrade trade = fxSettlementDTO.getTrade() == null ? null : fxTradeRepository.findById(fxSettlementDTO.getTrade())
                .orElseThrow(() -> new NotFoundException("trade not found"));
        fxSettlement.setTrade(trade);
        final User completedBy = fxSettlementDTO.getCompletedBy() == null ? null : userRepository.findById(fxSettlementDTO.getCompletedBy())
                .orElseThrow(() -> new NotFoundException("completedBy not found"));
        fxSettlement.setCompletedBy(completedBy);
        return fxSettlement;
    }

}
