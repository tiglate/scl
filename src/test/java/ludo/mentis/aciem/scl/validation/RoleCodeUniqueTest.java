package ludo.mentis.aciem.scl.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import ludo.mentis.aciem.scl.model.RoleDTO;
import ludo.mentis.aciem.scl.service.RoleService;
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
class RoleCodeUniqueTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private RoleService roleService;

    @Test
    void roleCodeUnique_isValid() {
        var validator = new RoleCodeUnique.RoleCodeUniqueValidator(roleService, request);
        assertTrue(validator.isValid(null, context));

        String code = "ROLE_ADMIN";
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Collections.emptyMap());
        when(roleService.codeExists(code)).thenReturn(false);
        assertTrue(validator.isValid(code, context));

        when(roleService.codeExists(code)).thenReturn(true);
        assertFalse(validator.isValid(code, context));

        Long id = 1L;
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Map.of("id", id.toString()));
        RoleDTO dto = new RoleDTO();
        dto.setCode(code);
        when(roleService.get(id)).thenReturn(dto);
        assertTrue(validator.isValid(code, context));
    }

}
