package ludo.mentis.aciem.scl.util;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderFactoryImpl implements PasswordEncoderFactory {

    @Override
    public PasswordEncoder create() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
