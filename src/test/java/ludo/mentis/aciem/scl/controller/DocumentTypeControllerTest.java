package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.model.DocumentTypeDTO;
import ludo.mentis.aciem.scl.service.DocumentTypeService;
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

@WebMvcTest(DocumentTypeController.class)
@Import(TestSecurityConfig.class)
@WithMockUser(authorities = UserRoles.ADMIN)
class DocumentTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentTypeService documentTypeService;

    @MockitoBean
    private FileDataService fileDataService;

    @Test
    void testList() throws Exception {
        when(documentTypeService.findAll(any(DocumentTypeDTO.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/documentTypes").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("documentType/list"))
                .andExpect(model().attributeExists("documentTypes", "filter"));
    }

    @Test
    void testView() throws Exception {
        when(documentTypeService.get(1L)).thenReturn(new DocumentTypeDTO());

        mockMvc.perform(get("/documentTypes/view/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("documentType/view"))
                .andExpect(model().attributeExists("documentType"));
    }

    @Test
    void testEditGet() throws Exception {
        when(documentTypeService.get(1L)).thenReturn(new DocumentTypeDTO());

        mockMvc.perform(get("/documentTypes/edit/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("documentType/edit"))
                .andExpect(model().attributeExists("documentType"));
    }

    @Test
    void testAddGet() throws Exception {
        mockMvc.perform(get("/documentTypes/add").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("documentType/add"))
                .andExpect(model().attributeExists("documentType"));
    }

    @Test
    void testAddPost_Success() throws Exception {
        mockMvc.perform(post("/documentTypes/add").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf())
                        .param("name", "Passport"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documentTypes"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(documentTypeService).create(any(DocumentTypeDTO.class));
    }

    @Test
    void testEditPost_Success() throws Exception {
        DocumentTypeDTO existing = new DocumentTypeDTO();
        existing.setName("Passport");
        when(documentTypeService.get(1L)).thenReturn(existing);

        mockMvc.perform(post("/documentTypes/edit/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf())
                        .param("name", "Passport"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documentTypes"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(documentTypeService).update(eq(1L), any(DocumentTypeDTO.class));
    }

    @Test
    void testDelete_Success() throws Exception {
        when(documentTypeService.getReferencedWarning(1L)).thenReturn(null);

        mockMvc.perform(post("/documentTypes/delete/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documentTypes"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(documentTypeService).delete(1L);
    }

    @Test
    void testDelete_Referenced() throws Exception {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setMessage("Reference warning");
        when(documentTypeService.getReferencedWarning(1L)).thenReturn(warning);

        mockMvc.perform(post("/documentTypes/delete/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documentTypes"))
                .andExpect(flash().attribute("MSG_ERROR", "Reference warning"));
    }
}
