package pl.crystalek.budgetweb.websocket;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Order
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor.getCommand() != StompCommand.SUBSCRIBE) {
            return message;
        }

        final String destination = (String) message.getHeaders().get("simpDestination");
        final Pattern pattern = Pattern.compile("/user/(\\d+)/");
        final Matcher matcher = pattern.matcher(destination);

        if (!matcher.find()) {
            throw new AccessDeniedException("Access Denied: You do not have permission to subscribe to this topic.");
        }

        final String userId = matcher.group(1);
        final String currentUserId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("Access Denied: You do not have permission to subscribe to this topic.");
        }

        return message;
    }
}