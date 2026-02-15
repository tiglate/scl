package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.model.CurrencyDTO;
import ludo.mentis.aciem.scl.service.CurrencyService;
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

@WebMvcTest(CurrencyController.class)
@Import(TestSecurityConfig.class)
@WithMockUser(authorities = UserRoles.ADMIN)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrencyService currencyService;

    @MockitoBean
    private FileDataService fileDataService;

    @Test
    void testList() throws Exception {
        when(currencyService.findAll(any(CurrencyDTO.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/currencies").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("currency/list"))
                .andExpect(model().attributeExists("currencies", "filter"));
    }

    @Test
    void testView() throws Exception {
        when(currencyService.get(1L)).thenReturn(new CurrencyDTO());

        mockMvc.perform(get("/currencies/view/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("currency/view"))
                .andExpect(model().attributeExists("currency"));
    }

    @Test
    void testEditGet() throws Exception {
        when(currencyService.get(1L)).thenReturn(new CurrencyDTO());

        mockMvc.perform(get("/currencies/edit/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("currency/edit"))
                .andExpect(model().attributeExists("currency"));
    }

    @Test
    void testAddGet() throws Exception {
        mockMvc.perform(get("/currencies/add").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(view().name("currency/add"))
                .andExpect(model().attributeExists("currency"));
    }

    @Test
    void testAddPost_Success() throws Exception {
        mockMvc.perform(post("/currencies/add").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf())
                        .param("isoCode", "USD")
                        .param("bacenCode", "220")
                        .param("name", "US Dollar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/currencies"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(currencyService).create(any(CurrencyDTO.class));
    }

    @Test
    void testEditPost_Success() throws Exception {
        CurrencyDTO existing = new CurrencyDTO();
        existing.setIsoCode("USD");
        existing.setBacenCode("220");
        when(currencyService.get(1L)).thenReturn(existing);

        mockMvc.perform(post("/currencies/edit/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf())
                        .param("isoCode", "USD")
                        .param("bacenCode", "220")
                        .param("name", "US Dollar Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/currencies"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(currencyService).update(eq(1L), any(CurrencyDTO.class));
    }

    @Test
    void testDelete_Success() throws Exception {
        when(currencyService.getReferencedWarning(1L)).thenReturn(null);

        mockMvc.perform(post("/currencies/delete/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/currencies"))
                .andExpect(flash().attributeExists("MSG_SUCCESS"));

        verify(currencyService).delete(1L);
    }

    @Test
    void testDelete_Referenced() throws Exception {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setMessage("Reference warning");
        when(currencyService.getReferencedWarning(1L)).thenReturn(warning);

        mockMvc.perform(post("/currencies/delete/1").with(user(createCustomUserDetails("admin", List.of(UserRoles.ADMIN))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/currencies"))
                .andExpect(flash().attribute("MSG_ERROR", "Reference warning"));
    }
}
