package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.config.MailProperties;
import ludo.mentis.aciem.scl.util.LogSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    public MailServiceImpl(final JavaMailSender javaMailSender,
            final MailProperties mailProperties) {
        this.javaMailSender = javaMailSender;
        this.mailProperties = mailProperties;
    }

    @Override
    @Async
    public void sendMail(final String mailTo, final String subject, final String html) {
        log.info("sending mail {} to {}", LogSafe.of(subject), LogSafe.of(mailTo));

        javaMailSender.send(mimeMessage -> {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(mailProperties.getMailFrom(), mailProperties.getMailDisplayName());
            message.setTo(mailTo);
            message.setSubject(subject);
            message.setText(html, true);
        });

        log.info("sending completed");
    }

}
