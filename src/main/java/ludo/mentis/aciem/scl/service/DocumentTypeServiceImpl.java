package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Document;
import ludo.mentis.aciem.scl.domain.DocumentType;
import ludo.mentis.aciem.scl.model.DocumentTypeDTO;
import ludo.mentis.aciem.scl.repos.DocumentRepository;
import ludo.mentis.aciem.scl.repos.DocumentTypeRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentRepository documentRepository;

    public DocumentTypeServiceImpl(final DocumentTypeRepository documentTypeRepository,
            final DocumentRepository documentRepository) {
        this.documentTypeRepository = documentTypeRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public Page<DocumentTypeDTO> findAll(final String filter, final Pageable pageable) {
        Page<DocumentType> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = documentTypeRepository.findAllById(longFilter, pageable);
        } else {
            page = documentTypeRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(documentType -> mapToDTO(documentType, new DocumentTypeDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    @Override
    public DocumentTypeDTO get(final Long id) {
        return documentTypeRepository.findById(id)
                .map(documentType -> mapToDTO(documentType, new DocumentTypeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final DocumentTypeDTO documentTypeDTO) {
        final DocumentType documentType = new DocumentType();
        mapToEntity(documentTypeDTO, documentType);
        return documentTypeRepository.save(documentType).getId();
    }

    @Override
    public void update(final Long id, final DocumentTypeDTO documentTypeDTO) {
        final DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(documentTypeDTO, documentType);
        documentTypeRepository.save(documentType);
    }

    @Override
    public void delete(final Long id) {
        documentTypeRepository.deleteById(id);
    }

    private DocumentTypeDTO mapToDTO(final DocumentType documentType,
            final DocumentTypeDTO documentTypeDTO) {
        documentTypeDTO.setId(documentType.getId());
        documentTypeDTO.setName(documentType.getName());
        return documentTypeDTO;
    }

    private DocumentType mapToEntity(final DocumentTypeDTO documentTypeDTO,
            final DocumentType documentType) {
        documentType.setName(documentTypeDTO.getName());
        return documentType;
    }

    @Override
    public boolean nameExists(final String name) {
        return documentTypeRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Document documentTypeDocument = documentRepository.findFirstByDocumentType(documentType);
        if (documentTypeDocument != null) {
            referencedWarning.setKey("documentType.document.documentType.referenced");
            referencedWarning.addParam(documentTypeDocument.getId());
            return referencedWarning;
        }
        return null;
    }

}
