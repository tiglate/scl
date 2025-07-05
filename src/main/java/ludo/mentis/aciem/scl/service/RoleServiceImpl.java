package ludo.mentis.aciem.scl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.model.RoleDTO;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(final RoleRepository roleRepository,
                           final UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<RoleDTO> findAll(RoleDTO searchDTO, Pageable pageable) {
        return roleRepository.findAllBySearchCriteria(
                searchDTO.getCode(),
                searchDTO.getDescription(),
                pageable
        );
    }

    @Override
    public RoleDTO get(final Long id) {
        return roleRepository.findById(id)
                .map(role -> mapToDTO(role, new RoleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final RoleDTO roleDTO) {
        var role = mapToEntity(roleDTO);
        return roleRepository.save(role).getId();
    }

    @Override
    public void update(final Long id, final RoleDTO roleDTO) {
        final Role role = roleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(roleDTO, role);
        roleRepository.save(role);
    }

    @Override
    public void delete(final Long id) {
        roleRepository.deleteById(id);
    }

    private RoleDTO mapToDTO(final Role role, final RoleDTO roleDTO) {
        roleDTO.setId(role.getId());
        roleDTO.setCode(role.getCode());
        roleDTO.setDescription(role.getDescription());
        roleDTO.setCreatedAt(role.getCreatedAt());
        roleDTO.setUpdatedAt(role.getUpdatedAt());
        return roleDTO;
    }

    private Role mapToEntity(final RoleDTO roleDTO) {
        return mapToEntity(roleDTO, new Role());
    }

    private Role mapToEntity(final RoleDTO roleDTO, final Role role) {
        role.setCode(roleDTO.getCode() != null ? roleDTO.getCode().toUpperCase() : null);
        role.setDescription(roleDTO.getDescription());
        return role;
    }

    @Override
    public boolean codeExists(final String code) {
        return roleRepository.existsByCodeIgnoreCase(code);
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        final var referencedWarning = new ReferencedWarning();
        final var role        = roleRepository.findById(id).orElseThrow(NotFoundException::new);
        final var roleUser    = userRepository.findFirstByRoles(role);

        if (roleUser != null) {
            referencedWarning.setKey("role.user.role.referenced");
            referencedWarning.addParam(roleUser.getId());
            return referencedWarning;
        }

        return null;
    }

}