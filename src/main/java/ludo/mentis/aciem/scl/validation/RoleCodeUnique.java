package ludo.mentis.aciem.scl.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import ludo.mentis.aciem.scl.service.RoleService;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.annotation.ElementType.*;


/**
 * Validate that the code value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = RoleCodeUnique.RoleCodeUniqueValidator.class
)
public @interface RoleCodeUnique {

    String message() default "This Code is already taken.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class RoleCodeUniqueValidator implements ConstraintValidator<RoleCodeUnique, String> {

        private final RoleService roleService;
        private final HttpServletRequest request;

        public RoleCodeUniqueValidator(final RoleService roleService,
                final HttpServletRequest request) {
            this.roleService = roleService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equalsIgnoreCase(roleService.get(Long.parseLong(currentId)).getCode())) {
                // value hasn't changed
                return true;
            }
            return !roleService.codeExists(value);
        }

    }

}
