package Datadog_java.Datadog_java;

import datadog.trace.api.Trace;
import datadog.trace.api.CorrelationIdentifier;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MYController {

    private static final Logger logger = LoggerFactory.getLogger(MYController.class);

    public void init() {
        System.setProperty("dd.service", "datadog-java-0.0.1-snapshot");
        logger.info("Service name set to: {}", System.getProperty("dd.service"));
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }

    @Trace(operationName = "hello.request")
    @GetMapping("/hello")
    public String hello(@RequestParam(required = false) String name) {
        // Use MDC to store trace and span IDs
        try {
            MDC.put("dd.trace_id", CorrelationIdentifier.getTraceId());
            MDC.put("dd.span_id", CorrelationIdentifier.getSpanId());

            logger.info("Received request for /hello endpoint with name: {}", name);

            if ("error".equalsIgnoreCase(name)) {
                logger.error("Simulating internal server error for testing");
                throw new RuntimeException("Simulated Internal Server Error");
            }

            if (name == null || !"hello".equalsIgnoreCase(name)) {
                String errorMessage = "Invalid input: expected 'hello', but received: " + name;
                logger.error("Error: {}", errorMessage);

                Span span = GlobalTracer.get().activeSpan();
                if (span != null) {
                    span.setTag("error", true);
                    span.setTag("error.fingerprint", "invalid-input");
                    span.setTag("error.message", "Invalid input received: " + name);
                    logger.info("Custom error grouping applied with error.fingerprint");
                }

                throw new InvalidInputException(errorMessage);
            }

            logger.info("Processing the request...");
            return "Hello, Datadog! 8080";
        } finally {
            // Clean up MDC context
            MDC.remove("dd.trace_id");
            MDC.remove("dd.span_id");
        }
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInputException(InvalidInputException ex) {
        try {
            logger.error("An error occurred: {}", ex.getMessage(), ex);
            Span span = GlobalTracer.get().activeSpan();
            if (span != null) {
                span.setTag("error", true);
                span.setTag("error.fingerprint", "invalid-input-exception-handler");
                span.setTag("error.message", ex.getMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + ex.getMessage());
        } finally {
            MDC.clear(); // Clear MDC to avoid leaking context
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        try {
            logger.error("An unexpected error occurred", ex);
            Span span = GlobalTracer.get().activeSpan();
            if (span != null) {
                span.setTag("error", true);
                span.setTag("error.fingerprint", "general-exception-handler");
                span.setTag("error.message", ex.getMessage());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error: Please contact support.");
        } finally {
            MDC.clear(); // Clear MDC to avoid leaking context
        }
    }
}
