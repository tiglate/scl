package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.model.DepartmentDTO;
import ludo.mentis.aciem.scl.repos.DepartmentRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.exception.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DepartmentServiceImpl(final DepartmentRepository departmentRepository,
                                 final UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<DepartmentDTO> findAll(DepartmentDTO searchDTO, Pageable pageable) {
        return departmentRepository.findAllBySearchCriteria(
                searchDTO.getName(),
                searchDTO.getEmail(),
                pageable
        );
    }

    @Override
    public DepartmentDTO get(final Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null when retrieving an entity.");
        }
        return departmentRepository.findById(id)
                .map(department -> mapToDTO(department, new DepartmentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final DepartmentDTO departmentDTO) {
        var department = mapToEntity(departmentDTO);
        return departmentRepository.save(department).getId();
    }

    @Override
    public void update(final Long id, final DepartmentDTO departmentDTO) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null when updating an entity.");
        }
        final Department department = departmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(departmentDTO, department);
        departmentRepository.save(department);
    }

    @Override
    public void delete(final Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        departmentRepository.deleteById(id);
    }

    private DepartmentDTO mapToDTO(final Department department, final DepartmentDTO departmentDTO) {
        departmentDTO.setId(department.getId());
        departmentDTO.setName(department.getName());
        departmentDTO.setEmail(department.getEmail());
        departmentDTO.setCreatedAt(department.getCreatedAt());
        departmentDTO.setUpdatedAt(department.getUpdatedAt());
        return departmentDTO;
    }

    private Department mapToEntity(final DepartmentDTO departmentDTO) {
        return mapToEntity(departmentDTO, new Department());
    }

    private Department mapToEntity(final DepartmentDTO departmentDTO, final Department department) {
        department.setName(departmentDTO.getName());
        department.setEmail(departmentDTO.getEmail());
        return department;
    }

    @Override
    public boolean nameExists(final String name) {
        return departmentRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null when checking for references.");
        }
        final var referencedWarning = new ReferencedWarning();
        final var department        = departmentRepository.findById(id).orElseThrow(NotFoundException::new);
        final var departmentUser    = userRepository.findFirstByDepartment(department);

        if (departmentUser != null) {
            referencedWarning.setMessage("This entity is still referenced by User %d via field Department.", departmentUser.getId());
            return referencedWarning;
        }

        return null;
    }

}