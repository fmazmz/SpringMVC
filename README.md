# Fleetify

A car rental management application built with Spring Boot and Thymeleaf. Users can sign up, register as drivers, browse available cars, and book them for specific time windows with configurable insurance options. Admins manage the full lifecycle of cars, drivers, and bookings.

## Tech Stack

- Spring Boot 4.0 (Web MVC, Security, Data JPA, Validation, Actuator)
- Thymeleaf with Spring Security integration
- PostgreSQL (runtime), H2 (tests)
- Lombok
- Docker Compose for local database provisioning

## Prerequisites

- Java 25+
- Maven 3.9+ (or use the included `mvnw` wrapper)
- Docker and Docker Compose

## Getting Started

Start the PostgreSQL database:

```
docker compose up -d
```

Run the application:

```
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. On first launch the database schema is created and seeded with sample car data and a test driver account (`testdriver@email.com` / `111`).

Run tests (uses an in-memory H2 database):

```
./mvnw test
```

## User Roles

| Role | Description |
|------|-------------|
| `ADMIN` | Full access. Manages cars, drivers, and all bookings. Created via SQL seed data. |
| `APP_USER` | Default role on signup. Can browse cars and register as a driver. |
| `DRIVER` | Upgraded from `APP_USER` after registration. Can book cars and manage own bookings. |

## Features

### Authentication

- Email/password login with BCrypt hashing and Spring Security
- Public signup at `/signup/new`
- CSRF protection enabled on all forms

### Cars

- **Browse** (`/cars/browse`) -- paginated listing with filtering by make, model, year, price range, licence plate, and VIN. Available to all authenticated users.
- **Create** (`/cars/new`) -- admin only. Validates make, model, hourly price, licence plate (6 chars), VIN (unique), and year.
- **View** (`/cars/{id}`) -- detail page for a single car.
- **Update / Delete** -- admin only.

### Drivers

- **Become a driver** (`/drivers/new`) -- available to `APP_USER` accounts. Requires first name, last name, and SSN. Promotes the user to `DRIVER` role.
- **List / Update / Delete** (`/drivers`) -- admin only.

### Bookings

- **Book a car** (`/bookings/new`) -- drivers select a time window, then choose from cars available during that period. Insurance tier (Basic, Premium, Full Coverage) is selected at booking time. Total price is calculated from the car's hourly rate plus insurance cost.
- **My Bookings** (`/bookings/my-bookings`) -- drivers view and cancel their own bookings.
- **Manage Bookings** (`/bookings`) -- admin-only paginated list with filtering. Admins can update or delete any booking.
- Booking time overlap validation prevents double-booking a car.

### Insurance

Flat-rate tiers added to the booking total:

| Tier | Price |
|------|-------|
| Basic | 79.00 |
| Premium | 129.00 |
| Full Coverage | 199.00 |

## Project Structure

```
src/main/java/org/example/springmvc/
  auth/           Security config, user details service, nonce filter
  bookings/       Booking entity, DTOs, service, controller
  cars/           Car entity, DTOs, service, controller
  drivers/        Driver entity, DTOs, service, controller
  users/          User entity, DTOs, service, signup controller
  insurances/     Insurance interface and car insurance implementation
  exceptions/     Custom exceptions and global handler
  utils/          Database seeder, search utilities, converters
```

## Configuration

Application settings are in `src/main/resources/application.yml`. Key defaults:

- **Port**: 8080
- **Database**: `jdbc:postgresql://localhost:5432/mvc_db` (user: `root`, password: `root`)
- **JPA DDL**: `create-drop` (schema recreated on each startup)
- **Seed data**: SQL files in `src/main/resources/data/` are loaded on startup

The test profile (`application-test.yml`) swaps PostgreSQL for an in-memory H2 database and disables SQL seed loading.
