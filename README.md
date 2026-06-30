# Ticket Booking System (TBS)

A complete, modern bus ticket booking application featuring a Spring Boot Java backend, PostgreSQL database, and a React Vite frontend. Supports guest checkout, pessimistic seat locking, scheduling automated holds release, and JWT-based admin controls.

---

## Repository Structure

```
TicketBookingSystem/
├── backend/               # Spring Boot Application (API Server)
│   ├── src/               # Source code (Java)
│   ├── build.gradle       # Gradle Build configuration
│   └── ...
├── TicketSystemFront/     # React + Vite client (Admin & Passenger UI)
│   ├── src/               # Source code (React components & logic)
│   ├── package.json       # Node package configuration
│   └── ...
└── README.md              # Project documentation
```

---

## Technology Stack

### Backend
- **Language & Runtime:** Java 17
- **Framework:** Spring Boot 4.x
- **Security:** Spring Security (HTTP Basic Auth & Stateless JWT Verification)
- **Database:** PostgreSQL 14.x
- **Persistence:** Spring Data JPA / Hibernate (DDL updates auto-managed)
- **Build System:** Gradle 9.x

### Frontend
- **Framework:** React 19 (Vite)
- **Styling:** Vanilla CSS modules (Navy & Saffron themed layout)
- **State Management:** In-memory JWT context & LocalStorage token syncing
- **Build Tool:** Vite 8.x

---

## Getting Started

### Prerequisites
- **Java JDK 17** or higher
- **Node.js** (v18+ recommended) & npm
- **PostgreSQL** database running locally (default port: 5432)

---

### 1. Setting Up the Backend

1. **Create the Database:**
   Connect to your PostgreSQL server and create a database named `ticket_booking`:
   ```sql
   CREATE DATABASE ticket_booking;
   ```

2. **Configure Database Secrets:**
   Set the following environment variables in your terminal before running:
   - `DB_URL`: `jdbc:postgresql://localhost:5432/ticket_booking`
   - `DB_USERNAME`: `your_postgres_username`
   - `DB_PASSWORD`: `your_postgres_password`
   - `JWT_SECRET_KEY`: A secure key containing at least 32 characters (e.g. `mySuperSecureSecretJwtKeyStringNeedToBeLong`)

3. **Run the Application:**
   Navigate into the `backend/` directory and build/run using Gradle:
   ```bash
   cd backend
   ./gradlew bootRun
   ```
    The backend API will start on `http://localhost:8080/api/v1`

4. **Running Tests:**
   Execute the automated JUnit 5 and Mockito unit tests in isolation (no database required):
   ```bash
   ./gradlew test
   ```

---

### 2. Setting Up the Frontend

1. **Install Dependencies:**
   Navigate to the `TicketSystemFront/` directory and install NPM packages:
   ```bash
   cd TicketSystemFront
   npm install
   ```

2. **Run Linter (Optional):**
   Ensure everything is formatted and free of syntax issues:
   ```bash
   npm run lint
   ```

3. **Launch the Dev Server:**
   Start Vite:
   ```bash
   npm run dev
   ```
   The app will run locally at `http://localhost:5173`

---

## Core Features

### JWT Authentication & Roles
The application uses stateful login but stateless JWT validation:
- **Guests:** Can search for trips, view seat layouts, and make guest bookings anonymously.
- **Passengers:** Can register a user account, log in, and book seats under their persistent profile.
- **Administrators:** Log in to access the Admin Console, allowing them to manage physical buses, routes, and schedule timetables.

### Pessimistic Seat Locking & Auto-Release Scheduler
- When a user chooses seats and clicks "Confirm & Hold", the backend locks the selected seats using a pessimistic write lock (`PESSIMISTIC_WRITE`) and marks their status as `PENDING`.
- Seats are held for exactly 10 minutes.
- An `@Scheduled` task runs automatically in the backend every 5 minutes, checking the database for expired locks and releasing them back to `AVAILABLE`.

### Route-Level Boarding & Dropping Points
- Supports multiple pickup (boarding) and drop-off points for each route.
- Each stop is configured with an offset time relative to the trip's start time and has absolute stop times dynamically mapped on retrieval.
- Booking entries explicitly record the selected boarding and dropping stop names at check-out.

### Multi-City Itinerary Travel Planner
- Allows mapping a complex multi-hop travel itinerary across multiple destinations.
- Supports both **Fixed Order** (strict sequence of cities) and **Flexible Order** (the Traveling Salesperson algorithm computes the optimal visiting order keeping the start city fixed).
- Validates a minimum **2-hour transfer buffer** between consecutive trips.
- Finds both the **Cheapest Route** (minimum sum of fares) and the **Shortest Route** (minimum sum of distances), allowing constraints like arrival dates.

---

## API Documentation

All API requests are prefixed with `/api/v1` (the backend servlet context path).

### Authentication (`/auth`)

#### `POST /auth/register`
- Registers a new user. Administrators cannot be registered publicly.
- **Request Body:**
  ```json
  {
    "name": "passenger_username",
    "password": "secure_password",
    "roles": ["ROLE_USER"]
  }
  ```

#### `POST /auth/login`
- Authenticates credentials and returns a plain text JWT token.
- **Request Body:**
  ```json
  {
    "name": "passenger_username",
    "password": "secure_password"
  }
  ```
- **Response:** Raw JWT String (e.g. `eyJhbGciOiJIUzI1NiJ9...`)

---

### Operations (`/trips` & `/bookings`)

#### `GET /trips`
- Search schedules between source and destination cities on a specific date. All parameters are optional.
- **Query Parameters:** `sourceCity`, `destinationCity`, `date` (format: `YYYY-MM-DD`)

#### `GET /trips/{id}/seats`
- Fetch the physical seat map status for a trip (`AVAILABLE`, `PENDING`, `BOOKED`).

#### `POST /bookings`
- Books/holds selected seats. Accessible by anyone.
- **Request Body (Guest):**
  ```json
  {
    "tripId": 12,
    "seatIds": [101, 102],
    "isGuest": true,
    "guestInfo": {
      "email": "passenger@domain.com",
      "number": "9876543210"
    },
    "userId": null,
    "bypassAccountCheck": false
  }
  ```
- **Request Body (Registered Passenger - JWT Authenticated):**
  ```json
  {
    "tripId": 12,
    "seatIds": [101, 102],
    "isGuest": false,
    "guestInfo": null,
    "userId": null,
    "bypassAccountCheck": false
  }
  ```
  > [!NOTE]
  > When `isGuest` is `false` and `userId` is omitted, the backend dynamically resolves the user's ID from the security context of the JWT token.

---

### Travel Planner & Route Stops

#### `POST /admin/routes/{routeId}/stops`
- Configure pick-up and drop-off points for a route. Requires admin access.
- **Request Body:**
  ```json
  [
    {
      "stopName": "Gachibowli",
      "stopType": "BOARDING",
      "minutesOffset": 0,
      "sequence": 1
    },
    {
      "stopName": "Majestic",
      "stopType": "DROPPING",
      "minutesOffset": 360,
      "sequence": 2
    }
  ]
  ```

#### `POST /trips/plan`
- Plan a multi-hop travel itinerary across multiple cities. Finds both the cheapest route and the shortest route, ensuring a minimum 2-hour transfer buffer.
- **Request Body:**
  ```json
  {
    "startCity": "Hyderabad",
    "startDate": "2026-07-01",
    "destinations": [
      {
        "cityName": "Bangalore",
        "arrivalDate": "2026-07-02"
      },
      {
        "cityName": "Chennai",
        "arrivalDate": null
      }
    ],
    "flexibleOrder": true
  }
  ```
- **Response:**
  ```json
  {
    "cheapestRoute": {
      "trips": [ ... ],
      "totalFare": 1600.00,
      "totalDistance": 850
    },
    "shortestDistanceRoute": {
      "trips": [ ... ],
      "totalFare": 2000.00,
      "totalDistance": 850
    }
  }
  ```

---

### Administrative Operations (`/admin`)
Requires JWT authenticated request with `ROLE_ADMIN` role.

#### `POST /admin/buses`
- Register a bus vehicle (max capacity: 52 seats).
- **Request Body:**
  ```json
  {
    "busNumber": "TS-09-UB-1234",
    "busName": "Super luxury sleeper",
    "busType": "AC Sleeper",
    "totalSeats": 40
  }
  ```

#### `POST /admin/routes`
- Register a route between two cities.
- **Request Body:**
  ```json
  {
    "sourceCity": "Hyderabad",
    "destinationCity": "Bangalore",
    "distanceKm": 575
  }
  ```

#### `POST /admin/trips`
- Schedules a timetable trip, mapping a bus to a route with start/end schedules and pricing.
- **Request Body:**
  ```json
  {
    "busId": 1,
    "routeId": 1,
    "startTime": "2026-06-30T22:00:00",
    "arrivalTime": "2026-07-01T06:00:00",
    "fare": 1250.00
  }
  ```

---

## TODO Status

- [x] Repositories
- [x] Services (booking flow, seat locking, payment, expiry scheduler)
- [x] Controllers
- [x] DTOs and request/response models
- [x] Scheduled purge job for expired bookings (TripSeat lock auto-release)
- [x] Async email dispatch post-booking
- [x] Security config and role enforcement
- [x] Exception handling / error responses
- [x] Tests
