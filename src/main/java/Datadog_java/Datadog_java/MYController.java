package Datadog_java.Datadog_java;

import datadog.trace.api.CorrelationIdentifier;
import datadog.trace.api.Trace;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MYController {
    private static final Logger logger = LoggerFactory.getLogger(MYController.class);

    @Trace(operationName = "hello.request")
    @GetMapping("/hello")
    public ResponseEntity<String> hello(@RequestParam(required = false) String name) {
        // Add Datadog tracing information to the log context
        ThreadContext.put("dd.trace_id", CorrelationIdentifier.getTraceId());
        ThreadContext.put("dd.span_id", CorrelationIdentifier.getSpanId());
        ThreadContext.put("dd.service", "Datadog-java-0.0.1-SNAPSHOT");
        ThreadContext.put("dd.env", "staging");
        ThreadContext.put("dd.version", "1.0.0");

        try {
            logger.info("Received request for /hello endpoint with name: {}", name);
            logger.info("Check the router if it is working or not");

            if (name == null || !"hello".equalsIgnoreCase(name)) {
                String errorMessage = "Invalid input: expected 'hello', but received: " + name;
                logger.error("Error: {}", errorMessage);
                throw new IllegalArgumentException("Invalid input received: " + name);
            }

            logger.info("Processing the request...");
            return new ResponseEntity<>("Hello, Datadog!,,,8125", HttpStatus.OK);

        } catch (Exception e) {
            logger.error("An exception occurred due to invalid input", e); // Log the exception with the error
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            ThreadContext.clearAll();  // Always clear the logging context
        }
    }
}
