package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.model.FileContentDTO;
import ludo.mentis.aciem.scl.service.FxSettlementService;
import ludo.mentis.aciem.scl.util.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FxSettlementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FxSettlementService fxSettlementService;

    @InjectMocks
    private FxSettlementController fxSettlementController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(fxSettlementController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void list_shouldReturnListView() throws Exception {
        mockMvc.perform(get("/fxSettlements"))
                .andExpect(status().isOk())
                .andExpect(view().name("fxSettlement/list"));
    }

    @Test
    void download_shouldReturnFileContent() throws Exception {
        UUID id = UUID.randomUUID();
        FileContentDTO dto = new FileContentDTO();
        dto.setFileName("test.png");
        Blob blob = mock(Blob.class);
        byte[] content = "test content".getBytes();
        when(blob.getBinaryStream()).thenReturn(new ByteArrayInputStream(content));
        when(blob.length()).thenReturn((long) content.length);
        dto.setContent(blob);

        when(fxSettlementService.getFile(id)).thenReturn(dto);

        mockMvc.perform(get("/fxSettlements/download/" + id))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.png\""))
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(content));
    }

    @Test
    void download_shouldThrowNotFoundExceptionWhenBlobIsNull() throws Exception {
        UUID id = UUID.randomUUID();
        FileContentDTO dto = new FileContentDTO();
        dto.setContent(null);

        when(fxSettlementService.getFile(id)).thenReturn(dto);

        mockMvc.perform(get("/fxSettlements/download/" + id))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    void download_shouldReturnInternalServerErrorOnSQLException() throws Exception {
        UUID id = UUID.randomUUID();
        FileContentDTO dto = new FileContentDTO();
        dto.setFileName("test.png");
        Blob blob = mock(Blob.class);
        when(blob.getBinaryStream()).thenThrow(new SQLException("DB error"));
        dto.setContent(blob);

        when(fxSettlementService.getFile(id)).thenReturn(dto);

        mockMvc.perform(get("/fxSettlements/download/" + id))
                .andExpect(status().isInternalServerError());
    }
}
