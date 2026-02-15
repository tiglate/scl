package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.model.RoleDTO;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    Page<RoleDTO> findAll(RoleDTO filter, Pageable pageable);

    RoleDTO get(Long id);

    @SuppressWarnings("UnusedReturnValue")
    Long create(RoleDTO roleDTO);

    void update(Long id, RoleDTO roleDTO);

    void delete(Long id);

    boolean codeExists(String code);

    ReferencedWarning getReferencedWarning(Long id);

}