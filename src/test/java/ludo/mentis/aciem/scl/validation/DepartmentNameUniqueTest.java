package ludo.mentis.aciem.scl.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import ludo.mentis.aciem.scl.model.DepartmentDTO;
import ludo.mentis.aciem.scl.service.DepartmentService;
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
class DepartmentNameUniqueTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private DepartmentService departmentService;

    @Test
    void departmentNameUnique_isValid() {
        var validator = new DepartmentNameUnique.DepartmentNameUniqueValidator(departmentService, request);
        assertTrue(validator.isValid(null, context));

        String name = "IT";
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Collections.emptyMap());
        when(departmentService.nameExists(name)).thenReturn(false);
        assertTrue(validator.isValid(name, context));

        when(departmentService.nameExists(name)).thenReturn(true);
        assertFalse(validator.isValid(name, context));

        Long id = 1L;
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Map.of("id", id.toString()));
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName(name);
        when(departmentService.get(id)).thenReturn(dto);
        assertTrue(validator.isValid(name, context));
    }

}
