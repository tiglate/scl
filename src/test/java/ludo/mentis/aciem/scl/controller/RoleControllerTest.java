package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.model.RoleDTO;
import ludo.mentis.aciem.scl.service.FileDataService;
import ludo.mentis.aciem.scl.service.RoleService;
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
import java.util.List;

import static ludo.mentis.aciem.scl.controller.TestSecurityConfig.createCustomUserDetails;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@Import(TestSecurityConfig.class)
@WithMockUser(authorities = UserRoles.ADMIN)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private FileDataService fileDataService;

    @Test
    void testList() throws Exception {
        when(roleService.findAll(any(RoleDTO.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/roles").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("role/list"))
                .andExpect(model().attributeExists("roles", "filter"));
    }

    @Test
    void testView() throws Exception {
        when(roleService.get(1L)).thenReturn(new RoleDTO());

        mockMvc.perform(get("/roles/view/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("role/view"))
                .andExpect(model().attributeExists("role"));
    }

    @Test
    void testAddGet() throws Exception {
        mockMvc.perform(get("/roles/add").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("role/add"))
                .andExpect(model().attributeExists("role"));
    }

    @Test
    void testAddPost_Success() throws Exception {
        mockMvc.perform(post("/roles/add").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf())
                        .param("code", "ROLE_TEST")
                        .param("description", "Test Role"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/roles"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(roleService).create(any(RoleDTO.class));
    }

    @Test
    void testAddPost_BindingErrors() throws Exception {
        mockMvc.perform(post("/roles/add").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf())
                        .param("code", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("role/add"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testEditGet() throws Exception {
        when(roleService.get(1L)).thenReturn(new RoleDTO());

        mockMvc.perform(get("/roles/edit/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("role/edit"))
                .andExpect(model().attributeExists("role"));
    }

    @Test
    void testEditPost_Success() throws Exception {
        RoleDTO existingRole = new RoleDTO();
        existingRole.setCode("ROLE_UPDATED");
        when(roleService.get(1L)).thenReturn(existingRole);

        mockMvc.perform(post("/roles/edit/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf())
                        .param("code", "ROLE_UPDATED")
                        .param("description", "Updated Role"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/roles"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(roleService).update(eq(1L), any(RoleDTO.class));
    }

    @Test
    void testDelete_Success() throws Exception {
        when(roleService.getReferencedWarning(1L)).thenReturn(null);

        mockMvc.perform(post("/roles/delete/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/roles"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(roleService).delete(1L);
    }

    @Test
    void testDelete_Referenced() throws Exception {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setMessage("Reference warning");
        when(roleService.getReferencedWarning(1L)).thenReturn(warning);

        mockMvc.perform(post("/roles/delete/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/roles"))
                .andExpect(flash().attribute("MSG_ERROR", "Reference warning"));
    }
}
