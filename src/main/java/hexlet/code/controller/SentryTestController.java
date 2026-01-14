package hexlet.code.controller;

import hexlet.code.exception.SentryTestException;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SentryTestController {

    private void safeCapture(Throwable ex) {
        try {
            if (Sentry.isEnabled()) {
                Sentry.captureException(ex);
            } else {
                log.debug("Sentry is disabled; not capturing exception: {}", ex.getMessage());
            }
        } catch (Throwable t) {
            log.warn("Failed to capture exception to Sentry: {}", t.getMessage(), t);
        }
    }

    @GetMapping("/sentry-test")
    public String triggerError() {
        try {
            throw new SentryTestException("This is a test exception for Sentry.");
        } catch (SentryTestException e) {
            safeCapture(e);
            return "Exception caught and sent to Sentry! Check your Sentry dashboard.";
        }
    }
}
