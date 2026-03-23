package ludo.mentis.aciem.scl.util;

import org.springframework.security.crypto.password.PasswordEncoder;

public class FakePasswordEncoderFactory implements PasswordEncoderFactory {
    @Override
    public PasswordEncoder create() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return "encodedPassword";
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.contentEquals(rawPassword);
            }
        };
    }
}
