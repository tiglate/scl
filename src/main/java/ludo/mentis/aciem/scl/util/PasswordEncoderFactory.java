package ludo.mentis.aciem.scl.util;

import org.springframework.security.crypto.password.PasswordEncoder;

public interface PasswordEncoderFactory {
    PasswordEncoder create();
}
