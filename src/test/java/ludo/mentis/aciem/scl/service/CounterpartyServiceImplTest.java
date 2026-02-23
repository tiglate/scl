package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Document;
import ludo.mentis.aciem.scl.domain.DocumentType;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.model.CounterpartyDTO;
import ludo.mentis.aciem.scl.model.DocumentDTO;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.DocumentTypeRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounterpartyServiceImplTest {

    @Mock
    private FxTradeRepository fxTradeRepository;
    @Mock
    private DocumentTypeRepository documentTypeRepository;
    @Mock
    private CounterpartyRepository counterpartyRepository;

    @InjectMocks
    private CounterpartyServiceImpl counterpartyService;

    @Test
    void findAll_shouldReturnPage() {
        CounterpartyDTO searchDTO = new CounterpartyDTO();
        Pageable pageable = mock(Pageable.class);
        Page<CounterpartyDTO> expectedPage = new PageImpl<>(Collections.emptyList());

        when(counterpartyRepository.findAllBySearchCriteria(any(), any(), any(), any(), eq(pageable)))
                .thenReturn(expectedPage);

        Page<CounterpartyDTO> result = counterpartyService.findAll(searchDTO, pageable);

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    void get_shouldReturnDTO_whenFound() {
        Long id = 1L;
        Counterparty counterparty = new Counterparty();
        counterparty.setId(id);
        counterparty.setLongName("Long Name");

        when(counterpartyRepository.findById(id)).thenReturn(Optional.of(counterparty));

        CounterpartyDTO result = counterpartyService.get(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getLongName()).isEqualTo("Long Name");
    }

    @Test
    void get_shouldThrowNotFoundException_whenNotFound() {
        Long id = 1L;
        when(counterpartyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> counterpartyService.get(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldSaveAndReturnId() {
        CounterpartyDTO dto = new CounterpartyDTO();
        dto.setLongName("New Counterparty");
        dto.setIsActive(true);
        dto.setDocuments(Collections.emptyList());

        Counterparty savedEntity = new Counterparty();
        savedEntity.setId(123L);

        when(counterpartyRepository.save(any(Counterparty.class))).thenReturn(savedEntity);

        Long id = counterpartyService.create(dto);

        assertThat(id).isEqualTo(123L);
        verify(counterpartyRepository).save(any(Counterparty.class));
    }

    @Test
    void update_shouldUpdateWhenFound() {
        Long id = 1L;
        CounterpartyDTO dto = new CounterpartyDTO();
        dto.setLongName("Updated Name");
        dto.setIsActive(true);
        dto.setDocuments(Collections.emptyList());

        Counterparty existingEntity = new Counterparty();
        existingEntity.setId(id);

        when(counterpartyRepository.findById(id)).thenReturn(Optional.of(existingEntity));

        counterpartyService.update(id, dto);

        assertThat(existingEntity.getLongName()).isEqualTo("Updated Name");
        verify(counterpartyRepository).save(existingEntity);
    }

    @Test
    void delete_shouldCallRepository() {
        Long id = 1L;
        counterpartyService.delete(id);
        verify(counterpartyRepository).deleteById(id);
    }

    @Test
    void getReferencedWarning_shouldReturnWarning_whenReferencedByFxTrade() {
        Long id = 1L;
        Counterparty counterparty = new Counterparty();
        counterparty.setId(id);

        FxTrade fxTrade = new FxTrade();
        fxTrade.setId(500L);

        when(counterpartyRepository.findById(id)).thenReturn(Optional.of(counterparty));
        when(fxTradeRepository.findFirstByCounterparty(counterparty)).thenReturn(fxTrade);

        var warning = counterpartyService.getReferencedWarning(id);

        assertThat(warning).isNotNull();
        assertThat(warning.toMessage()).contains("referenced by Fx Trade 500");
    }

    @Test
    void getReferencedWarning_shouldReturnNull_whenNotReferenced() {
        Long id = 1L;
        Counterparty counterparty = new Counterparty();
        counterparty.setId(id);

        when(counterpartyRepository.findById(id)).thenReturn(Optional.of(counterparty));
        when(fxTradeRepository.findFirstByCounterparty(counterparty)).thenReturn(null);

        var warning = counterpartyService.getReferencedWarning(id);

        assertThat(warning).isNull();
    }
    
    @Test
    void update_shouldHandleDocuments() {
        Long id = 1L;
        CounterpartyDTO dto = new CounterpartyDTO();
        dto.setLongName("Name");
        dto.setIsActive(true);
        
        DocumentDTO docDTO = new DocumentDTO();
        docDTO.setAction("new");
        docDTO.setValue("DOC123");
        docDTO.setDocumentTypeId(10L);
        dto.setDocuments(List.of(docDTO));

        Counterparty existingEntity = new Counterparty();
        existingEntity.setId(id);
        existingEntity.setDocuments(new java.util.HashSet<>());

        DocumentType docType = new DocumentType();
        docType.setId(10L);

        when(counterpartyRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(documentTypeRepository.findById(10L)).thenReturn(Optional.of(docType));

        counterpartyService.update(id, dto);

        assertThat(existingEntity.getDocuments()).hasSize(1);
        Document savedDoc = existingEntity.getDocuments().iterator().next();
        assertThat(savedDoc.getValue()).isEqualTo("DOC123");
        assertThat(savedDoc.getDocumentType()).isEqualTo(docType);
    }
}
