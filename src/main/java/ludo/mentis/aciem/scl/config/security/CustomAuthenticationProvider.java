package ludo.mentis.aciem.scl.config.security;

import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.repos.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final LdapAuthenticationProvider ldapAuthenticationProvider;

    public CustomAuthenticationProvider(UserRepository userRepository,
                                        DaoAuthenticationProvider daoAuthenticationProvider,
                                        LdapAuthenticationProvider ldapAuthenticationProvider) {
        this.userRepository = userRepository;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.ldapAuthenticationProvider = ldapAuthenticationProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        User user = userRepository.findByUsernameIgnoreCase(username);

        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        if (Boolean.TRUE.equals(user.getUseAD())) {
            return ldapAuthenticationProvider.authenticate(authentication);
        } else {
            return daoAuthenticationProvider.authenticate(authentication);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
