package ludo.mentis.aciem.scl.config.security;

import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Mock
    private LdapAuthenticationProvider ldapAuthenticationProvider;

    @InjectMocks
    private CustomAuthenticationProvider customAuthenticationProvider;

    private UsernamePasswordAuthenticationToken authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        authRequest = new UsernamePasswordAuthenticationToken("user1", "pass1");
        user = new User();
        user.setUsername("user1");
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(userRepository.findByUsernameIgnoreCase("user1")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            customAuthenticationProvider.authenticate(authRequest);
        });
    }

    @Test
    void testAuthenticate_UseAD_True() {
        user.setUseAD(true);
        when(userRepository.findByUsernameIgnoreCase("user1")).thenReturn(user);
        Authentication expectedAuth = mock(Authentication.class);
        when(ldapAuthenticationProvider.authenticate(authRequest)).thenReturn(expectedAuth);

        Authentication result = customAuthenticationProvider.authenticate(authRequest);

        assertEquals(expectedAuth, result);
        verify(ldapAuthenticationProvider).authenticate(authRequest);
        verifyNoInteractions(daoAuthenticationProvider);
    }

    @Test
    void testAuthenticate_UseAD_False() {
        user.setUseAD(false);
        when(userRepository.findByUsernameIgnoreCase("user1")).thenReturn(user);
        Authentication expectedAuth = mock(Authentication.class);
        when(daoAuthenticationProvider.authenticate(authRequest)).thenReturn(expectedAuth);

        Authentication result = customAuthenticationProvider.authenticate(authRequest);

        assertEquals(expectedAuth, result);
        verify(daoAuthenticationProvider).authenticate(authRequest);
        verifyNoInteractions(ldapAuthenticationProvider);
    }

    @Test
    void testSupports() {
        assertTrue(customAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
        assertFalse(customAuthenticationProvider.supports(String.class));
    }
}
