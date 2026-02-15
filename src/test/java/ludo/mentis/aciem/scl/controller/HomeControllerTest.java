package ludo.mentis.aciem.scl.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.CustomUserDetails;
import ludo.mentis.aciem.scl.service.FileDataService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
@Import(TestSecurityConfig.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileDataService fileDataService;

    @Test
    void testIndex_Unauthenticated_ShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testIndex_Authenticated_ShouldBeOk() throws Exception {
        User user = new User();
        user.setName("John Doe");
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        mockMvc.perform(get("/").with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("home/index"));
    }
}
