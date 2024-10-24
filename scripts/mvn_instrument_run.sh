#!/bin/sh
set -e  # Exit immediately if a command exits with a non-zero status

# Set Datadog-related environment variables
export DD_SERVICE="datadog-java-0.0.1-snapshot"
export DD_VERSION="1.0.0"
export DD_ENV="staging"


# Java options
JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"

# Build the project
./mvnw clean package

# Check if the JAR file exists before running it
if [ -f target/Datadog-java-0.0.1-SNAPSHOT.jar ]; then
    echo "Using agent path: C:\\Users\\Srishti\\Downloads\\Datadog-java\\Datadog-java\\dd-java-agent.jar"
    echo "Using JAR path: target/Datadog-java-0.0.1-SNAPSHOT.jar"

    # Run the application with Datadog Java agent
    java $JAVA_OPTS -javaagent:C:\\Users\\Srishti\\Downloads\\Datadog-java\\Datadog-java\\dd-java-agent.jar \
        -Ddd.trace.sample.rate=1 \
        -Ddd.service=${DD_SERVICE} \
        -Ddd.env=${DD_ENV} \
        -Ddd.version=${DD_VERSION} \
        -Ddd.logs.injection=true \
        -jar target/Datadog-java-0.0.1-SNAPSHOT.jar
else
    echo "JAR file not found: target/Datadog-java-0.0.1-SNAPSHOT.jar"
    exit 1
fi
