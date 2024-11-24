package pl.crystalek.budgetweb.websocket;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WebSocketController {
    SimpMessagingTemplate messagingTemplate;

    @SneakyThrows
    @SendTo("/topic/notifications")
    @MessageMapping("/sendNotification")
    public String sendNotification(String message) {
        return message;
    }

    public void sendToSpecificUser(String user, String message) {
        messagingTemplate.convertAndSendToUser(user, "/topic/notifications", message);
    }

    @Scheduled(fixedRate = 2000) // co 2 sekundy
    public void sendPeriodicNotification() {
        String message = "To jest wiadomość wysyłana co 2 sekundy!";
        sendToSpecificUser("1", message);
    }
}
