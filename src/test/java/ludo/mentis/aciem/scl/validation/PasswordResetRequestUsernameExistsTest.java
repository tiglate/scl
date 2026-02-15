package ludo.mentis.aciem.scl.validation;

import jakarta.validation.ConstraintValidatorContext;
import ludo.mentis.aciem.scl.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetRequestUsernameExistsTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private UserRepository userRepository;

    @Test
    void passwordResetRequestUsernameExists_isValid() {
        var validator = new PasswordResetRequestUsernameExists.PasswordResetRequestUsernameExistsValidator(userRepository);
        assertTrue(validator.isValid(null, context));
        assertTrue(validator.isValid("invalid-email", context));

        String email = "test@example.com";
        when(userRepository.existsByEmailIgnoreCase(email)).thenReturn(true);
        assertTrue(validator.isValid(email, context));

        when(userRepository.existsByEmailIgnoreCase(email)).thenReturn(false);
        assertFalse(validator.isValid(email, context));
    }

}
