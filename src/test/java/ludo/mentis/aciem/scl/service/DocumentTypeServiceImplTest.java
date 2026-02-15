package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Document;
import ludo.mentis.aciem.scl.domain.DocumentType;
import ludo.mentis.aciem.scl.model.DocumentTypeDTO;
import ludo.mentis.aciem.scl.repos.DocumentRepository;
import ludo.mentis.aciem.scl.repos.DocumentTypeRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
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
class DocumentTypeServiceImplTest {

    @Mock
    private DocumentTypeRepository documentTypeRepository;
    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentTypeServiceImpl documentTypeService;

    @Test
    void findAll_shouldReturnPage() {
        DocumentTypeDTO searchDTO = new DocumentTypeDTO();
        Pageable pageable = mock(Pageable.class);
        Page<DocumentTypeDTO> expectedPage = new PageImpl<>(Collections.emptyList());

        when(documentTypeRepository.findAllBySearchCriteria(any(), eq(pageable)))
                .thenReturn(expectedPage);

        Page<DocumentTypeDTO> result = documentTypeService.findAll(searchDTO, pageable);

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    void get_shouldReturnDTO_whenFound() {
        Long id = 1L;
        DocumentType documentType = new DocumentType();
        documentType.setId(id);
        documentType.setName("Passport");

        when(documentTypeRepository.findById(id)).thenReturn(Optional.of(documentType));

        DocumentTypeDTO result = documentTypeService.get(id);

        assertThat(result.getName()).isEqualTo("Passport");
    }

    @Test
    void get_shouldThrowNotFoundException_whenNotFound() {
        Long id = 1L;
        when(documentTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentTypeService.get(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldSaveAndReturnId() {
        DocumentTypeDTO dto = new DocumentTypeDTO();
        dto.setName("ID Card");

        DocumentType savedEntity = new DocumentType();
        savedEntity.setId(7L);

        when(documentTypeRepository.save(any(DocumentType.class))).thenReturn(savedEntity);

        Long id = documentTypeService.create(dto);

        assertThat(id).isEqualTo(7L);
        verify(documentTypeRepository).save(any(DocumentType.class));
    }

    @Test
    void update_shouldUpdateWhenFound() {
        Long id = 1L;
        DocumentTypeDTO dto = new DocumentTypeDTO();
        dto.setName("Updated Type");

        DocumentType existingEntity = new DocumentType();
        existingEntity.setId(id);

        when(documentTypeRepository.findById(id)).thenReturn(Optional.of(existingEntity));

        documentTypeService.update(id, dto);

        assertThat(existingEntity.getName()).isEqualTo("Updated Type");
        verify(documentTypeRepository).save(existingEntity);
    }

    @Test
    void delete_shouldCallRepository() {
        Long id = 1L;
        documentTypeService.delete(id);
        verify(documentTypeRepository).deleteById(id);
    }

    @Test
    void nameExists_shouldReturnResult() {
        when(documentTypeRepository.existsByNameIgnoreCase("Passport")).thenReturn(true);
        assertThat(documentTypeService.nameExists("Passport")).isTrue();
    }

    @Test
    void getReferencedWarning_shouldReturnWarning_whenReferencedByDocument() {
        Long id = 1L;
        DocumentType documentType = new DocumentType();
        documentType.setId(id);

        Document document = new Document();
        document.setId(200L);

        when(documentTypeRepository.findById(id)).thenReturn(Optional.of(documentType));
        when(documentRepository.findFirstByDocumentType(documentType)).thenReturn(document);

        var warning = documentTypeService.getReferencedWarning(id);

        assertThat(warning).isNotNull();
        assertThat(warning.toMessage()).contains("referenced by Document 200 via field Document Type");
    }

    @Test
    void getReferencedWarning_shouldReturnNull_whenNotReferenced() {
        Long id = 1L;
        DocumentType documentType = new DocumentType();
        documentType.setId(id);

        when(documentTypeRepository.findById(id)).thenReturn(Optional.of(documentType));
        when(documentRepository.findFirstByDocumentType(documentType)).thenReturn(null);

        var warning = documentTypeService.getReferencedWarning(id);

        assertThat(warning).isNull();
    }
}
