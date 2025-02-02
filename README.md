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

# cURL Commands

## Create property

```bash
curl -i -X POST http://localhost:8080/property \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Studio RM - Seafront!",
           "amenities":["Elevator", "Netflix"],
           "address": "New York, EUA",
           "hostName": "John Doe",
           "checkInTime": "14:00",
           "checkOutTime": "14:00",
           "dailyRate": 200.00
         }'
```

## Update property

```bash
curl -i -X PUT http://localhost:8080/property/:property-id \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Studio RM - Seafront",
           "amenities":["Elevator", "Netflix", "Air conditional"],
           "address": "New York, EUA",
           "hostName": "John Doe",
           "checkInTime": "14:00",
           "checkOutTime": "14:00",
           "dailyRate": 200.00
         }'
```

## Create property block

```bash
curl -i -X POST http://localhost:8080/block-property \
     -H "Content-Type: application/json" \
     -d '{
           "propertyId": :propertyId,
           "startDate": "2025-06-29",
           "endDate": "2025-06-30"
         }'
```

## Update property block

```bash
curl -i -X PUT http://localhost:8080/block-property/:block-property-id \
     -H "Content-Type: application/json" \
     -d '{
           "propertyId": :propertyId,
           "startDate": "2025-06-15",
           "endDate": "2025-06-16"
         }'
```

## Delete property block

```bash
curl -i -X DELETE http://localhost:8080/block-property/:block-property-id
```

## Create booking

```bash
curl -i -X POST http://localhost:8080/booking \
     -H "Content-Type: application/json" \
     -d '{
           "propertyId": :propertyId,
           "startDate": "2025-06-27",
           "endDate": "2025-06-28",
           "guestDetails": {
             "fullName": "John Doe",
             "email": "johndoe@example.com",
             "phone": "+1 123-456-7890",
             "numberOfAdults": 2,
             "numberOfChildren": 1,
             "numberOfInfants": 0,
             "specialRequests": "Late check-in, if possible."
           }
         }'
```

## Update booking dates

```bash
curl -i -X PATCH http://localhost:8080/booking/:booking-id/update-booking-dates \
     -H "Content-Type: application/json" \
     -d '{
           "startDate": "2025-06-20",
           "endDate": "2025-06-21"
         }'
```

## Update guest details

```bash
curl -i -X PATCH http://localhost:8080/booking/:booking-id/update-guest-details \
     -H "Content-Type: application/json" \
     -d '{
           "fullName": "John Doe 2",
           "email": "johndoe2@example.com",
           "phone": "+1 122-456-7890",
           "numberOfAdults": 1,
           "numberOfChildren": 0,
           "numberOfInfants": 1,
           "specialRequests": "2 Late check-in, if possible."
         }'
```

## Cancel booking

```bash
curl -X PATCH http://localhost:8080/booking/:booking-id/cancel
```

## Rebook booking

```bash
curl -X PATCH http://localhost:8080/booking/:booking-id/rebook
```

## Delete booking

```bash
curl -X DELETE http://localhost:8080/booking/:booking-id
```
