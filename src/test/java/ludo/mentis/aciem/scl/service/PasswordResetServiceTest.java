package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.PasswordResetCompleteRequest;
import ludo.mentis.aciem.scl.model.PasswordResetRequest;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.WebUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private MailService mailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void testStartProcess_UserNotFound() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("unknown@example.com");

        when(userRepository.findByEmailIgnoreCase(request.getEmail())).thenReturn(null);

        passwordResetService.startProcess(request);

        verify(userRepository, never()).save(any());
        verify(mailService, never()).sendMail(anyString(), anyString(), anyString());
    }

    @Test
    void testStartProcess_UserFound_NoValidRequest() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("user@example.com");

        User user = new User();
        user.setEmail(request.getEmail());

        when(userRepository.findByEmailIgnoreCase(request.getEmail())).thenReturn(user);

        try (MockedStatic<WebUtils> webUtils = mockStatic(WebUtils.class)) {
            webUtils.when(() -> WebUtils.renderTemplate(anyString(), any())).thenReturn("renderedTemplate");

            passwordResetService.startProcess(request);

            assertNotNull(user.getResetUID());
            assertNotNull(user.getResetStart());
            verify(userRepository).save(user);
            verify(mailService).sendMail(request.getEmail(), "Password Reset", "renderedTemplate");
        }
    }

    @Test
    void testStartProcess_UserFound_HasValidRequest() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("user@example.com");

        User user = new User();
        user.setEmail(request.getEmail());
        UUID existingUid = UUID.randomUUID();
        user.setResetUID(existingUid);
        user.setResetStart(OffsetDateTime.now().minusDays(1));

        when(userRepository.findByEmailIgnoreCase(request.getEmail())).thenReturn(user);

        try (MockedStatic<WebUtils> webUtils = mockStatic(WebUtils.class)) {
            webUtils.when(() -> WebUtils.renderTemplate(anyString(), any())).thenReturn("renderedTemplate");

            passwordResetService.startProcess(request);

            assertEquals(existingUid, user.getResetUID());
            assertTrue(user.getResetStart().isAfter(OffsetDateTime.now().minusMinutes(1)));
            verify(userRepository).save(user);
            verify(mailService).sendMail(request.getEmail(), "Password Reset", "renderedTemplate");
        }
    }

    @Test
    void testIsValidPasswordResetUid_Valid() {
        UUID uid = UUID.randomUUID();
        User user = new User();
        user.setResetUID(uid);
        user.setResetStart(OffsetDateTime.now().minusDays(1));

        when(userRepository.findByResetUID(uid)).thenReturn(user);

        assertTrue(passwordResetService.isValidPasswordResetUid(uid));
    }

    @Test
    void testIsValidPasswordResetUid_Expired() {
        UUID uid = UUID.randomUUID();
        User user = new User();
        user.setResetUID(uid);
        user.setResetStart(OffsetDateTime.now().minusWeeks(2));

        when(userRepository.findByResetUID(uid)).thenReturn(user);

        assertFalse(passwordResetService.isValidPasswordResetUid(uid));
    }

    @Test
    void testIsValidPasswordResetUid_NotFound() {
        UUID uid = UUID.randomUUID();
        when(userRepository.findByResetUID(uid)).thenReturn(null);

        assertFalse(passwordResetService.isValidPasswordResetUid(uid));
    }

    @Test
    void testCompleteProcess_Success() {
        UUID uid = UUID.randomUUID();
        PasswordResetCompleteRequest request = new PasswordResetCompleteRequest();
        request.setUid(uid);
        request.setNewPassword("newSecret");

        User user = new User();
        user.setUsername("testuser");
        user.setResetUID(uid);
        user.setResetStart(OffsetDateTime.now());

        when(userRepository.findByResetUID(uid)).thenReturn(user);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedPassword");

        passwordResetService.completeProcess(request);

        assertEquals("encodedPassword", user.getPassword());
        assertNull(user.getResetUID());
        assertNull(user.getResetStart());
        verify(userRepository).save(user);
    }

    @Test
    void testCompleteProcess_InvalidRequest() {
        UUID uid = UUID.randomUUID();
        PasswordResetCompleteRequest request = new PasswordResetCompleteRequest();
        request.setUid(uid);

        when(userRepository.findByResetUID(uid)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> passwordResetService.completeProcess(request));
    }
}
