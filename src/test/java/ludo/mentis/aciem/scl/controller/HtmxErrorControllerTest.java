package ludo.mentis.aciem.scl.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HtmxErrorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BasicErrorController basicErrorController;

    @InjectMocks
    private HtmxErrorController htmxErrorController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(htmxErrorController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void errorHtmx_shouldDelegateToBasicErrorController() throws Exception {
        when(basicErrorController.errorHtml(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(new ModelAndView("error"));

        mockMvc.perform(get("/error")
                        .header("HX-Request", "true"))
                .andExpect(status().isOk());

        verify(basicErrorController).errorHtml(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
