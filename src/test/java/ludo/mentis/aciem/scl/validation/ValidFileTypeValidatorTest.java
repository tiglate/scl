package ludo.mentis.aciem.scl.validation;

import jakarta.validation.ConstraintValidatorContext;
import ludo.mentis.aciem.scl.model.FileData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidFileTypeValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void validFileType_isValid() {
        var validator = new ValidFileType.ValidFileTypeValidator();
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.value()).thenReturn(new String[]{"pdf", "jpg"});
        validator.initialize(annotation);

        assertTrue(validator.isValid(null, context));
        
        FileData fileData = new FileData();
        assertTrue(validator.isValid(fileData, context));

        fileData.setFileName("test.pdf");
        assertTrue(validator.isValid(fileData, context));

        fileData.setFileName("test.JPG");
        assertTrue(validator.isValid(fileData, context));

        fileData.setFileName("test.txt");
        assertFalse(validator.isValid(fileData, context));
    }

}
