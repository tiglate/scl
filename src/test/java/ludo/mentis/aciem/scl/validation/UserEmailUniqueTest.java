package ludo.mentis.aciem.scl.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import ludo.mentis.aciem.scl.model.UserDTO;
import ludo.mentis.aciem.scl.service.UserService;
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
class UserEmailUniqueTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private UserService userService;

    @Test
    void userEmailUnique_isValid() {
        var validator = new UserEmailUnique.UserEmailUniqueValidator(userService, request);
        assertTrue(validator.isValid(null, context));

        String email = "user@example.com";
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Collections.emptyMap());
        when(userService.emailExists(email)).thenReturn(false);
        assertTrue(validator.isValid(email, context));

        when(userService.emailExists(email)).thenReturn(true);
        assertFalse(validator.isValid(email, context));

        Long id = 1L;
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Map.of("id", id.toString()));
        UserDTO dto = new UserDTO();
        dto.setEmail(email);
        when(userService.get(id)).thenReturn(dto);
        assertTrue(validator.isValid(email, context));
    }

}
