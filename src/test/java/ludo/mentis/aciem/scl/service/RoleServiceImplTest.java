package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.RoleDTO;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void testFindAll() {
        RoleDTO searchDTO = new RoleDTO();
        searchDTO.setCode("ADMIN");
        searchDTO.setDescription("Admin role");
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoleDTO> expectedPage = new PageImpl<>(Collections.emptyList());

        when(roleRepository.findAllBySearchCriteria(searchDTO.getCode(), searchDTO.getDescription(), pageable))
                .thenReturn(expectedPage);

        Page<RoleDTO> result = roleService.findAll(searchDTO, pageable);

        assertEquals(expectedPage, result);
    }

    @Test
    void testGet_Success() {
        Long id = 1L;
        Role role = new Role();
        role.setId(id);
        role.setCode("ADMIN");

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        RoleDTO result = roleService.get(id);

        assertEquals(id, result.getId());
        assertEquals("ADMIN", result.getCode());
    }

    @Test
    void testGet_NotFound() {
        Long id = 1L;
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.get(id));
    }

    @Test
    void testCreate() {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setCode("user");
        roleDTO.setDescription("User role");

        Role role = new Role();
        role.setId(1L);

        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Long result = roleService.create(roleDTO);

        assertEquals(1L, result);
        verify(roleRepository).save(argThat(r -> "USER".equals(r.getCode())));
    }

    @Test
    void testUpdate_Success() {
        Long id = 1L;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setCode("admin");
        roleDTO.setDescription("New Description");

        Role existingRole = new Role();
        existingRole.setId(id);

        when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));

        roleService.update(id, roleDTO);

        assertEquals("ADMIN", existingRole.getCode());
        assertEquals("New Description", existingRole.getDescription());
        verify(roleRepository).save(existingRole);
    }

    @Test
    void testDelete() {
        Long id = 1L;
        roleService.delete(id);
        verify(roleRepository).deleteById(id);
    }

    @Test
    void testCodeExists() {
        String code = "ADMIN";
        when(roleRepository.existsByCodeIgnoreCase(code)).thenReturn(true);

        assertTrue(roleService.codeExists(code));
    }

    @Test
    void testGetReferencedWarning_NoReference() {
        Long id = 1L;
        Role role = new Role();
        role.setId(id);

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(userRepository.findFirstByRoles(anySet())).thenReturn(null);

        ReferencedWarning warning = roleService.getReferencedWarning(id);

        assertNull(warning);
    }

    @Test
    void testGetReferencedWarning_ReferencedByUser() {
        Long id = 1L;
        Role role = new Role();
        role.setId(id);

        User user = new User();
        user.setId(10L);

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(userRepository.findFirstByRoles(anySet())).thenReturn(user);

        ReferencedWarning warning = roleService.getReferencedWarning(id);

        assertNotNull(warning);
        assertTrue(warning.toMessage().contains("referenced by User 10"));
    }
}
