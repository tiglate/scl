package ludo.mentis.aciem.scl.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldsNotEqualValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void fieldsNotEqual_isValid() {
        var validator = new FieldsNotEqualValidator();
        FieldsNotEqual annotation = mock(FieldsNotEqual.class);
        when(annotation.field()).thenReturn("field1");
        when(annotation.secondField()).thenReturn("field2");
        validator.initialize(annotation);

        assertTrue(validator.isValid(null, context));

        class TestBean {
            private String field1;
            private String field2;
            public TestBean(String f1, String f2) { this.field1 = f1; this.field2 = f2; }
            public String getField1() { return field1; }
            public String getField2() { return field2; }
        }

        assertTrue(validator.isValid(new TestBean("a", "b"), context));
        assertFalse(validator.isValid(new TestBean("a", "a"), context));
        assertTrue(validator.isValid(new TestBean(null, "b"), context));
        assertFalse(validator.isValid(new TestBean(null, null), context));
    }

}
