package ludo.mentis.aciem.scl.rest;

import ludo.mentis.aciem.scl.model.CustomUserDetails;
import ludo.mentis.aciem.scl.model.FxSettlementStepDTO;
import ludo.mentis.aciem.scl.service.FxSettlementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FxSettlementRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FxSettlementService fxSettlementService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FxSettlementRestController fxSettlementRestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fxSettlementRestController).build();
    }

    @Test
    void save_shouldReturnOk_whenSuccessful() throws Exception {
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(1L);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        MockMultipartFile details = new MockMultipartFile("details", "", MediaType.APPLICATION_JSON_VALUE,
                "{\"currentStep\":\"INS\", \"fxTradeId\":1}".getBytes());

        mockMvc.perform(multipart("/api/v1/fxSettlements/step")
                        .file(file)
                        .file(details)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("Settlement step processed successfully"));

        verify(fxSettlementService).save(any(FxSettlementStepDTO.class), eq(file));
    }

    @Test
    void save_shouldReturnError_whenServiceThrowsException() throws Exception {
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(1L);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        doThrow(new RuntimeException("Error")).when(fxSettlementService).save(any(), any());

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        MockMultipartFile details = new MockMultipartFile("details", "", MediaType.APPLICATION_JSON_VALUE,
                "{\"currentStep\":\"INS\", \"fxTradeId\":1}".getBytes());

        mockMvc.perform(multipart("/api/v1/fxSettlements/step")
                        .file(file)
                        .file(details)
                        .principal(authentication))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error processing settlement step: Error"));
    }

    @Test
    void list_shouldReturnSteps() throws Exception {
        when(fxSettlementService.findAllBySearchCriteria(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/fxSettlements/steps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void viewStep_shouldReturnHistory() throws Exception {
        when(fxSettlementService.viewStep(anyLong(), anyString())).thenReturn(null);

        mockMvc.perform(get("/api/v1/fxSettlements/view")
                        .param("fxSettlementId", "1")
                        .param("step", "INS"))
                .andExpect(status().isOk());
    }

    @Test
    void getLastTradeDate_shouldReturnDate() throws Exception {
        LocalDate now = LocalDate.now();
        when(fxSettlementService.getLastTradeDate()).thenReturn(now);

        mockMvc.perform(get("/api/v1/fxSettlements/lastTradeDate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void rollbackStep_shouldReturnOk() throws Exception {
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(1L);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        mockMvc.perform(post("/api/v1/fxSettlements/rollbackStep")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fxSettlementId\":1, \"currentStep\":\"G10\"}")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("Settlement step rolled back successfully"));

        verify(fxSettlementService).rollbackStep(eq(1L), eq("G10"), eq(1L));
    }

    @Test
    void getHistory_shouldReturnList() throws Exception {
        when(fxSettlementService.getHistoryByFxSettlementId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/fxSettlements/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
