package pl.crystalek.budgetweb.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmailSender {
    JavaMailSender javaMailSender;

    @Async
    public void send(final EmailContent emailContent) {
        try {
            final MimeMessage mimeMessage = emailContent.getMimeMessage(javaMailSender);
            javaMailSender.send(mimeMessage);
        } catch (final MessagingException ignore) {

        }
    }
}
