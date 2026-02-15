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
class UserUsernameUniqueTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private UserService userService;

    @Test
    void userUsernameUnique_isValid() {
        var validator = new UserUsernameUnique.UserUsernameUniqueValidator(userService, request);
        assertTrue(validator.isValid(null, context));

        String username = "johndoe";
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Collections.emptyMap());
        when(userService.usernameExists(username)).thenReturn(false);
        assertTrue(validator.isValid(username, context));

        when(userService.usernameExists(username)).thenReturn(true);
        assertFalse(validator.isValid(username, context));

        Long id = 1L;
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Map.of("id", id.toString()));
        UserDTO dto = new UserDTO();
        dto.setUsername(username);
        when(userService.get(id)).thenReturn(dto);
        assertTrue(validator.isValid(username, context));
    }

}
