package ludo.mentis.aciem.scl.service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.PasswordResetCompleteRequest;
import ludo.mentis.aciem.scl.model.PasswordResetRequest;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public PasswordResetService(final MailService mailService,
            final PasswordEncoder passwordEncoder, final UserRepository userRepository) {
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    private boolean hasValidRequest(final User user) {
        return user != null && user.getResetUID() != null && 
                user.getResetStart().plusWeeks(1).isAfter(OffsetDateTime.now());
    }

    public void startProcess(final PasswordResetRequest passwordResetRequest) {
        log.info("received password reset request for {}", passwordResetRequest.getEmail());

        final User user = userRepository.findByEmailIgnoreCase(passwordResetRequest.getEmail());
        if (user == null) {
            log.warn("user {} not found", passwordResetRequest.getEmail());
            return;
        }

        // keep existing uid if still valid
        if (!hasValidRequest(user)) {
            user.setResetUID(UUID.randomUUID());
        }
        user.setResetStart(OffsetDateTime.now());
        userRepository.save(user);

        mailService.sendMail(passwordResetRequest.getEmail(), WebUtils.getMessage("passwordReset.mail.subject"),
                WebUtils.renderTemplate("/mails/passwordReset", Map.of("passwordResetUid", user.getResetUID())));
    }

    public boolean isValidPasswordResetUid(final UUID passwordResetUid) {
        final User user = userRepository.findByResetUID(passwordResetUid);
        if (hasValidRequest(user)) {
            return true;
        }
        log.warn("invalid password reset uid {}", passwordResetUid);
        return false;
    }

    public void completeProcess(final PasswordResetCompleteRequest passwordResetCompleteRequest) {
        final User user = userRepository.findByResetUID(passwordResetCompleteRequest.getUid());
        Assert.isTrue(hasValidRequest(user), "invalid update password request");

        log.warn("updating password for user {}", user.getUsername());

        user.setPassword(passwordEncoder.encode(passwordResetCompleteRequest.getNewPassword()));
        user.setResetUID(null);
        user.setResetStart(null);
        userRepository.save(user);
    }

}
