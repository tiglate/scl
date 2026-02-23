package ludo.mentis.aciem.scl.dev;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.DocumentType;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.DocumentTypeRepository;
import ludo.mentis.aciem.scl.util.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounterpartiesLoaderTest {

    @Mock
    private RandomUtils randomUtils;

    @Mock
    private CounterpartyRepository counterpartyRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @InjectMocks
    private CounterpartiesLoader counterpartiesLoader;

    @Test
    void getOrder_shouldReturnOne() {
        assertEquals(1, counterpartiesLoader.getOrder());
    }

    @Test
    void getName_shouldReturnCounterparties() {
        assertEquals("Counterparties", counterpartiesLoader.getName());
    }

    @Test
    void canItRun_shouldReturnTrue_whenNoCounterpartiesExist() {
        when(counterpartyRepository.count()).thenReturn(0L);
        assertTrue(counterpartiesLoader.canItRun());
    }

    @Test
    void canItRun_shouldReturnFalse_whenCounterpartiesExist() {
        when(counterpartyRepository.count()).thenReturn(5L);
        assertFalse(counterpartiesLoader.canItRun());
    }

    @Test
    void run_shouldCreateOneHundredCounterparties() {
        DocumentType cpf = new DocumentType();
        cpf.setName("CPF");
        DocumentType cnpj = new DocumentType();
        cnpj.setName("CNPJ");
        DocumentType ein = new DocumentType();
        ein.setName("EIN");

        when(documentTypeRepository.findByNameIgnoreCase("CPF")).thenReturn(Optional.of(cpf));
        when(documentTypeRepository.findByNameIgnoreCase("CNPJ")).thenReturn(Optional.of(cnpj));
        when(documentTypeRepository.findByNameIgnoreCase("EIN")).thenReturn(Optional.of(ein));
        when(randomUtils.pickRandomBoolean()).thenReturn(true);

        int result = counterpartiesLoader.run();

        assertEquals(100, result);
        verify(counterpartyRepository, times(100)).save(any(Counterparty.class));
    }
}
