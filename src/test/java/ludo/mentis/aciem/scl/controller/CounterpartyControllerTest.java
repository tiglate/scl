package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.model.CounterpartyDTO;
import ludo.mentis.aciem.scl.repos.DocumentTypeRepository;
import ludo.mentis.aciem.scl.service.CounterpartyService;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CounterpartyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CounterpartyService counterpartyService;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @InjectMocks
    private CounterpartyController counterpartyController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(counterpartyController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void list_shouldReturnListView() throws Exception {
        Page<CounterpartyDTO> page = new PageImpl<>(List.of(new CounterpartyDTO()), PageRequest.of(0, 20), 1);
        when(counterpartyService.findAll(any(CounterpartyDTO.class), any(Pageable.class))).thenReturn(page);
        when(documentTypeRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        mockMvc.perform(get("/counterparties"))
                .andExpect(status().isOk())
                .andExpect(view().name("counterparty/list"))
                .andExpect(model().attributeExists("counterparties"))
                .andExpect(model().attributeExists("filter"))
                .andExpect(model().attributeExists("paginationModel"));
    }

    @Test
    void view_shouldReturnViewView() throws Exception {
        CounterpartyDTO dto = new CounterpartyDTO();
        dto.setId(1L);
        when(counterpartyService.get(1L)).thenReturn(dto);
        when(documentTypeRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        mockMvc.perform(get("/counterparties/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("counterparty/view"))
                .andExpect(model().attributeExists("counterparty"));
    }

    @Test
    void edit_shouldReturnEditView() throws Exception {
        CounterpartyDTO dto = new CounterpartyDTO();
        dto.setId(1L);
        when(counterpartyService.get(1L)).thenReturn(dto);
        when(documentTypeRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        mockMvc.perform(get("/counterparties/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("counterparty/edit"))
                .andExpect(model().attributeExists("counterparty"));
    }

    @Test
    void add_shouldReturnAddView() throws Exception {
        when(documentTypeRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        mockMvc.perform(get("/counterparties/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("counterparty/add"))
                .andExpect(model().attributeExists("counterparty"));
    }

    @Test
    void addPost_shouldRedirectOnSuccess() throws Exception {
        mockMvc.perform(post("/counterparties/add")
                        .param("longName", "Test Counterparty")
                        .param("isActive", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/counterparties"));

        verify(counterpartyService).create(any(CounterpartyDTO.class));
    }

    @Test
    void addPost_shouldReturnAddViewOnValidationError() throws Exception {
        when(documentTypeRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        mockMvc.perform(post("/counterparties/add")
                        .param("longName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("counterparty/add"));

        verify(counterpartyService, never()).create(any());
    }

    @Test
    void editPost_shouldRedirectOnSuccess() throws Exception {
        mockMvc.perform(post("/counterparties/edit/1")
                        .param("longName", "Updated Name")
                        .param("isActive", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/counterparties"));

        verify(counterpartyService).update(eq(1L), any(CounterpartyDTO.class));
    }

    @Test
    void delete_shouldRedirectOnSuccess() throws Exception {
        when(counterpartyService.getReferencedWarning(1L)).thenReturn(null);

        mockMvc.perform(post("/counterparties/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/counterparties"));

        verify(counterpartyService).delete(1L);
    }

    @Test
    void delete_shouldShowWarningWhenReferenced() throws Exception {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setMessage("Referenced");
        when(counterpartyService.getReferencedWarning(1L)).thenReturn(warning);

        mockMvc.perform(post("/counterparties/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/counterparties"))
                .andExpect(flash().attributeExists("MSG_ERROR"));

        verify(counterpartyService, never()).delete(1L);
    }
}
