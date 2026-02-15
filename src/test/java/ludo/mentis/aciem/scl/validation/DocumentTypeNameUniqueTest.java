package ludo.mentis.aciem.scl.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import ludo.mentis.aciem.scl.model.DocumentTypeDTO;
import ludo.mentis.aciem.scl.service.DocumentTypeService;
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
class DocumentTypeNameUniqueTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private DocumentTypeService documentTypeService;

    @Test
    void documentTypeNameUnique_isValid() {
        var validator = new DocumentTypeNameUnique.DocumentTypeNameUniqueValidator(documentTypeService, request);
        assertTrue(validator.isValid(null, context));

        String name = "Passport";
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Collections.emptyMap());
        when(documentTypeService.nameExists(name)).thenReturn(false);
        assertTrue(validator.isValid(name, context));

        when(documentTypeService.nameExists(name)).thenReturn(true);
        assertFalse(validator.isValid(name, context));

        Long id = 1L;
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Map.of("id", id.toString()));
        DocumentTypeDTO dto = new DocumentTypeDTO();
        dto.setName(name);
        when(documentTypeService.get(id)).thenReturn(dto);
        assertTrue(validator.isValid(name, context));
    }

}
