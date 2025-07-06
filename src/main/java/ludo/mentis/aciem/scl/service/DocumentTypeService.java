package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.model.DocumentTypeDTO;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DocumentTypeService {

	Page<DocumentTypeDTO> findAll(DocumentTypeDTO searchDTO, Pageable pageable);

    DocumentTypeDTO get(Long id);

    Long create(DocumentTypeDTO documentTypeDTO);

    void update(Long id, DocumentTypeDTO documentTypeDTO);

    void delete(Long id);

    boolean nameExists(String name);

    ReferencedWarning getReferencedWarning(Long id);

}
