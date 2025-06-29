package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    Page<DocumentType> findAllById(Long id, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

}
