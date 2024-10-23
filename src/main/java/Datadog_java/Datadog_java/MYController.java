package Datadog_java.Datadog_java;

import datadog.trace.api.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // Exception for invalid input
    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }

    @Trace(operationName = "hello.request")
    @GetMapping("/hello")
    public String hello(@RequestParam(required = false) String name) {
        logger.info("Received request for /hello endpoint with name: {}", name);
        logger.info("Check the router if it is working or not");

        // Throw exception if invalid name is provided
        if (name == null || !"hello".equalsIgnoreCase(name)) {
            String errorMessage = "Invalid input: expected 'hello', but received: " + name;
            logger.error("Error: {}", errorMessage);

            // Throw custom exception for error tracking
            throw new InvalidInputException("Invalid input received: " + name);
        }

        logger.info("Processing the request...");
        return "Hello, Datadog!";
    }

    // Exception handler for invalid inputs
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInputException(InvalidInputException ex) {
        logger.error("An error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid input: " + ex.getMessage());
    }

    // Generic Exception handler for other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        logger.error("An unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal Server Error: Please contact support.");
    }
}
