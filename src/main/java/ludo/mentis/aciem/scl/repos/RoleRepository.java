package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.model.RoleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT new ludo.mentis.aciem.scl.model.RoleDTO(d.id, d.code, d.description, d.createdAt, d.updatedAt) " +
            "FROM Role d " +
            "WHERE (:code IS NULL OR d.code LIKE %:code%) " +
            "AND (:description IS NULL OR d.description LIKE %:description%)")
    Page<RoleDTO> findAllBySearchCriteria(
            @Param("code") String code,
            @Param("description") String description,
            Pageable pageable
    );

    Role findByCode(String code);

    Page<Role> findAllById(Long id, Pageable pageable);

    boolean existsByCodeIgnoreCase(String code);

}
