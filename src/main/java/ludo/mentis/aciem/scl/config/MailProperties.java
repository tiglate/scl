package ludo.mentis.aciem.scl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MailProperties {

    @Value("${app.mail.from}")
    private String mailFrom;

    @Value("${app.mail.displayName}")
    private String mailDisplayName;

    public String getMailFrom() {
        return mailFrom;
    }

    public String getMailDisplayName() {
        return mailDisplayName;
    }

}
