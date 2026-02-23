package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.*;
import ludo.mentis.aciem.scl.exception.NotFoundException;
import ludo.mentis.aciem.scl.exception.StepAlreadyTaken;
import ludo.mentis.aciem.scl.model.FxSettlementHistoryDTO;
import ludo.mentis.aciem.scl.model.FxSettlementStepDTO;
import ludo.mentis.aciem.scl.model.Step;
import ludo.mentis.aciem.scl.repos.FxSettlementLogRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FxSettlementServiceImplTest {

    @Mock
    private FxSettlementRepository fxSettlementRepository;
    @Mock
    private FxTradeRepository fxTradeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FxSettlementLogRepository fxSettlementLogRepository;

    @InjectMocks
    private FxSettlementServiceImpl service;

    @Captor
    private ArgumentCaptor<FxSettlement> settlementCaptor;
    @Captor
    private ArgumentCaptor<FxSettlementLog> logCaptor;

    private final MultipartFile file = null;

    @Test
    void findAllBySearchCriteria_shouldDelegateToRepository() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);
        List<FxSettlementView> expected = Collections.emptyList();
        when(fxSettlementRepository.findAllBySearchCriteria(start, end)).thenReturn(expected);

        List<FxSettlementView> result = service.findAllBySearchCriteria(start, end);

        assertThat(result).isSameAs(expected);
        verify(fxSettlementRepository).findAllBySearchCriteria(start, end);
    }

    @Test
    void getLastTradeDate_shouldDelegateToRepository() {
        LocalDate expected = LocalDate.of(2025, 5, 5);
        when(fxSettlementRepository.findLastTradeDate()).thenReturn(expected);

        LocalDate result = service.getLastTradeDate();

        assertThat(result).isEqualTo(expected);
        verify(fxSettlementRepository).findLastTradeDate();
    }

    @Test
    void save_shouldValidateInputs() {
        assertThatThrownBy(() -> service.save(null, file)).isInstanceOf(IllegalArgumentException.class);

        FxSettlementStepDTO dto = new FxSettlementStepDTO();
        dto.setCurrentStep(Step.INSTRUCTION_RECEIVED);
        dto.setUserId(1L);
        // missing trade id
        assertThatThrownBy(() -> service.save(dto, file)).isInstanceOf(IllegalArgumentException.class);

        dto.setFxTradeId(2L);
        dto.setUserId(null);
        assertThatThrownBy(() -> service.save(dto, file)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void save_shouldCreateNewSettlementAndLog_whenStepINS() throws Exception {
        FxSettlementStepDTO dto = new FxSettlementStepDTO();
        dto.setCurrentStep(Step.INSTRUCTION_RECEIVED);
        dto.setFxTradeId(10L);
        dto.setUserId(20L);
        dto.setComments("ok");

        when(fxSettlementRepository.findFirstByTradeIdWithLock(10L)).thenReturn(Optional.empty());
        User user = new User();
        user.setId(20L);
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        FxTrade trade = new FxTrade();
        trade.setId(10L);
        trade.setTradeId("T-10");
        when(fxTradeRepository.findById(10L)).thenReturn(Optional.of(trade));

        when(fxSettlementRepository.save(any(FxSettlement.class))).thenAnswer(inv -> inv.getArgument(0));

        service.save(dto, file);

        verify(fxSettlementRepository).save(settlementCaptor.capture());
        FxSettlement saved = settlementCaptor.getValue();
        assertThat(saved.getTrade()).isSameAs(trade);
        assertThat(saved.isInsFlag()).isTrue();
        assertThat(saved.getInsUser()).isSameAs(user);
        assertThat(saved.getInsComments()).isEqualTo("ok");
        assertThat(saved.getInsTimestamp()).isNotNull();

        verify(fxSettlementLogRepository).save(logCaptor.capture());
        FxSettlementLog log = logCaptor.getValue();
        assertThat(log.getFxSettlement()).isSameAs(saved);
        assertThat(log.getUser()).isSameAs(user);
        assertThat(log.getStep()).isEqualTo(Step.INSTRUCTION_RECEIVED.getValue());
        assertThat(log.isFlag()).isTrue();
        assertThat(log.getComments()).isEqualTo("ok");
        assertThat(log.getEventDate()).isNotNull();
    }

    @Test
    void save_shouldThrowStepAlreadyTaken_whenFlagAlreadyTrue() {
        FxSettlementStepDTO dto = new FxSettlementStepDTO();
        dto.setCurrentStep(Step.RECEIVED_OR_PAID_FOREIGN_CURRENCY);
        dto.setFxTradeId(10L);
        dto.setUserId(20L);

        FxSettlement existing = new FxSettlement();
        existing.setG10Flag(true);
        when(fxSettlementRepository.findFirstByTradeIdWithLock(10L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(20L)).thenReturn(Optional.of(new User()));
        FxTrade trade = new FxTrade();
        trade.setId(10L);
        trade.setTradeId("T-10");
        when(fxTradeRepository.findById(10L)).thenReturn(Optional.of(trade));

        assertThatThrownBy(() -> service.save(dto, file)).isInstanceOf(StepAlreadyTaken.class);

        verify(fxSettlementRepository, never()).save(any());
        verify(fxSettlementLogRepository, never()).save(any());
    }

    @Test
    void save_shouldThrowNotFound_whenUserOrTradeMissing() {
        FxSettlementStepDTO dto = new FxSettlementStepDTO();
        dto.setCurrentStep(Step.INSTRUCTION_RECEIVED);
        dto.setFxTradeId(10L);
        dto.setUserId(20L);

        when(fxSettlementRepository.findFirstByTradeIdWithLock(10L)).thenReturn(Optional.of(new FxSettlement()));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto, file)).isInstanceOf(NotFoundException.class);

        // user present, trade missing
        when(userRepository.findById(20L)).thenReturn(Optional.of(new User()));
        when(fxTradeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto, file)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getHistoryByFxSettlementId_shouldDelegateToRepository() {
        when(fxSettlementLogRepository.getHistoryByFxSettlementId(5L)).thenReturn(Collections.emptyList());
        List<FxSettlementHistoryDTO> list = service.getHistoryByFxSettlementId(5L);
        assertThat(list).isEmpty();
        verify(fxSettlementLogRepository).getHistoryByFxSettlementId(5L);
    }

    @Test
    void viewStep_shouldValidateInputs() {
        assertThatThrownBy(() -> service.viewStep(null, Step.INSTRUCTION_RECEIVED)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.viewStep(0L, Step.INSTRUCTION_RECEIVED)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.viewStep(1L, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void viewStep_shouldReturnDto_whenFound() {
        FxSettlementHistoryDTO dto = new FxSettlementHistoryDTO();
        when(fxSettlementLogRepository.findHistoryByFxSettlementIdAndStep(7L, Step.INSTRUCTION_RECEIVED.getValue()))
                .thenReturn(Optional.of(dto));

        FxSettlementHistoryDTO result = service.viewStep(7L, Step.INSTRUCTION_RECEIVED);

        assertThat(result).isSameAs(dto);
        verify(fxSettlementLogRepository).findHistoryByFxSettlementIdAndStep(7L, Step.INSTRUCTION_RECEIVED.getValue());
    }

    @Test
    void rollbackStep_shouldResetG10_andCreateLog() {
        FxSettlement settlement = new FxSettlement();
        settlement.setG10Flag(true);
        settlement.setG10Comments("c");
        settlement.setG10User(new User());

        when(fxSettlementRepository.findById(100L)).thenReturn(Optional.of(settlement));
        User user = new User();
        user.setId(9L);
        when(userRepository.findById(9L)).thenReturn(Optional.of(user));

        when(fxSettlementRepository.save(any(FxSettlement.class))).thenAnswer(inv -> inv.getArgument(0));

        service.rollbackStep(100L, Step.RECEIVED_OR_PAID_FOREIGN_CURRENCY, 9L);

        verify(fxSettlementRepository).save(settlementCaptor.capture());
        FxSettlement saved = settlementCaptor.getValue();
        assertThat(saved.isG10Flag()).isFalse();
        assertThat(saved.getG10Comments()).isNull();
        assertThat(saved.getG10User()).isNull();
        assertThat(saved.getG10Timestamp()).isNull();

        verify(fxSettlementLogRepository).save(logCaptor.capture());
        FxSettlementLog log = logCaptor.getValue();
        assertThat(log.getFxSettlement()).isSameAs(saved);
        assertThat(log.getUser()).isSameAs(user);
        assertThat(log.getStep()).isEqualTo(Step.RECEIVED_OR_PAID_FOREIGN_CURRENCY.getValue());
        assertThat(log.isFlag()).isFalse();
        assertThat(log.getComments()).isEqualTo("- STEP DELETED -");
        assertThat(log.getEventDate()).isNotNull();
    }

    @Test
    void rollbackStep_shouldThrowNotFound_whenSettlementOrUserMissing() {
        when(fxSettlementRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.rollbackStep(1L, Step.INSTRUCTION_RECEIVED, 2L)).isInstanceOf(NotFoundException.class);

        FxSettlement existing = new FxSettlement();
        when(fxSettlementRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.rollbackStep(1L, Step.INSTRUCTION_RECEIVED, 2L)).isInstanceOf(NotFoundException.class);
    }
}
