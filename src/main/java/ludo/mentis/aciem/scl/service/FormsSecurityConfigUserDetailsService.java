package ludo.mentis.aciem.scl.service;

import java.util.List;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.FormsSecurityConfigUserDetails;
import ludo.mentis.aciem.scl.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class FormsSecurityConfigUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(FormsSecurityConfigUserDetailsService.class);

    private final UserRepository userRepository;

    public FormsSecurityConfigUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public FormsSecurityConfigUserDetails loadUserByUsername(final String username) {
        final User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null) {
            log.warn("user not found: {}", username);
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        final List<SimpleGrantedAuthority> authorities = user.getRoles() == null ? List.of() : 
                user.getRoles()
                .stream()
                .map(roleRef -> new SimpleGrantedAuthority(roleRef.getCode()))
                .toList();
        return new FormsSecurityConfigUserDetails(user.getId(), username, user.getPassword(), authorities);
    }

}
