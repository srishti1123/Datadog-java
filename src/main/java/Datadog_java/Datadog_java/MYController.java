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

import java.net.SocketTimeoutException;
import java.sql.SQLException;

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

            MDC.put("dd.trace_id", CorrelationIdentifier.getTraceId());
            MDC.put("dd.span_id", CorrelationIdentifier.getSpanId());

            logger.info("Received request for /hello endpoint with name: {}", name);

            // Simulate different errors
            if ("error".equalsIgnoreCase(name)) {
                logger.error("Simulating internal server error for testing");
                throw new RuntimeException("Simulated Internal Server Error");
            }
            if (name == null || !"hello".equalsIgnoreCase(name)) {
                String errorMessage = "Invalid input: expected 'hello', but received: " + name;
                logger.error("Error: {}", errorMessage);
                throw new InvalidInputException(errorMessage);
            }

//            logger.info("Processing the request...");
            return "Hello, Datadog! 8080";
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInputException(InvalidInputException ex) {
        logger.error("Invalid input error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid input: " + ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        logger.error("An unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal Server Error: Please contact support.");
    }
}
