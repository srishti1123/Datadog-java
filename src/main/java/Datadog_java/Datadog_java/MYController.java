package Datadog_java.Datadog_java;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.SocketTimeoutException;
import java.sql.SQLException;

// Spring Boot annotations for creating REST controller
@RestController
@RequestMapping("/api")
public class MYController {

    // Example init method - not being used, remove if unnecessary
    @GetMapping("/init")
    public String init() {
        return "Initialization successful";
    }

    // Example hello method - responding to a GET request
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        // Example logic with null check
        if (name == null || name.equalsIgnoreCase("")) {
            return "Hello, Anonymous!";
        }
        return "Hello, " + name + "!";
    }

    // Example method that may throw SQLException and SocketTimeoutException
    @GetMapping("/data")
    public ResponseEntity<String> fetchData() {
        try {
            // Simulate data fetching that may cause exceptions
            simulateDatabaseOperation();
            simulateNetworkCall();

            return ResponseEntity.ok("Data fetched successfully");

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());

        } catch (SocketTimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Network timeout: " + e.getMessage());
        }
    }

    // Method to simulate a network call that can throw SocketTimeoutException
    private void simulateNetworkCall() throws SocketTimeoutException {
        // Simulate a condition where a timeout happens
        throw new SocketTimeoutException("Timeout while calling the external service");
    }

    // Method to simulate a database operation that can throw SQLException
    private void simulateDatabaseOperation() throws SQLException {
        // Simulate a database error
        throw new SQLException("Failed to fetch data from the database");
    }

    // Exception handler for invalid input
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInputException(InvalidInputException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Exception handler for general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Custom exception class for invalid inputs
    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
