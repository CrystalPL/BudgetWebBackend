package pl.crystalek.budgetweb.receipt.ai.model;

import lombok.Getter;
import pl.crystalek.budgetweb.share.ResponseAPI;

@Getter
public class AIReceiptResponse extends ResponseAPI<AIReceiptResponseMessage> {
    private AIReceipt receipt;

    public AIReceiptResponse(final boolean success, final AIReceiptResponseMessage message) {
        super(success, message);
    }

    public AIReceiptResponse(final boolean success, final AIReceiptResponseMessage message, final AIReceipt receipt) {
        super(success, message);

        this.receipt = receipt;
    }
}
