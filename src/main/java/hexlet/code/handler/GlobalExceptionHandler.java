package hexlet.code.handler;

import hexlet.code.exception.LabelNotFoundException;
import hexlet.code.exception.RsaKeyLoadingException;
import hexlet.code.exception.SentryTestException;
import hexlet.code.exception.TaskNotFoundException;
import hexlet.code.exception.TaskStatusDeletionException;
import hexlet.code.exception.TaskStatusNotFoundException;
import hexlet.code.exception.UserDeletionException;
import hexlet.code.exception.UserNotFoundException;
import hexlet.code.exception.LabelDeletionException;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Environment environment;

    public GlobalExceptionHandler(Environment environment) {
        this.environment = environment;
    }

    private boolean isTestProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test");
    }

    private void safeCapture(Throwable ex) {
        if (isTestProfile()) {
            return;
        }

        try {
            Sentry.captureException(ex);
        } catch (Throwable t) {
            log.warn("Failed to capture exception to Sentry: {}", t.getMessage(), t);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        safeCapture(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
        safeCapture(e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        safeCapture(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException e) {
        safeCapture(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(TaskStatusNotFoundException.class)
    public ResponseEntity<String> handleTaskStatusNotFoundException(TaskStatusNotFoundException e) {
        safeCapture(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(LabelNotFoundException.class)
    public ResponseEntity<String> handleLabelNotFoundException(LabelNotFoundException e) {
        safeCapture(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(UserDeletionException.class)
    public ResponseEntity<String> handleUserDeletionException(UserDeletionException e) {
        safeCapture(e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(TaskStatusDeletionException.class)
    public ResponseEntity<String> handleTaskStatusDeletionException(TaskStatusDeletionException e) {
        safeCapture(e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(RsaKeyLoadingException.class)
    public ResponseEntity<String> handleRsaKeyLoadingException(RsaKeyLoadingException e) {
        safeCapture(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(SentryTestException.class)
    public ResponseEntity<String> handleSentryTestException(SentryTestException e) {
        safeCapture(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        safeCapture(e);
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(LabelDeletionException.class)
    public ResponseEntity<String> handleLabelDeletionException(LabelDeletionException e) {
        safeCapture(e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
