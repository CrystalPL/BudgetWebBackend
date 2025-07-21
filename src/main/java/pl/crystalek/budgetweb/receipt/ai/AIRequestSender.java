package pl.crystalek.budgetweb.receipt.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptPrompt;

import java.util.concurrent.CompletableFuture;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
class AIRequestSender {
    ChatModel chatModel;

    @Async
    public CompletableFuture<String> sendRequest(final AIReceiptPrompt prompt) {
        final Message[] prompts = prompt.getPrompts();
        final String result = chatModel.call(prompts);

        return CompletableFuture.completedFuture(result);
    }
}
