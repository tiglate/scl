package ludo.mentis.aciem.scl.service;


public interface MailService {

    void sendMail(String mailTo, String subject, String html);

}
