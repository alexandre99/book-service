.PHONY: run tests

# Run tests with Maven (using the wrapper, if available)
tests:
	./mvnw test

# Run the Spring Boot application
run:
	./mvnw spring-boot:run