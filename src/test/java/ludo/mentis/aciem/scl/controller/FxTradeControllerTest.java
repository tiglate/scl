package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.domain.FxTradeView;
import ludo.mentis.aciem.scl.model.FxTradeDTO;
import ludo.mentis.aciem.scl.model.FxTradeSearchDTO;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.service.FxTradeService;
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
class FxTradeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FxTradeService fxTradeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CounterpartyRepository counterpartyRepository;

    @InjectMocks
    private FxTradeController fxTradeController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(fxTradeController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void list_shouldReturnListView() throws Exception {
        Page<FxTradeView> page = new PageImpl<>(List.of(new FxTradeView()), PageRequest.of(0, 18), 1);
        when(fxTradeService.findAll(any(FxTradeSearchDTO.class), any(Pageable.class))).thenReturn(page);
        when(counterpartyRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());
        when(currencyRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());
        when(userRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        mockMvc.perform(get("/fxTrades"))
                .andExpect(status().isOk())
                .andExpect(view().name("fxTrade/list"))
                .andExpect(model().attributeExists("fxTrades"))
                .andExpect(model().attributeExists("filter"))
                .andExpect(model().attributeExists("paginationModel"));
    }

    @Test
    void view_shouldReturnViewView() throws Exception {
        FxTradeDTO dto = new FxTradeDTO();
        dto.setId(1L);
        when(fxTradeService.get(1L)).thenReturn(dto);
        when(counterpartyRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());
        when(currencyRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());
        when(userRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        mockMvc.perform(get("/fxTrades/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("fxTrade/view"))
                .andExpect(model().attributeExists("fxTrade"));
    }

    @Test
    void edit_shouldReturnEditView() throws Exception {
        FxTradeDTO dto = new FxTradeDTO();
        dto.setId(1L);
        when(fxTradeService.get(1L)).thenReturn(dto);
        when(counterpartyRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());
        when(currencyRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());
        when(userRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        mockMvc.perform(get("/fxTrades/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("fxTrade/edit"))
                .andExpect(model().attributeExists("fxTrade"));
    }

    @Test
    void add_shouldReturnAddView() throws Exception {
        when(counterpartyRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());
        when(currencyRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());
        when(userRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        mockMvc.perform(get("/fxTrades/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("fxTrade/add"))
                .andExpect(model().attributeExists("fxTrade"));
    }

    @Test
    void addPost_shouldRedirectOnSuccess() throws Exception {
        mockMvc.perform(post("/fxTrades/add")
                        .param("tradeDate", "2026-02-22")
                        .param("valueDate", "2026-02-24")
                        .param("product", "FX_SPOT")
                        .param("buyCurrencyId", "1")
                        .param("buyAmount", "100")
                        .param("sellCurrencyId", "2")
                        .param("sellAmount", "200")
                        .param("exchangeRate", "0.5")
                        .param("counterpartyId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fxTrades"));

        verify(fxTradeService).create(any(FxTradeDTO.class));
    }

    @Test
    void editPost_shouldRedirectOnSuccess() throws Exception {
        mockMvc.perform(post("/fxTrades/edit/1")
                        .param("tradeDate", "2026-02-22")
                        .param("valueDate", "2026-02-24")
                        .param("product", "FX_SPOT")
                        .param("buyCurrencyId", "1")
                        .param("buyAmount", "100")
                        .param("sellCurrencyId", "2")
                        .param("sellAmount", "200")
                        .param("exchangeRate", "0.5")
                        .param("counterpartyId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fxTrades"));

        verify(fxTradeService).update(eq(1L), any(FxTradeDTO.class));
    }

    @Test
    void delete_shouldRedirectOnSuccess() throws Exception {
        when(fxTradeService.getReferencedWarning(1L)).thenReturn(null);

        mockMvc.perform(post("/fxTrades/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fxTrades"));

        verify(fxTradeService).delete(1L);
    }

    @Test
    void delete_shouldShowWarningWhenReferenced() throws Exception {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setMessage("Referenced");
        when(fxTradeService.getReferencedWarning(1L)).thenReturn(warning);

        mockMvc.perform(post("/fxTrades/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fxTrades"))
                .andExpect(flash().attributeExists("MSG_ERROR"));

        verify(fxTradeService, never()).delete(1L);
    }
}
