package Datadog_java.Datadog_java;

import datadog.trace.api.Trace;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/api")
public class MYController {

    private static final Logger logger = LoggerFactory.getLogger(MYController.class);

    // Set the Datadog service name programmatically
//    @PostConstruct
    public void init() {
        System.setProperty("dd.service", "Datadog-java-0.0.1-SNAPSHOT");
        logger.info("Service name set to: {}", System.getProperty("dd.service"));
    }

    // Custom exception for invalid input
    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }

    @Trace(operationName = "hello.request")
    @GetMapping("/hello")
    public String hello(@RequestParam(required = false) String name) {
        logger.info("Received request for /hello endpoint with name: {}", name);

        // Simulate an internal server error
        if ("error".equalsIgnoreCase(name)) {
            logger.error("Simulating internal server error for testing");
            throw new RuntimeException("Simulated Internal Server Error");
        }

        // Throw an exception if invalid name is provided and create a custom span for error handling
        if (name == null || !"hello".equalsIgnoreCase(name)) {
            String errorMessage = "Invalid input: expected 'hello', but received: " + name;
            logger.error("Error: {}", errorMessage);

            // Start a new trace/span for this error
            Span span = GlobalTracer.get().activeSpan();
            if (span != null) {
                span.setTag("error", true); // Mark the span as an error
                span.setTag("error.fingerprint", "invalid-input"); // Custom error fingerprint
                span.setTag("error.message", "Invalid input received: " + name);
                logger.info("Custom error grouping applied with error.fingerprint");
            }

            // Throw a custom exception
            throw new InvalidInputException(errorMessage);
        }

        logger.info("Processing the request...");
        return "Hello, Datadog!";
    }

    // Exception handler for InvalidInputException
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInputException(InvalidInputException ex) {
        logger.error("An error occurred: {}", ex.getMessage(), ex);

        // Capture the active span and add the error information
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("error", true);
            span.setTag("error.fingerprint", "invalid-input-exception-handler"); // Different fingerprint for this handler
            span.setTag("error.message", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid input: " + ex.getMessage());
    }

    // Generic exception handler for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        logger.error("An unexpected error occurred", ex);

        // Capture the active span and add the error information
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("error", true);
            span.setTag("error.fingerprint", "general-exception-handler"); // Different fingerprint for general errors
            span.setTag("error.message", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal Server Error: Please contact support.");
    }
}
