package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Document;
import ludo.mentis.aciem.scl.domain.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DocumentRepository extends JpaRepository<Document, Long> {

    Document findFirstByDocumentType(DocumentType documentType);

}
