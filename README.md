# Booking Service

This project is a booking service application that allows managing properties, bookings, and property blocks. The application is built with Spring Boot and uses JPA/Hibernate along with Maven. It also includes a Makefile for running tests and starting the project.

## Features

### Property Management
- **Create a Property:** Register a new property.
- **Update a Property:** Modify existing property details.
- **Find a Property by ID:** Retrieve a property by its unique identifier.
- **List All Properties:** Get a list of all properties.

### Booking Management
- **Create a Booking:** Submit a reservation for a property.
- **Update Booking Dates and Guest Details:** Modify the booking dates (start and end) and guest information.
- **Cancel a Booking:** Mark a booking as canceled.
- **Rebook a Canceled Booking:** Reschedule a booking that was canceled.
- **Delete a Booking:** Remove a booking (soft delete).
- **Get a Booking:** Retrieve booking details by ID.

### Block Property Management
- **Create a Block:** Block a property for a specific range of days (for example, when the owner uses the property or for maintenance).
- **Update a Block:** Modify the details of an existing block.
- **Delete a Block:** Remove a block from the system.

## Running the Application

### Prerequisites
- **Java 17 or higher**
- **Maven**
- **Make**  
  **Note:** You need to have `make` installed on your system.

### Using the Makefile

A Makefile is provided to simplify common tasks:

- **Run Tests:**  
  Execute the following command to run the project's tests:
  ```bash
  make tests

- **Run Application:**
  Execute the following command to run the project:
  ```bash
  make run
