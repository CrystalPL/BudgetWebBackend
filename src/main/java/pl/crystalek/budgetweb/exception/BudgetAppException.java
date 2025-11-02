package pl.crystalek.budgetweb.exception;

public class BudgetAppException extends RuntimeException {

    public BudgetAppException(final String message) {
        super(message);
    }

    public BudgetAppException(final Enum<?> messageKey) {
        this(messageKey.name());
    }
}
