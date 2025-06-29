package ludo.mentis.aciem.scl.service;

import java.util.HashSet;
import java.util.List;
import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.FxSettlement;
import ludo.mentis.aciem.scl.domain.FxSettlementStep;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.UserDTO;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementStepRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FxSettlementStepRepository fxSettlementStepRepository;
    private final FxTradeRepository fxTradeRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final FxSettlementRepository fxSettlementRepository;

    public UserServiceImpl(final UserRepository userRepository, final RoleRepository roleRepository,
            final PasswordEncoder passwordEncoder,
            final FxSettlementStepRepository fxSettlementStepRepository,
            final FxTradeRepository fxTradeRepository,
            final CounterpartyRepository counterpartyRepository,
            final FxSettlementRepository fxSettlementRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.fxSettlementStepRepository = fxSettlementStepRepository;
        this.fxTradeRepository = fxTradeRepository;
        this.counterpartyRepository = counterpartyRepository;
        this.fxSettlementRepository = fxSettlementRepository;
    }

    @Override
    public Page<UserDTO> findAll(final String filter, final Pageable pageable) {
        Page<User> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = userRepository.findAllById(longFilter, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    @Override
    public UserDTO get(final Long id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getId();
    }

    @Override
    public void update(final Long id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    @Override
    public void delete(final Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setName(user.getName());
        userDTO.setGender(user.getGender());
        userDTO.setIsActive(user.getIsActive());
        userDTO.setResetUID(user.getResetUID());
        userDTO.setResetStart(user.getResetStart());
        userDTO.setRoles(user.getRoles().stream()
                .map(role -> role.getId())
                .toList());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setName(userDTO.getName());
        user.setGender(userDTO.getGender());
        user.setIsActive(userDTO.getIsActive());
        user.setResetUID(userDTO.getResetUID());
        user.setResetStart(userDTO.getResetStart());
        final List<Role> roles = roleRepository.findAllById(
                userDTO.getRoles() == null ? List.of() : userDTO.getRoles());
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
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final FxSettlementStep userFxSettlementStep = fxSettlementStepRepository.findFirstByUser(user);
        if (userFxSettlementStep != null) {
            referencedWarning.setKey("user.fxSettlementStep.user.referenced");
            referencedWarning.addParam(userFxSettlementStep.getId());
            return referencedWarning;
        }
        final FxTrade updatedByFxTrade = fxTradeRepository.findFirstByUpdatedBy(user);
        if (updatedByFxTrade != null) {
            referencedWarning.setKey("user.fxTrade.updatedBy.referenced");
            referencedWarning.addParam(updatedByFxTrade.getId());
            return referencedWarning;
        }
        final Counterparty updatedByCounterparty = counterpartyRepository.findFirstByUpdatedBy(user);
        if (updatedByCounterparty != null) {
            referencedWarning.setKey("user.counterparty.updatedBy.referenced");
            referencedWarning.addParam(updatedByCounterparty.getId());
            return referencedWarning;
        }
        final FxSettlement completedByFxSettlement = fxSettlementRepository.findFirstByCompletedBy(user);
        if (completedByFxSettlement != null) {
            referencedWarning.setKey("user.fxSettlement.completedBy.referenced");
            referencedWarning.addParam(completedByFxSettlement.getId());
            return referencedWarning;
        }
        return null;
    }

}
