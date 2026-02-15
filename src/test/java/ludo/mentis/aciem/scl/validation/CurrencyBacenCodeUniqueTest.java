package ludo.mentis.aciem.scl.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import ludo.mentis.aciem.scl.model.CurrencyDTO;
import ludo.mentis.aciem.scl.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
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
class CurrencyBacenCodeUniqueTest {

    @Mock
    private CurrencyService currencyService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    private CurrencyBacenCodeUnique.CurrencyBacenCodeUniqueValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CurrencyBacenCodeUnique.CurrencyBacenCodeUniqueValidator(currencyService, request);
    }

    @Test
    void isValid_NullValue_ReturnsTrue() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void isValid_NewValueNotExists_ReturnsTrue() {
        String bacenCode = "123";
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Collections.emptyMap());
        when(currencyService.bacenCodeExists(bacenCode)).thenReturn(false);

        assertTrue(validator.isValid(bacenCode, context));
    }

    @Test
    void isValid_NewValueExists_ReturnsFalse() {
        String bacenCode = "123";
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Collections.emptyMap());
        when(currencyService.bacenCodeExists(bacenCode)).thenReturn(true);

        assertFalse(validator.isValid(bacenCode, context));
    }

    @Test
    void isValid_SameValueAsCurrentRecord_ReturnsTrue() {
        String bacenCode = "123";
        Long currentId = 1L;
        Map<String, String> pathVariables = Collections.singletonMap("id", currentId.toString());
        
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);
        
        CurrencyDTO currentCurrency = new CurrencyDTO();
        currentCurrency.setBacenCode(bacenCode);
        when(currencyService.get(currentId)).thenReturn(currentCurrency);

        assertTrue(validator.isValid(bacenCode, context));
    }

    @Test
    void isValid_DifferentValueThanCurrentRecord_Exists_ReturnsFalse() {
        String newValue = "456";
        String oldValue = "123";
        Long currentId = 1L;
        Map<String, String> pathVariables = Collections.singletonMap("id", currentId.toString());

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);

        CurrencyDTO currentCurrency = new CurrencyDTO();
        currentCurrency.setBacenCode(oldValue);
        when(currencyService.get(currentId)).thenReturn(currentCurrency);
        when(currencyService.bacenCodeExists(newValue)).thenReturn(true);

        assertFalse(validator.isValid(newValue, context));
    }
}
