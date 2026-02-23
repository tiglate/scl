package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.model.CurrencyDTO;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
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
class CurrencyServiceImplTest {

    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private FxTradeRepository fxTradeRepository;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Test
    void findAll_shouldReturnPage() {
        CurrencyDTO searchDTO = new CurrencyDTO();
        Pageable pageable = mock(Pageable.class);
        Page<CurrencyDTO> expectedPage = new PageImpl<>(Collections.emptyList());

        when(currencyRepository.findAllBySearchCriteria(any(), any(), any(), eq(pageable)))
                .thenReturn(expectedPage);

        Page<CurrencyDTO> result = currencyService.findAll(searchDTO, pageable);

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    void get_shouldReturnDTO_whenFound() {
        Long id = 1L;
        Currency currency = new Currency();
        currency.setId(id);
        currency.setName("Dollar");
        currency.setIsoCode("USD");

        when(currencyRepository.findById(id)).thenReturn(Optional.of(currency));

        CurrencyDTO result = currencyService.get(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Dollar");
        assertThat(result.getIsoCode()).isEqualTo("USD");
    }

    @Test
    void get_shouldThrowNotFoundException_whenNotFound() {
        Long id = 1L;
        when(currencyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currencyService.get(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldSaveAndReturnId() {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setName("Dollar");
        dto.setIsoCode("usd");

        Currency savedEntity = new Currency();
        savedEntity.setId(10L);

        when(currencyRepository.save(any(Currency.class))).thenReturn(savedEntity);

        Long id = currencyService.create(dto);

        assertThat(id).isEqualTo(10L);
        verify(currencyRepository).save(argThat(currency -> currency.getIsoCode().equals("USD")));
    }

    @Test
    void update_shouldUpdateWhenFound() {
        Long id = 1L;
        CurrencyDTO dto = new CurrencyDTO();
        dto.setName("Updated Dollar");
        dto.setIsoCode("usd");

        Currency existingEntity = new Currency();
        existingEntity.setId(id);

        when(currencyRepository.findById(id)).thenReturn(Optional.of(existingEntity));

        currencyService.update(id, dto);

        assertThat(existingEntity.getName()).isEqualTo("Updated Dollar");
        assertThat(existingEntity.getIsoCode()).isEqualTo("USD");
        verify(currencyRepository).save(existingEntity);
    }

    @Test
    void delete_shouldCallRepository() {
        Long id = 1L;
        currencyService.delete(id);
        verify(currencyRepository).deleteById(id);
    }

    @Test
    void isoCodeExists_shouldReturnResult() {
        when(currencyRepository.existsByIsoCodeIgnoreCase("USD")).thenReturn(true);
        assertThat(currencyService.isoCodeExists("USD")).isTrue();
    }

    @Test
    void bacenCodeExists_shouldReturnResult() {
        when(currencyRepository.existsByBacenCodeIgnoreCase("220")).thenReturn(true);
        assertThat(currencyService.bacenCodeExists("220")).isTrue();
    }

    @Test
    void getReferencedWarning_shouldReturnWarning_whenReferencedByBuyCurrencyFxTrade() {
        Long id = 1L;
        Currency currency = new Currency();
        currency.setId(id);

        FxTrade fxTrade = new FxTrade();
        fxTrade.setId(300L);

        when(currencyRepository.findById(id)).thenReturn(Optional.of(currency));
        when(fxTradeRepository.findFirstByBuyCurrency(currency)).thenReturn(fxTrade);

        var warning = currencyService.getReferencedWarning(id);

        assertThat(warning).isNotNull();
        assertThat(warning.toMessage()).contains("referenced by Fx Trade 300 via field Buy Currency");
    }

    @Test
    void getReferencedWarning_shouldReturnWarning_whenReferencedBySellCurrencyFxTrade() {
        Long id = 1L;
        Currency currency = new Currency();
        currency.setId(id);

        FxTrade fxTrade = new FxTrade();
        fxTrade.setId(400L);

        when(currencyRepository.findById(id)).thenReturn(Optional.of(currency));
        when(fxTradeRepository.findFirstByBuyCurrency(currency)).thenReturn(null);
        when(fxTradeRepository.findFirstBySellCurrency(currency)).thenReturn(fxTrade);

        var warning = currencyService.getReferencedWarning(id);

        assertThat(warning).isNotNull();
        assertThat(warning.toMessage()).contains("referenced by Fx Trade 400 via field Sell Currency");
    }

    @Test
    void getReferencedWarning_shouldReturnNull_whenNotReferenced() {
        Long id = 1L;
        Currency currency = new Currency();
        currency.setId(id);

        when(currencyRepository.findById(id)).thenReturn(Optional.of(currency));
        when(fxTradeRepository.findFirstByBuyCurrency(currency)).thenReturn(null);
        when(fxTradeRepository.findFirstBySellCurrency(currency)).thenReturn(null);

        var warning = currencyService.getReferencedWarning(id);

        assertThat(warning).isNull();
    }
}
