package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.model.RoleDTO;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(final RoleRepository roleRepository,
            final UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<RoleDTO> findAll(final String filter, final Pageable pageable) {
        Page<Role> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = roleRepository.findAllById(longFilter, pageable);
        } else {
            page = roleRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(role -> mapToDTO(role, new RoleDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    @Override
    public RoleDTO get(final Long id) {
        return roleRepository.findById(id)
                .map(role -> mapToDTO(role, new RoleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final RoleDTO roleDTO) {
        final Role role = new Role();
        mapToEntity(roleDTO, role);
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
        final Role role = roleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        userRepository.findAllByRoles(role)
                .forEach(user -> user.getRoles().remove(role));
        roleRepository.delete(role);
    }

    private RoleDTO mapToDTO(final Role role, final RoleDTO roleDTO) {
        roleDTO.setId(role.getId());
        roleDTO.setCode(role.getCode());
        roleDTO.setDescription(role.getDescription());
        return roleDTO;
    }

    private Role mapToEntity(final RoleDTO roleDTO, final Role role) {
        role.setCode(roleDTO.getCode());
        role.setDescription(roleDTO.getDescription());
        return role;
    }

    @Override
    public boolean codeExists(final String code) {
        return roleRepository.existsByCodeIgnoreCase(code);
    }

}
