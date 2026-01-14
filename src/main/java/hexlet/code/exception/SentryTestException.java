package hexlet.code.exception;

public class SentryTestException extends RuntimeException {
    public SentryTestException(String message) {
        super(message);
    }

    public SentryTestException(String message, Throwable cause) {
        super(message, cause);
    }
}
