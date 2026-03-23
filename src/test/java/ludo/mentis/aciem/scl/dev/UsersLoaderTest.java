package ludo.mentis.aciem.scl.dev;

import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.Gender;
import ludo.mentis.aciem.scl.repos.DepartmentRepository;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.FakePasswordEncoderFactory;
import ludo.mentis.aciem.scl.util.PasswordEncoderFactory;
import ludo.mentis.aciem.scl.util.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersLoaderTest {

    @Mock
    private RandomUtils randomUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private PasswordEncoderFactory passwordEncoderFactory = new FakePasswordEncoderFactory();

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private UsersLoader usersLoader;

    @Test
    void getOrder_shouldReturnZero() {
        assertEquals(0, usersLoader.getOrder());
    }

    @Test
    void getName_shouldReturnUsers() {
        assertEquals("Users", usersLoader.getName());
    }

    @Test
    void canItRun_shouldReturnTrue_whenNoUsersExist() {
        when(userRepository.count()).thenReturn(0L);
        assertTrue(usersLoader.canItRun());
    }

    @Test
    void canItRun_shouldReturnFalse_whenUsersExist() {
        when(userRepository.count()).thenReturn(10L);
        assertFalse(usersLoader.canItRun());
    }

    @Test
    void run_shouldCreateAdminAndRegularUsers() {
        Department itDept = new Department();
        itDept.setName("IT");
        Department hrDept = new Department();
        hrDept.setName("HR");

        Role adminRole = new Role();
        adminRole.setCode("ADMIN");
        Role userRole = new Role();
        userRole.setCode("USER");

        when(departmentRepository.findByNameIgnoreCase("IT")).thenReturn(Optional.of(itDept));
        when(departmentRepository.findAll()).thenReturn(List.of(itDept, hrDept));
        when(roleRepository.findAll()).thenReturn(List.of(adminRole, userRole));
        when(randomUtils.pickRandomEnumValue(Gender.class)).thenReturn(Gender.MALE);
        when(randomUtils.pickRandomBoolean()).thenReturn(true);
        when(randomUtils.createRandomSublist(any(), anyInt())).thenReturn(List.of(userRole));

        int result = usersLoader.run();

        // 1 admin + 2 users per department (2 departments) + 2 ad users = 1 + 4 + 2 = 7
        assertEquals(7, result);
        verify(userRepository, times(7)).save(any(User.class));
    }
}
