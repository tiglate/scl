package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.DepartmentDTO;
import ludo.mentis.aciem.scl.repos.DepartmentRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Test
    void findAll_shouldReturnPage() {
        DepartmentDTO searchDTO = new DepartmentDTO();
        Pageable pageable = mock(Pageable.class);
        Page<DepartmentDTO> expectedPage = new PageImpl<>(Collections.emptyList());

        when(departmentRepository.findAllBySearchCriteria(any(), any(), eq(pageable)))
                .thenReturn(expectedPage);

        Page<DepartmentDTO> result = departmentService.findAll(searchDTO, pageable);

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    void get_shouldReturnDTO_whenFound() {
        Long id = 1L;
        Department department = new Department();
        department.setName("IT");
        department.setEmail("it@example.com");

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        DepartmentDTO result = departmentService.get(id);

        assertThat(result.getName()).isEqualTo("IT");
        assertThat(result.getEmail()).isEqualTo("it@example.com");
    }

    @Test
    void get_shouldThrowNotFoundException_whenNotFound() {
        Long id = 1L;
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.get(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldSaveAndReturnId() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("New Dept");

        Department savedEntity = new Department();
        savedEntity.setId(5L);

        when(departmentRepository.save(any(Department.class))).thenReturn(savedEntity);

        Long id = departmentService.create(dto);

        assertThat(id).isEqualTo(5L);
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void update_shouldUpdateWhenFound() {
        Long id = 1L;
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("Updated Dept");

        Department existingEntity = new Department();
        existingEntity.setId(id);

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existingEntity));

        departmentService.update(id, dto);

        assertThat(existingEntity.getName()).isEqualTo("Updated Dept");
        verify(departmentRepository).save(existingEntity);
    }

    @Test
    void delete_shouldCallRepository() {
        Long id = 1L;
        departmentService.delete(id);
        verify(departmentRepository).deleteById(id);
    }

    @Test
    void nameExists_shouldReturnResult() {
        when(departmentRepository.existsByNameIgnoreCase("IT")).thenReturn(true);
        assertThat(departmentService.nameExists("IT")).isTrue();
    }

    @Test
    void getReferencedWarning_shouldReturnWarning_whenReferencedByUser() {
        Long id = 1L;
        Department department = new Department();
        department.setId(id);

        User user = new User();
        user.setId(100L);

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        when(userRepository.findFirstByDepartment(department)).thenReturn(user);

        var warning = departmentService.getReferencedWarning(id);

        assertThat(warning).isNotNull();
        assertThat(warning.toMessage()).contains("referenced by User 100 via field Department");
    }

    @Test
    void getReferencedWarning_shouldReturnNull_whenNotReferenced() {
        Long id = 1L;
        Department department = new Department();
        department.setId(id);

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        when(userRepository.findFirstByDepartment(department)).thenReturn(null);

        var warning = departmentService.getReferencedWarning(id);

        assertThat(warning).isNull();
    }
}
