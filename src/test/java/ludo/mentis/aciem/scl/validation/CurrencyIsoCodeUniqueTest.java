package ludo.mentis.aciem.scl.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import ludo.mentis.aciem.scl.model.CurrencyDTO;
import ludo.mentis.aciem.scl.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyIsoCodeUniqueTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private CurrencyService currencyService;

    @Test
    void currencyIsoCodeUnique_isValid() {
        var validator = new CurrencyIsoCodeUnique.CurrencyIsoCodeUniqueValidator(currencyService, request);
        assertTrue(validator.isValid(null, context));
        
        String isoCode = "USD";
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Collections.emptyMap());
        when(currencyService.isoCodeExists(isoCode)).thenReturn(false);
        assertTrue(validator.isValid(isoCode, context));

        when(currencyService.isoCodeExists(isoCode)).thenReturn(true);
        assertFalse(validator.isValid(isoCode, context));

        Long id = 1L;
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Map.of("id", id.toString()));
        CurrencyDTO dto = new CurrencyDTO();
        dto.setIsoCode(isoCode);
        when(currencyService.get(id)).thenReturn(dto);
        assertTrue(validator.isValid(isoCode, context));
    }

}
