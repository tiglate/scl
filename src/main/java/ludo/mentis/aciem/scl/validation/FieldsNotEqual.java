package ludo.mentis.aciem.scl.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldsNotEqualValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsNotEqual {

    String message() default "Fields must not be equal";

    String field();

    String secondField();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        FieldsNotEqual[] value();
    }
}