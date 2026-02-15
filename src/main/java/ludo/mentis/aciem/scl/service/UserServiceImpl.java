package ludo.mentis.aciem.scl.service;

import java.util.Collections;
import java.util.HashSet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.UserDTO;
import ludo.mentis.aciem.scl.model.UserSearchDTO;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.DepartmentRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementStepRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;


@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FxTradeRepository fxTradeRepository;
    private final DepartmentRepository departmentRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final FxSettlementRepository fxSettlementRepository;
    private final FxSettlementStepRepository fxSettlementStepRepository;

    public UserServiceImpl(final UserRepository userRepository,
    		               final RoleRepository roleRepository,
    		               final PasswordEncoder passwordEncoder,
    		               final FxTradeRepository fxTradeRepository,
    		               final DepartmentRepository departmentRepository,
    		               final CounterpartyRepository counterpartyRepository,
    		               final FxSettlementRepository fxSettlementRepository,
    		               final FxSettlementStepRepository fxSettlementStepRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.fxTradeRepository = fxTradeRepository;
        this.departmentRepository = departmentRepository;
        this.counterpartyRepository = counterpartyRepository;
        this.fxSettlementRepository = fxSettlementRepository;
        this.fxSettlementStepRepository = fxSettlementStepRepository;
    }

    @Override
    public Page<UserDTO> findAll(UserSearchDTO searchDTO, Pageable pageable) {
        return userRepository.findAllBySearchCriteria(
                searchDTO.getUsername(),
                searchDTO.getName(),
                searchDTO.getDepartment(),
                searchDTO.getEnabled(),
                pageable
        );
    }

    @Override
    public UserDTO get(final Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final UserDTO userDTO) {
        final var user = mapToEntity(userDTO);
        return userRepository.save(user).getId();
    }

    @Override
    public void update(final Long id, final UserDTO userDTO) {
        final var user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    @Override
    public void delete(final Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user) {
        var userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender());
        userDTO.setUsername(user.getUsername());
        userDTO.setEnabled(user.getEnabled());
        userDTO.setDepartmentId(user.getDepartment() == null ? null : user.getDepartment().getId());
        userDTO.setResetUID(user.getResetUID());
        userDTO.setResetStart(user.getResetStart());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setRoles(user.getRoles().stream()
                .map(Role::getId)
                .toList());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO) {
        return mapToEntity(userDTO, new User());
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty() ? user.getPassword() : passwordEncoder.encode(userDTO.getPassword()));
        user.setEnabled(userDTO.getEnabled());
        user.setResetUID(userDTO.getResetUID());
        user.setResetStart(userDTO.getResetStart());

        final var department = userDTO.getDepartmentId() == null ? null : departmentRepository.findById(userDTO.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("department not found"));
        user.setDepartment(department);

        final var roles = roleRepository.findAllById(userDTO.getRoles() == null ? Collections.emptyList() : userDTO.getRoles());
        if (roles.size() != (userDTO.getRoles() == null ? 0 : userDTO.getRoles().size())) {
            throw new NotFoundException("one of roles not found");
        }
        user.setRoles(new HashSet<>(roles));

        return user;
    }

    @Override
    public boolean emailExists(final String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean usernameExists(final String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        final var referencedWarning = new ReferencedWarning();
        final var user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final var userFxSettlementStep = fxSettlementStepRepository.findFirstByUser(user);
        if (userFxSettlementStep != null) {
            referencedWarning.setMessage("This entity is still referenced by Fx Settlement Step %d via field User.", userFxSettlementStep.getId());
            return referencedWarning;
        }
        final var updatedByFxTrade = fxTradeRepository.findFirstByUpdatedBy(user);
        if (updatedByFxTrade != null) {
            referencedWarning.setMessage("This entity is still referenced by Fx Trade %d via field Updated By.", updatedByFxTrade.getId());
            return referencedWarning;
        }
        final var updatedByCounterparty = counterpartyRepository.findFirstByUpdatedBy(user);
        if (updatedByCounterparty != null) {
            referencedWarning.setMessage("This entity is still referenced by Counterparty %d via field Updated By.", updatedByCounterparty.getId());
            return referencedWarning;
        }
        final var completedByFxSettlement = fxSettlementRepository.findFirstByCompletedBy(user);
        if (completedByFxSettlement != null) {
            referencedWarning.setMessage("This entity is still referenced by Fx Settlement %d via field Completed By.", completedByFxSettlement.getId());
            return referencedWarning;
        }
        return null;
    }

}