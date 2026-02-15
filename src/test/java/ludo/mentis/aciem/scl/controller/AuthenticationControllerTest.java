package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.service.FileDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@Import(TestSecurityConfig.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileDataService fileDataService;

    @Test
    void testLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("authentication/login"))
                .andExpect(model().attributeExists("authentication"));
    }

    @Test
    void testLogin_LoginRequired() throws Exception {
        mockMvc.perform(get("/login").param("loginRequired", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("MSG_INFO", "Login required for the requested page."));
    }

    @Test
    void testLogin_LoginError() throws Exception {
        mockMvc.perform(get("/login").param("loginError", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("MSG_ERROR", "Invalid username or password."));
    }
}
