package pl.crystalek.budgetweb.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.util.UriComponentsBuilder;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailContent {
    String subject;
    String from;
    String to;
    String message;

    public static EmailContent ofBasicEmail(final EmailProperties emailProperties, final String emailAddress, final String token) {
        final String url = UriComponentsBuilder.fromUriString(emailProperties.getReturnAddress())
                .queryParam("token", token)
                .build().toString();

        final String message = String.format(emailProperties.getMessage(), url);

        return builder()
                .from(emailProperties.getFrom())
                .to(emailAddress)
                .subject(emailProperties.getMessageSubject())
                .message(message)
                .build();
    }

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
