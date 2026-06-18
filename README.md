# TicketBookingSystem

A bus ticket booking backend built with Spring Boot. Supports registered users and guest checkouts, seat locking with pessimistic locking, expiring booking holds via `@Scheduled`, and async email notifications.

---

## Tech Stack

| Layer     | Technology                  |
|-----------|-----------------------------|
| Language  | Java 17                     |
| Framework | Spring Boot 4.1.0           |
| Database  | PostgreSQL 14.23            |
| ORM       | Spring Data JPA / Hibernate |
| Auth      | HTTP Basic Auth             |
| Build     | Gradle 9.5.1                |

---

## Prerequisites

- JDK 17
- PostgreSQL 14.23 running locally (or via Docker)
- Gradle 9.5.1

---

## Getting Started

```bash
git clone https://github.com/MyCompilerHatesMe/TicketBookingSystem.git
cd TicketBookingSystem
./gradlew bootRun --args='--spring.datasource.url=jdbc:postgresql://localhost:5432/ticket_booking --spring.datasource.username=your_user --spring.datasource.password=your_password'
```

> Credentials are not read from a `.env` file. Pass them as command-line arguments as shown above, or set them in your shell before running.

---

## Configuration

```yaml
# src/main/resources/application.yaml
spring:
  application:
    name: "TicketBookingSystem"
  datasource:
    url: ${DB_URL}
    password: ${DB_PASSWORD}
    username: ${DB_USERNAME}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

Schema is Hibernate-managed. No manual DDL required.

---

## Database Schema

Entities are grouped into four logical layers. All string fields are mapped as `String` in Java. Hibernate handles the column types. The two user tables coexist: `UserMaster` for registered accounts, `UserGuest` for anonymous bookings.

### Master Data

**Bus**
- `bus_id` - `Long` PK
- `bus_number` - `String`
- `bus_name` - `String`
- `bus_type` - `String`
- `total_seats` - `int`

**Seat**
- `seat_id` - `Long` PK
- `bus_id` - FK -> Bus
- `seat_number` - `String`

**Route**
- `route_id` - `Long` PK
- `source_city` - `String`
- `dest_city` - `String`
- `distance_km` - `int`

### Operational

**TripSchedule**
- `trip_id` - `Long` PK
- `bus_id` - FK -> Bus
- `route_id` - FK -> Route
- `departure_time` - `LocalDateTime`
- `arrival_time` - `LocalDateTime`
- `fare` - `BigDecimal`

### Users

**UserMaster**
- `user_id` - `Long` PK
- `email` - `String` UNIQUE
- `name` - `String`
- `mobile` - `String`
- `password` - `String`
- `created_at` - `LocalDateTime`

**UserGuest**
- `guest_id` - `Long` PK
- `email` - `String` UNIQUE
- `mobile` - `String` UNIQUE

### Transactions

**Booking**
- `booking_id` - `Long` PK
- `booking_ref` - `UUID`
- `user_id` - FK -> UserMaster, **nullable**
- `guest_id` - FK -> UserGuest, **nullable**
- `trip_id` - FK -> TripSchedule
- `booking_date` - `LocalDateTime`
- `total_amount` - `BigDecimal`
- `booking_status` - `Enum` (PENDING, CONFIRMED, CANCELLED, EXPIRED)
- `expires_at` - `LocalDateTime` (booking_date + 10 min)

> XOR constraint enforced at DB level:
> ```sql
> CHECK (
>   (user_id IS NULL AND guest_id IS NOT NULL) OR
>   (user_id IS NOT NULL AND guest_id IS NULL)
> )
> ```

**BookingSeat**
- `booking_seat_id` - `Long` PK
- `booking_id` - FK -> Booking
- `trip_id` - FK -> TripSchedule
- `seat_id` - FK -> Seat
- UNIQUE `(trip_id, seat_id)` - prevents double-booking

**Payment**
- `payment_id` - `Long` PK
- `booking_id` - FK -> Booking
- `amount` - `BigDecimal`
- `payment_mode` - `Enum` (UPI, CARD, NET_BANKING, WALLET)
- `payment_status` - `Enum` (PENDING, SUCCESS, FAILED)
- `transaction_id` - `String`

---

## Planned API

> TODO — controllers and services not yet implemented.

### Trips
```
GET /api/v1/trips?source=&dest=&date=
GET /api/v1/trips/{id}/seats
```

### Bookings
```
POST /api/v1/bookings
```
- If email exists in `UserMaster` -> link booking to that user
- Otherwise -> create `UserGuest` entry, link to that
- Seats held immediately via `PESSIMISTIC_WRITE` lock
- `BookingStatus` set to `PENDING`, expires in 10 minutes
- Expired bookings purged by `@Scheduled` job

### Payments
```
POST /api/v1/payments
```
Confirms a `PENDING` booking → `CONFIRMED`.

### Admin
```
POST /api/v1/admin/buses
POST /api/v1/admin/routes
POST /api/v1/admin/trips
```

---

## TODO

- [ ] Repositories
- [ ] Services (booking flow, seat locking, payment, expiry scheduler)
- [ ] Controllers
- [ ] DTOs and request/response models
- [ ] `@Scheduled` purge job for expired bookings
- [ ] `@Async` email dispatch post-booking
- [ ] Security config and role enforcement (TODO auth layer TBD)
- [ ] Exception handling / error responses
- [ ] Tests

---

## Notes for Future Developers

The schema is intentionally Hibernate-managed (`ddl-auto: update`) to keep iteration fast during early development review this before any production deployment.

Auth is HTTP Basic for now. The integration team will own the authentication layer once active development begins.