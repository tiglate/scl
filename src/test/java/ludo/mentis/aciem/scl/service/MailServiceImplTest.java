package ludo.mentis.aciem.scl.service;

import jakarta.mail.Address;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import ludo.mentis.aciem.scl.config.MailProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MailProperties mailProperties;

    @InjectMocks
    private MailServiceImpl mailService;

    @Test
    void testSendMail() throws Exception {
        final String mailTo = "test@example.com";
        final String subject = "Test Subject";
        final String html = "<h1>Test Content</h1>";
        final String mailFrom = "from@example.com";
        final String displayName = "SCL Admin";

        when(mailProperties.getMailFrom()).thenReturn(mailFrom);
        when(mailProperties.getMailDisplayName()).thenReturn(displayName);

        mailService.sendMail(mailTo, subject, html);

        final ArgumentCaptor<MimeMessagePreparator> preparatorCaptor = ArgumentCaptor.forClass(MimeMessagePreparator.class);
        verify(javaMailSender).send(preparatorCaptor.capture());

        MimeMessage mimeMessage = mock(MimeMessage.class);
        
        preparatorCaptor.getValue().prepare(mimeMessage);

        verify(mailProperties).getMailFrom();
        verify(mailProperties).getMailDisplayName();

        verify(mimeMessage).setSubject(subject, "UTF-8");
        verify(mimeMessage).setFrom(argThat((Address address) -> {
            InternetAddress internetAddress = (InternetAddress) address;
            return internetAddress.getAddress().equals(mailFrom) && internetAddress.getPersonal().equals(displayName);
        }));
        verify(mimeMessage).setRecipient(eq(MimeMessage.RecipientType.TO), argThat((Address address) -> {
            InternetAddress internetAddress = (InternetAddress) address;
            return internetAddress.getAddress().equals(mailTo);
        }));
    }
}
