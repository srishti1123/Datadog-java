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
    @Trace(operationName = "hello.request")
    @GetMapping("/hello")
    public String hello(@RequestParam(required = false) String name) {
        logger.info("Received request for /hello endpoint with name: {}", name);
        logger.info("check the router if it is working or not");

        if (name == null || !"hello".equalsIgnoreCase(name)) {
            String errorMessage = "Invalid input: expected 'hello', but received: " + name;
            logger.error("Error: {}", errorMessage);
            return errorMessage;
        }
        logger.info("Processing the request...");
        return "Hello, Datadog!";
    }
}
