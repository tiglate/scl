package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.model.DepartmentDTO;
import ludo.mentis.aciem.scl.service.DepartmentService;
import ludo.mentis.aciem.scl.service.FileDataService;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import ludo.mentis.aciem.scl.util.UserRoles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static ludo.mentis.aciem.scl.controller.TestSecurityConfig.createCustomUserDetails;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
@Import(TestSecurityConfig.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @MockitoBean
    private FileDataService fileDataService;

    @Test
    void testList() throws Exception {
        when(departmentService.findAll(any(DepartmentDTO.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/departments").with(user(createCustomUserDetails("user", List.of(UserRoles.DEPARTMENT_READ)))))
                .andExpect(status().isOk())
                .andExpect(view().name("department/list"))
                .andExpect(model().attributeExists("departments", "filter"));
    }

    @Test
    void testView() throws Exception {
        when(departmentService.get(1L)).thenReturn(new DepartmentDTO());

        mockMvc.perform(get("/departments/view/1").with(user(createCustomUserDetails("user", List.of(UserRoles.DEPARTMENT_READ)))))
                .andExpect(status().isOk())
                .andExpect(view().name("department/view"))
                .andExpect(model().attributeExists("department"));
    }

    @Test
    void testEditGet() throws Exception {
        when(departmentService.get(1L)).thenReturn(new DepartmentDTO());

        mockMvc.perform(get("/departments/edit/1").with(user(createCustomUserDetails("user", List.of(UserRoles.DEPARTMENT_READ)))))
                .andExpect(status().isOk())
                .andExpect(view().name("department/edit"))
                .andExpect(model().attributeExists("department"));
    }

    @Test
    void testAddGet() throws Exception {
        mockMvc.perform(get("/departments/add").with(user(createCustomUserDetails("user", List.of(UserRoles.DEPARTMENT_WRITE)))))
                .andExpect(status().isOk())
                .andExpect(view().name("department/add"))
                .andExpect(model().attributeExists("department"));
    }

    @Test
    void testAddPost_Success() throws Exception {
        mockMvc.perform(post("/departments/add").with(user(createCustomUserDetails("user", List.of(UserRoles.DEPARTMENT_WRITE))))
                        .with(csrf())
                        .param("name", "IT")
                        .param("email", "it@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(departmentService).create(any(DepartmentDTO.class));
    }

    @Test
    void testEditPost_Success() throws Exception {
        DepartmentDTO existing = new DepartmentDTO();
        existing.setName("IT");
        when(departmentService.get(1L)).thenReturn(existing);

        mockMvc.perform(post("/departments/edit/1").with(user(createCustomUserDetails("user", List.of(UserRoles.DEPARTMENT_WRITE))))
                        .with(csrf())
                        .param("name", "IT")
                        .param("email", "it_new@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(departmentService).update(eq(1L), any(DepartmentDTO.class));
    }

    @Test
    void testDelete_Success() throws Exception {
        when(departmentService.getReferencedWarning(1L)).thenReturn(null);

        mockMvc.perform(post("/departments/delete/1").with(user(createCustomUserDetails("user", List.of(UserRoles.DEPARTMENT_WRITE))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(departmentService).delete(1L);
    }

    @Test
    void testDelete_Referenced() throws Exception {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setMessage("Reference warning");
        when(departmentService.getReferencedWarning(1L)).thenReturn(warning);

        mockMvc.perform(post("/departments/delete/1").with(user(createCustomUserDetails("user", List.of(UserRoles.DEPARTMENT_WRITE))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"))
                .andExpect(flash().attribute("MSG_ERROR", "Reference warning"));
    }
}
