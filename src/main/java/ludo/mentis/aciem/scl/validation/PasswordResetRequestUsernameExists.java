package ludo.mentis.aciem.scl.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.WebUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;


/**
 * Validate that there is an account for the given e-mail.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = PasswordResetRequestUsernameExists.PasswordResetRequestUsernameExistsValidator.class
)
public @interface PasswordResetRequestUsernameExists {

    String message() default "No account found for the given e-mail.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class PasswordResetRequestUsernameExistsValidator implements ConstraintValidator<PasswordResetRequestUsernameExists, String> {

        private final UserRepository userRepository;

        public PasswordResetRequestUsernameExistsValidator(final UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null || !value.matches(WebUtils.EMAIL_PATTERN)) {
                // no valid value present
                return true;
            }
            return userRepository.existsByEmailIgnoreCase(value);
        }

    }

}
