package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.*;
import ludo.mentis.aciem.scl.model.Gender;
import ludo.mentis.aciem.scl.model.UserDTO;
import ludo.mentis.aciem.scl.model.UserSearchDTO;
import ludo.mentis.aciem.scl.repos.*;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private FxTradeRepository fxTradeRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private CounterpartyRepository counterpartyRepository;
    @Mock
    private FxSettlementRepository fxSettlementRepository;
    @Mock
    private FxSettlementStepRepository fxSettlementStepRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testFindAll() {
        UserSearchDTO searchDTO = new UserSearchDTO();
        searchDTO.setUsername("user");
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> expectedPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findAllBySearchCriteria(any(), any(), any(), any(), eq(pageable)))
                .thenReturn(expectedPage);

        Page<UserDTO> result = userService.findAll(searchDTO, pageable);

        assertEquals(expectedPage, result);
    }

    @Test
    void testGet_Success() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setName("John Doe");
        user.setRoles(Collections.emptySet());

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDTO result = userService.get(id);

        assertEquals(id, result.getId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testGet_NotFound() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.get(id));
    }

    @Test
    void testCreate_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("New User");
        userDTO.setPassword("secret");
        userDTO.setRoles(List.of(1L));
        userDTO.setDepartmentId(1L);

        Department department = new Department();
        department.setId(1L);
        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(10L);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(roleRepository.findAllById(anyList())).thenReturn(List.of(role));
        when(passwordEncoder.encode("secret")).thenReturn("encodedSecret");
        when(userRepository.save(any(User.class))).thenReturn(user);

        Long result = userService.create(userDTO);

        assertEquals(10L, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdate_Success() {
        Long id = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Updated Name");
        userDTO.setRoles(Collections.emptyList());
        userDTO.setDepartmentId(1L);

        User existingUser = new User();
        existingUser.setId(id);

        Department department = new Department();
        department.setId(1L);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(roleRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        userService.update(id, userDTO);

        assertEquals("Updated Name", existingUser.getName());
        verify(userRepository).save(existingUser);
    }

    @Test
    void testDelete() {
        Long id = 1L;
        userService.delete(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void testEmailExists() {
        String email = "test@example.com";
        when(userRepository.existsByEmailIgnoreCase(email)).thenReturn(true);
        assertTrue(userService.emailExists(email));
    }

    @Test
    void testUsernameExists() {
        String username = "testuser";
        when(userRepository.existsByUsernameIgnoreCase(username)).thenReturn(true);
        assertTrue(userService.usernameExists(username));
    }

    @Test
    void testGetReferencedWarning_NoReference() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(fxSettlementStepRepository.findFirstByUser(user)).thenReturn(null);
        when(fxTradeRepository.findFirstByUpdatedBy(user)).thenReturn(null);
        when(counterpartyRepository.findFirstByUpdatedBy(user)).thenReturn(null);
        when(fxSettlementRepository.findFirstByCompletedBy(user)).thenReturn(null);

        ReferencedWarning warning = userService.getReferencedWarning(id);

        assertNull(warning);
    }

    @Test
    void testGetReferencedWarning_ReferencedByFxSettlementStep() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        FxSettlementStep step = new FxSettlementStep();
        step.setId(5L);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(fxSettlementStepRepository.findFirstByUser(user)).thenReturn(step);

        ReferencedWarning warning = userService.getReferencedWarning(id);

        assertNotNull(warning);
        assertTrue(warning.toMessage().contains("referenced by Fx Settlement Step 5"));
    }
}
