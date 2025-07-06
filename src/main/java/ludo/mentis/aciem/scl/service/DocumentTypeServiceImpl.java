package ludo.mentis.aciem.scl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ludo.mentis.aciem.scl.domain.Document;
import ludo.mentis.aciem.scl.domain.DocumentType;
import ludo.mentis.aciem.scl.model.DocumentTypeDTO;
import ludo.mentis.aciem.scl.repos.DocumentRepository;
import ludo.mentis.aciem.scl.repos.DocumentTypeRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;

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
    public Page<DocumentTypeDTO> findAll(DocumentTypeDTO searchDTO, Pageable pageable) {
        return documentTypeRepository.findAllBySearchCriteria(
                searchDTO.getName(),
                pageable
        );
    }

    @Override
    public DocumentTypeDTO get(final Long id) {
        return documentTypeRepository.findById(id)
                .map(documenttype -> mapToDTO(documenttype, new DocumentTypeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final DocumentTypeDTO documenttypeDTO) {
        var documenttype = mapToEntity(documenttypeDTO);
        return documentTypeRepository.save(documenttype).getId();
    }

    @Override
    public void update(final Long id, final DocumentTypeDTO documenttypeDTO) {
        final DocumentType documenttype = documentTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(documenttypeDTO, documenttype);
        documentTypeRepository.save(documenttype);
    }

    @Override
    public void delete(final Long id) {
        documentTypeRepository.deleteById(id);
    }

    private DocumentTypeDTO mapToDTO(final DocumentType documenttype, final DocumentTypeDTO documenttypeDTO) {
        documenttypeDTO.setId(documenttype.getId());
        documenttypeDTO.setName(documenttype.getName());
        documenttypeDTO.setCreatedAt(documenttype.getCreatedAt());
        documenttypeDTO.setUpdatedAt(documenttype.getUpdatedAt());
        return documenttypeDTO;
    }

    private DocumentType mapToEntity(final DocumentTypeDTO documenttypeDTO) {
        return mapToEntity(documenttypeDTO, new DocumentType());
    }

    private DocumentType mapToEntity(final DocumentTypeDTO documenttypeDTO, final DocumentType documenttype) {
        documenttype.setName(documenttypeDTO.getName());
        return documenttype;
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