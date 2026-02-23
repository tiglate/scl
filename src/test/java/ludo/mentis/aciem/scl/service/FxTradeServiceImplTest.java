package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.*;
import ludo.mentis.aciem.scl.model.FxTradeDTO;
import ludo.mentis.aciem.scl.model.FxTradeSearchDTO;
import ludo.mentis.aciem.scl.repos.*;
import ludo.mentis.aciem.scl.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FxTradeServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FxTradeRepository fxTradeRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private CounterpartyRepository counterpartyRepository;
    @Mock
    private FxSettlementRepository fxSettlementRepository;

    @InjectMocks
    private FxTradeServiceImpl fxTradeService;

    @Test
    void findAll_shouldReturnPage() {
        FxTradeSearchDTO searchDTO = new FxTradeSearchDTO();
        Pageable pageable = mock(Pageable.class);
        Page<FxTradeView> expectedPage = new PageImpl<>(Collections.emptyList());

        when(fxTradeRepository.findAllBySearchCriteria(searchDTO, pageable))
                .thenReturn(expectedPage);

        Page<FxTradeView> result = fxTradeService.findAll(searchDTO, pageable);

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    void get_shouldReturnDTO_whenFound() {
        Long id = 1L;
        FxTrade fxTrade = new FxTrade();
        fxTrade.setId(id);
        fxTrade.setTradeId("T123");

        when(fxTradeRepository.findById(id)).thenReturn(Optional.of(fxTrade));

        FxTradeDTO result = fxTradeService.get(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getTradeId()).isEqualTo("T123");
    }

    @Test
    void get_shouldThrowNotFoundException_whenNotFound() {
        Long id = 1L;
        when(fxTradeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fxTradeService.get(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldSaveAndReturnId() {
        FxTradeDTO dto = new FxTradeDTO();
        dto.setTradeId("T123");
        dto.setCounterpartyId(10L);
        dto.setBuyCurrencyId(20L);
        dto.setSellCurrencyId(21L);
        dto.setUpdatedById(30L);

        Counterparty cp = new Counterparty();
        Currency bcc = new Currency();
        Currency scc = new Currency();
        User user = new User();

        when(counterpartyRepository.findById(10L)).thenReturn(Optional.of(cp));
        when(currencyRepository.findById(20L)).thenReturn(Optional.of(bcc));
        when(currencyRepository.findById(21L)).thenReturn(Optional.of(scc));
        when(userRepository.findById(30L)).thenReturn(Optional.of(user));

        FxTrade savedEntity = new FxTrade();
        savedEntity.setId(100L);
        when(fxTradeRepository.save(any(FxTrade.class))).thenReturn(savedEntity);

        Long id = fxTradeService.create(dto);

        assertThat(id).isEqualTo(100L);
        verify(fxTradeRepository).save(any(FxTrade.class));
    }

    @Test
    void update_shouldUpdateWhenFound() {
        Long id = 1L;
        FxTradeDTO dto = new FxTradeDTO();
        dto.setTradeId("UpdatedID");
        dto.setCounterpartyId(10L);
        dto.setBuyCurrencyId(20L);
        dto.setSellCurrencyId(21L);
        dto.setUpdatedById(30L);

        FxTrade existingEntity = new FxTrade();
        existingEntity.setId(id);

        when(fxTradeRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(counterpartyRepository.findById(10L)).thenReturn(Optional.of(new Counterparty()));
        when(currencyRepository.findById(20L)).thenReturn(Optional.of(new Currency()));
        when(currencyRepository.findById(21L)).thenReturn(Optional.of(new Currency()));
        when(userRepository.findById(30L)).thenReturn(Optional.of(new User()));

        fxTradeService.update(id, dto);

        assertThat(existingEntity.getTradeId()).isEqualTo("UpdatedID");
        verify(fxTradeRepository).save(existingEntity);
    }

    @Test
    void delete_shouldCallRepository() {
        Long id = 1L;
        fxTradeService.delete(id);
        verify(fxTradeRepository).deleteById(id);
    }

    @Test
    void getReferencedWarning_shouldReturnWarning_whenReferencedByFxSettlement() {
        Long id = 1L;
        FxTrade fxTrade = new FxTrade();
        fxTrade.setId(id);

        FxSettlement settlement = new FxSettlement();
        settlement.setId(999L);

        when(fxTradeRepository.findById(id)).thenReturn(Optional.of(fxTrade));
        when(fxSettlementRepository.findFirstByTrade(fxTrade)).thenReturn(settlement);

        var warning = fxTradeService.getReferencedWarning(id);

        assertThat(warning).isNotNull();
        assertThat(warning.toMessage()).contains("referenced by Fx Settlement 999 via field Trade");
    }

    @Test
    void getReferencedWarning_shouldReturnNull_whenNotReferenced() {
        Long id = 1L;
        FxTrade fxTrade = new FxTrade();
        fxTrade.setId(id);

        when(fxTradeRepository.findById(id)).thenReturn(Optional.of(fxTrade));
        when(fxSettlementRepository.findFirstByTrade(fxTrade)).thenReturn(null);

        var warning = fxTradeService.getReferencedWarning(id);

        assertThat(warning).isNull();
    }
}
