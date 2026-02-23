package ludo.mentis.aciem.scl.dev;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.FxTradePurpose;
import ludo.mentis.aciem.scl.model.Product;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FxTradesLoaderTest {

    @Mock
    private RandomUtils randomUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FxTradeRepository fxTradeRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CounterpartyRepository counterpartyRepository;

    @InjectMocks
    private FxTradesLoader fxTradesLoader;

    @Test
    void getOrder_shouldReturnTwo() {
        assertEquals(2, fxTradesLoader.getOrder());
    }

    @Test
    void getName_shouldReturnFxTrades() {
        assertEquals("FX Trades", fxTradesLoader.getName());
    }

    @Test
    void canItRun_shouldReturnTrue_whenNoFxTradesExist() {
        when(fxTradeRepository.count()).thenReturn(0L);
        assertTrue(fxTradesLoader.canItRun());
    }

    @Test
    void canItRun_shouldReturnFalse_whenFxTradesExist() {
        when(fxTradeRepository.count()).thenReturn(10L);
        assertFalse(fxTradesLoader.canItRun());
    }

    @Test
    void run_shouldCreateThreeHundredFxTrades() {
        when(counterpartyRepository.count()).thenReturn(10L);
        when(userRepository.count()).thenReturn(5L);
        
        when(randomUtils.pickRandomEnumValue(Product.class)).thenReturn(Product.FX_SPOT);
        when(randomUtils.getRandomDate(any(), any())).thenReturn(LocalDate.now());
        when(randomUtils.pickRandomEnumValue(FxTradePurpose.class)).thenReturn(FxTradePurpose.EQ);

        Currency usd = new Currency();
        usd.setIsoCode("USD");
        Currency brl = new Currency();
        brl.setIsoCode("BRL");
        Currency eur = new Currency();
        eur.setIsoCode("EUR");

        when(currencyRepository.findByIsoCodeIgnoreCase(anyString())).thenAnswer(invocation -> {
            String iso = invocation.getArgument(0);
            Currency c = new Currency();
            c.setIsoCode(iso);
            return Optional.of(c);
        });

        when(counterpartyRepository.findById(anyLong())).thenReturn(Optional.of(new Counterparty()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        int result = fxTradesLoader.run();

        assertEquals(300, result);
        verify(fxTradeRepository, times(300)).save(any(FxTrade.class));
    }
}
