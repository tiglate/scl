package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.model.PasswordResetCompleteRequest;
import ludo.mentis.aciem.scl.model.PasswordResetRequest;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.service.FileDataService;
import ludo.mentis.aciem.scl.service.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordResetController.class)
@Import(TestSecurityConfig.class)
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private FileDataService fileDataService;

    @Test
    void testStartGet() throws Exception {
        mockMvc.perform(get("/passwordReset/start"))
                .andExpect(status().isOk())
                .andExpect(view().name("passwordReset/start"))
                .andExpect(model().attributeExists("passwordResetRequest"));
    }

    @Test
    void testStartPost_Success() throws Exception {
        when(userRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);

        mockMvc.perform(post("/passwordReset/start")
                        .with(csrf())
                        .param("email", "test@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("MSG_INFO", "Password reset process started. Please check your e-mail."));

        verify(passwordResetService).startProcess(any(PasswordResetRequest.class));
    }

    @Test
    void testStartPost_BindingErrors() throws Exception {
        mockMvc.perform(post("/passwordReset/start")
                        .with(csrf())
                        .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("passwordReset/start"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testCompleteGet_Success() throws Exception {
        UUID uid = UUID.randomUUID();
        when(passwordResetService.isValidPasswordResetUid(uid)).thenReturn(true);

        mockMvc.perform(get("/passwordReset/complete").param("uid", uid.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("passwordReset/complete"))
                .andExpect(model().attributeExists("passwordResetCompleteRequest"));
    }

    @Test
    void testCompleteGet_InvalidUid() throws Exception {
        UUID uid = UUID.randomUUID();
        when(passwordResetService.isValidPasswordResetUid(uid)).thenReturn(false);

        mockMvc.perform(get("/passwordReset/complete").param("uid", uid.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("MSG_ERROR", "Invalid or expired password reset request."));
    }

    @Test
    void testCompletePost_Success() throws Exception {
        UUID uid = UUID.randomUUID();
        mockMvc.perform(post("/passwordReset/complete")
                        .with(csrf())
                        .param("uid", uid.toString())
                        .param("newPassword", "newPassword123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("MSG_SUCCESS", "Password was updated successfully."));

        verify(passwordResetService).completeProcess(any(PasswordResetCompleteRequest.class));
    }

    @Test
    void testCompletePost_BindingErrors() throws Exception {
        mockMvc.perform(post("/passwordReset/complete")
                        .with(csrf())
                        .param("newPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("passwordReset/complete"))
                .andExpect(model().hasErrors());
    }
}
