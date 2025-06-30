package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.model.DepartmentDTO;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    Page<DepartmentDTO> findAll(DepartmentDTO filter, Pageable pageable);

    DepartmentDTO get(Long id);

    @SuppressWarnings("UnusedReturnValue")
    Long create(DepartmentDTO departmentDTO);

    void update(Long id, DepartmentDTO departmentDTO);

    void delete(Long id);

    boolean nameExists(String name);

    ReferencedWarning getReferencedWarning(Long id);

}