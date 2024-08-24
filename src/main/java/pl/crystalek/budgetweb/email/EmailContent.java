package pl.crystalek.budgetweb.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailContent {
    String subject;
    String from;
    String to;
    String message;

    public MimeMessage getMimeMessage(final JavaMailSender javaMailSender) throws MessagingException {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setSubject(subject);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(message, true);

        return mimeMessage;
    }
}
