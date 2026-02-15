package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.DocumentType;
import ludo.mentis.aciem.scl.model.DocumentTypeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    @Query("SELECT new ludo.mentis.aciem.scl.model.DocumentTypeDTO(d.id, d.name, d.createdAt, d.updatedAt) " +
            "FROM DocumentType d " +
            "WHERE (:name IS NULL OR d.name LIKE %:name%) ")
    Page<DocumentTypeDTO> findAllBySearchCriteria(
            @Param("name") String name,
            Pageable pageable
    );

    boolean existsByNameIgnoreCase(String name);
    
    Optional<DocumentType> findByNameIgnoreCase(String name);
}
