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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.env.Environment;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler createGlobalExceptionHandler() {
        Environment environment = mock(Environment.class);
        when(environment.getActiveProfiles()).thenReturn(new String[]{});
        return new GlobalExceptionHandler(environment);
    }

    @Test
    void testHandleValidationExceptions() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        FieldError fieldError = new FieldError("objectName", "email", "must be a well-formed email address");
        bindingResult.addError(fieldError);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("must be a well-formed email address", response.getBody().get("email"));
    }

    @Test
    void testHandleBadCredentialsException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<String> response = globalExceptionHandler.handleBadCredentialsException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bad credentials", response.getBody());
    }

    @Test
    void testHandleUserNotFoundException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        UserNotFoundException ex = new UserNotFoundException(1L);

        ResponseEntity<String> response = globalExceptionHandler.handleUserNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User with id 1 not found", response.getBody());
    }

    @Test
    void testHandleTaskNotFoundException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        TaskNotFoundException ex = new TaskNotFoundException(1L);

        ResponseEntity<String> response = globalExceptionHandler.handleTaskNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task with id 1 not found", response.getBody());
    }

    @Test
    void testHandleTaskStatusNotFoundException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        TaskStatusNotFoundException ex = new TaskStatusNotFoundException(1L);

        ResponseEntity<String> response = globalExceptionHandler.handleTaskStatusNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("TaskStatus with id 1 not found", response.getBody());
    }

    @Test
    void testHandleLabelNotFoundException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        LabelNotFoundException ex = new LabelNotFoundException(1L);

        ResponseEntity<String> response = globalExceptionHandler.handleLabelNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Label with id 1 not found", response.getBody());
    }

    @Test
    void testHandleUserDeletionException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        UserDeletionException ex = new UserDeletionException(1L);

        ResponseEntity<String> response = globalExceptionHandler.handleUserDeletionException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Cannot delete user with id 1 because they have assigned tasks", response.getBody());
    }

    @Test
    void testHandleTaskStatusDeletionException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        TaskStatusDeletionException ex = new TaskStatusDeletionException(1L);

        ResponseEntity<String> response = globalExceptionHandler.handleTaskStatusDeletionException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Cannot delete task status with id 1 because it has associated tasks", response.getBody());
    }

    @Test
    void testHandleRsaKeyLoadingException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        RsaKeyLoadingException ex = new RsaKeyLoadingException("Error loading key", new RuntimeException());

        ResponseEntity<String> response = globalExceptionHandler.handleRsaKeyLoadingException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error loading key", response.getBody());
    }

    @Test
    void testHandleSentryTestException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        SentryTestException ex = new SentryTestException("Test exception for Sentry");

        ResponseEntity<String> response = globalExceptionHandler.handleSentryTestException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Test exception for Sentry", response.getBody());
    }

    @Test
    void testHandleGeneralException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        Exception ex = new Exception("General error");

        ResponseEntity<String> response = globalExceptionHandler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("General error", response.getBody());
    }

    @Test
    void testHandleLabelDeletionException() {
        GlobalExceptionHandler globalExceptionHandler = createGlobalExceptionHandler();
        LabelDeletionException ex = new LabelDeletionException(42L);
        ResponseEntity<String> response = globalExceptionHandler.handleLabelDeletionException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Cannot delete label with id 42 because it has associated tasks", response.getBody());
    }
}
