package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FileContent;
import ludo.mentis.aciem.scl.domain.FxSettlement;
import ludo.mentis.aciem.scl.domain.FxSettlementLog;
import ludo.mentis.aciem.scl.domain.FxSettlementView;
import ludo.mentis.aciem.scl.model.FileContentDTO;
import ludo.mentis.aciem.scl.model.FxSettlementHistoryDTO;
import ludo.mentis.aciem.scl.model.FxSettlementStepDTO;
import ludo.mentis.aciem.scl.repos.FxSettlementLogRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.FileUploadException;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.SecurityViolationException;
import ludo.mentis.aciem.scl.util.StepAlreadyTaken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Transactional(rollbackFor = Exception.class)
public class FxSettlementServiceImpl implements FxSettlementService {

    private final FxSettlementRepository fxSettlementRepository;
    private final FxTradeRepository fxTradeRepository;
    private final UserRepository userRepository;
    private final FxSettlementLogRepository fxSettlementLogRepository;
    private final FileDataService fileDataService;

    public FxSettlementServiceImpl(final FxSettlementRepository fxSettlementRepository,
                                   final FxTradeRepository fxTradeRepository,
                                   final UserRepository userRepository,
                                   final FxSettlementLogRepository fxSettlementLogRepository,
                                   final FileDataService fileDataService) {
        this.fxSettlementRepository = fxSettlementRepository;
        this.fxTradeRepository = fxTradeRepository;
        this.userRepository = userRepository;
        this.fxSettlementLogRepository = fxSettlementLogRepository;
        this.fileDataService = fileDataService;
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
    public void save(FxSettlementStepDTO dto, MultipartFile file) throws StepAlreadyTaken, FileUploadException {
        if (dto == null) {
            throw new IllegalArgumentException("dto must not be null");
        }
        if (dto.getFxTradeId() == null) {
            throw new IllegalArgumentException("fxTradeId must not be null");
        }
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (dto.getCurrentStep() == null || dto.getCurrentStep().trim().isBlank()) {
            throw new IllegalArgumentException("currentStep must not be null or blank");
        }

        final var settlement = fxSettlementRepository.findFirstByTradeIdWithLock(dto.getFxTradeId())
                .orElseGet(FxSettlement::new);

        final var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found for ID: " + dto.getUserId()));

        final var trade = fxTradeRepository.findById(dto.getFxTradeId())
                .orElseThrow(() -> new NotFoundException("Trade not found for ID: " + dto.getFxTradeId()));

        if (settlement.getTrade() == null) {
            settlement.setTrade(trade);
        }

        FileContent fileContent = null;
        if (file != null) {
            fileContent = fileDataService.create(file);
        }

        final var now = LocalDateTime.now();

        final var step = dto.getCurrentStep().trim().toLowerCase();

        switch (step) {
            case "ins":
                if (settlement.isInsFlag()) {
                    throw new StepAlreadyTaken("Step already taken for trade #" + trade.getTradeId() + ".");
                }
                settlement.setInsFlag(true);
                settlement.setInsComments(dto.getComments());
                settlement.setInsUser(user);
                settlement.setInsTimestamp(now);
                settlement.setInsFile(fileContent);
                break;
            case "g10":
                if (settlement.isG10Flag()) {
                    throw new StepAlreadyTaken("Step already taken for trade #" + trade.getTradeId() + ".");
                }
                settlement.setG10Flag(true);
                settlement.setG10Comments(dto.getComments());
                settlement.setG10User(user);
                settlement.setG10Timestamp(now);
                settlement.setG10File(fileContent);
                break;
            case "brl":
                if (settlement.isBrlFlag()) {
                    throw new StepAlreadyTaken("Step already taken for trade #" + trade.getTradeId() + ".");
                }
                settlement.setBrlFlag(true);
                settlement.setBrlComments(dto.getComments());
                settlement.setBrlUser(user);
                settlement.setBrlTimestamp(now);
                settlement.setBrlFile(fileContent);
                break;
            case "ion":
                if (settlement.isIonFlag()) {
                    throw new StepAlreadyTaken("Step already taken for trade #" + trade.getTradeId() + ".");
                }
                settlement.setIonFlag(true);
                settlement.setIonComments(dto.getComments());
                settlement.setIonUser(user);
                settlement.setIonTimestamp(now);
                settlement.setIonFile(fileContent);
                break;
            default:
                throw new IllegalArgumentException("Invalid step: " + step);
        }

        fxSettlementRepository.save(settlement);

        var logEntry = new FxSettlementLog();
        logEntry.setFxSettlement(settlement);
        logEntry.setUser(user);
        logEntry.setStep(step.equals("ins") ? "Instruction" : step);
        logEntry.setFlag(true);
        logEntry.setFile(fileContent);
        logEntry.setComments(dto.getComments());
        logEntry.setEventDate(now);

        fxSettlementLogRepository.save(logEntry);
    }

    @Override
    public List<FxSettlementHistoryDTO> getHistoryByFxSettlementId(Long id) {
        return fxSettlementLogRepository.getHistoryByFxSettlementId(id);
    }

    @Override
    public FxSettlementHistoryDTO viewStep(Long fxSettlementId, String step) {
        if (fxSettlementId == null || fxSettlementId <= 0) {
            throw new IllegalArgumentException("fxSettlementId must not be null or <= 0");
        }
        if (step == null || step.isBlank()) {
            throw new IllegalArgumentException("step must not be null or blank");
        }
        step = step.trim().toUpperCase();
        step = step.equals("INS") ? "Instruction" : step;

        return fxSettlementLogRepository.findHistoryByFxSettlementIdAndStep(fxSettlementId, step)
                .orElseThrow();
    }

    @Override
    public void rollbackStep(Long fxSettlementId, String step, Long userId) {
        if (step == null || step.trim().isBlank()) {
            throw new IllegalArgumentException("currentStep must not be null or blank");
        }

        final var settlement = fxSettlementRepository.findById(fxSettlementId)
                .orElseThrow(() -> new NotFoundException("Settlement not found for ID: " + fxSettlementId));

        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found for ID: " + userId));

        step = step.trim().toLowerCase().substring(0, 3);

        switch (step) {
            case "ins":
                settlement.setInsFlag(false);
                settlement.setInsComments(null);
                settlement.setInsUser(null);
                settlement.setInsTimestamp(null);
                break;
            case "g10":
                settlement.setG10Flag(false);
                settlement.setG10Comments(null);
                settlement.setG10User(null);
                settlement.setG10Timestamp(null);
                break;
            case "brl":
                settlement.setBrlFlag(false);
                settlement.setBrlComments(null);
                settlement.setBrlUser(null);
                settlement.setBrlTimestamp(null);
                break;
            case "ion":
                settlement.setIonFlag(false);
                settlement.setIonComments(null);
                settlement.setIonUser(null);
                settlement.setIonTimestamp(null);
                break;
        }

        fxSettlementRepository.save(settlement);

        var logEntry = new FxSettlementLog();
        logEntry.setFxSettlement(settlement);
        logEntry.setUser(user);
        logEntry.setStep(step.equals("ins") ? "Instruction" : step);
        logEntry.setFlag(false);
        logEntry.setComments("- STEP DELETED -");
        logEntry.setEventDate(LocalDateTime.now());

        fxSettlementLogRepository.save(logEntry);
    }

    @Override
    public FileContentDTO getFile(UUID id) {
        if (!fxSettlementRepository.existsSettlementByFileContentId(id)) {
            throw new SecurityViolationException("Access denied: no settlement record associated with this file.");
        }
        return fileDataService.get(id);
    }
}
