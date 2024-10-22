package Datadog_java.Datadog_java;

import datadog.trace.api.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MYController {
    private static final Logger logger = LoggerFactory.getLogger(MYController.class);

    // Static block to set Datadog properties
    static {
        // Set Datadog service, environment, and version properties
        System.setProperty("dd.service", "Datadog-java-0.0.1-SNAPSHOT");
        System.setProperty("dd.env", "staging");
        System.setProperty("dd.version", "1.0.0");

        // Optionally, set service mapping if needed
        System.setProperty("dd.service.mapping", "h2:Datadog-java-h2");
    }

    @Trace(operationName = "hello.request")
    @GetMapping("/hello")
    public String hello(@RequestParam(required = false) String name) {
        // Log the current Datadog settings
        logger.info("Service: {}, Environment: {}, Version: {}",
                System.getProperty("dd.service"),
                System.getProperty("dd.env"),
                System.getProperty("dd.version"));

        logger.info("Received request for /hello endpoint with name: {}", name);
        logger.info("Check the router if it is working or not");

        if (name == null || !"hello".equalsIgnoreCase(name)) {
            String errorMessage = "Invalid input: expected 'hello', but received: " + name;
            logger.error("Error: {}", errorMessage);
            try {
                throw new IllegalArgumentException("Invalid input received: " + name);
            } catch (Exception e) {
                logger.error("An exception occurred due to invalid input", e); // Log the exception with the error
            }
            return errorMessage;
        }

        logger.info("Processing the request...");
        return "Hello, Datadog!,,,8125";
    }
}
