package pl.crystalek.budgetweb.receipt.ai.model;

import org.springframework.ai.chat.messages.Message;

public record AIReceiptPrompt(Message systemMessagePrompt, Message userMessageCategories) {
    public Message[] getPrompts() {
        return new Message[]{systemMessagePrompt, userMessageCategories};
    }
}
