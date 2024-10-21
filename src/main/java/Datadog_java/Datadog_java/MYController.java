package Datadog_java.Datadog_java;

import datadog.trace.api.CorrelationIdentifier;
import datadog.trace.api.GlobalTracer;
import datadog.trace.api.Trace;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.apache.logging.log4j.ThreadContext;
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
        try {
            // Add Datadog tracing information to the log context
            ThreadContext.put("dd.trace_id", CorrelationIdentifier.getTraceId());
            ThreadContext.put("dd.span_id", CorrelationIdentifier.getSpanId());
            ThreadContext.put("dd.service", "Datadog-java-0.0.1-SNAPSHOT");
            ThreadContext.put("dd.env", "staging");
            ThreadContext.put("dd.version", "1.0.0");

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
        } finally {
            ThreadContext.clearAll();  // Clear the context to prevent contamination of subsequent logs
        }
    } }