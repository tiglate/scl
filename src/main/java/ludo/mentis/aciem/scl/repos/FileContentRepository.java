package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.FileContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface FileContentRepository extends JpaRepository<FileContent, UUID> {
}
