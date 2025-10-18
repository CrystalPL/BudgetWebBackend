package pl.crystalek.budgetweb.receipt.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.share.ResponseAPI;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SaveReceiptResponse extends ResponseAPI<SaveReceiptResponseMessage> {
    String additionalMessage;

    public SaveReceiptResponse(final boolean success, final SaveReceiptResponseMessage message, final String additionalMessage) {
        super(success, message);

        this.additionalMessage = additionalMessage;
    }
}
