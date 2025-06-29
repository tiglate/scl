package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByCode(String code);

    Page<Role> findAllById(Long id, Pageable pageable);

    boolean existsByCodeIgnoreCase(String code);

}
