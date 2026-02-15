package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.model.UserDTO;
import ludo.mentis.aciem.scl.model.UserSearchDTO;
import ludo.mentis.aciem.scl.repos.DepartmentRepository;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.service.FileDataService;
import ludo.mentis.aciem.scl.service.UserService;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import ludo.mentis.aciem.scl.util.UserRoles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@WithMockUser(authorities = UserRoles.ADMIN)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private FileDataService fileDataService;

    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private DepartmentRepository departmentRepository;

    @Test
    void testList() throws Exception {
        when(departmentRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());
        when(roleRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());
        when(userService.findAll(any(UserSearchDTO.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users", "filter"));
    }

    @Test
    void testView() throws Exception {
        when(departmentRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());
        when(roleRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());
        when(userService.get(1L)).thenReturn(new UserDTO());

        mockMvc.perform(get("/users/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/view"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testAddGet() throws Exception {
        when(departmentRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());
        when(roleRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testAddPost_Success() throws Exception {
        mockMvc.perform(post("/users/add")
                        .with(csrf())
                        .param("name", "John Doe")
                        .param("email", "john@example.com")
                        .param("gender", "MALE")
                        .param("username", "johndoe")
                        .param("password", "password")
                        .param("enabled", "true")
                        .param("departmentId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(userService).create(any(UserDTO.class));
    }

    @Test
    void testAddPost_BindingErrors() throws Exception {
        when(departmentRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());
        when(roleRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/users/add")
                        .with(csrf())
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testEditGet() throws Exception {
        when(departmentRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());
        when(roleRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());
        when(userService.get(1L)).thenReturn(new UserDTO());

        mockMvc.perform(get("/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/edit"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testEditPost_Success() throws Exception {
        mockMvc.perform(post("/users/edit/1")
                        .with(csrf())
                        .param("name", "John Doe Updated")
                        .param("email", "john_upd@example.com")
                        .param("gender", "MALE")
                        .param("username", "johndoe")
                        .param("enabled", "true")
                        .param("departmentId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(userService).update(eq(1L), any(UserDTO.class));
    }

    @Test
    void testDelete_Success() throws Exception {
        when(userService.getReferencedWarning(1L)).thenReturn(null);

        mockMvc.perform(post("/users/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(userService).delete(1L);
    }

    @Test
    void testDelete_Referenced() throws Exception {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setMessage("Reference error");
        when(userService.getReferencedWarning(1L)).thenReturn(warning);

        mockMvc.perform(post("/users/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attribute("MSG_ERROR", "Reference error"));
    }
}
