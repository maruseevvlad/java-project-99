package hexlet.code.exception;

public class RsaKeyLoadingException extends RuntimeException {
    public RsaKeyLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
