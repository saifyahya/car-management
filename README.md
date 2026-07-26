# Hotel Valet MVP

A packaged full-stack MVP for hotel valet coordination.

## Included
- Staff login with Spring Security HTTP Basic
- Vehicle check-in and digital ticket generation
- Console SMS gateway containing visitor link and pickup PIN
- Visitor page with no login and vehicle request action
- Live staff queue (manual refresh) and visitor polling every 5 seconds
- Assignment, retrieval, ready, and delivery workflow
- H2 database by default; PostgreSQL profile included
- Angular reusable standalone components with separate `.ts`, `.html`, and `.css` files

## Run backend
Requires Java 21 and Maven 3.9+.

```bash
cd backend
mvn spring-boot:run
```

Backend: `http://localhost:8080`
H2 console: `http://localhost:8080/h2-console`

## Run frontend
Requires Node.js 22 and npm.

```bash
cd frontend
npm install
npm start
```

Frontend: `http://localhost:4200`

Demo users:
- `admin / admin123`
- `valet / valet123`

## PostgreSQL
Start PostgreSQL:

```bash
docker compose up -d postgres
```

Then run the backend with:

```bash
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
```

## SMS integration
The default `LoggingSmsGateway` writes messages to the backend log. Replace it with an implementation of `SmsGateway` that calls your selected SMS provider.

## Important production tasks
- Move demo users to a database and enforce password rotation.
- Hash public access tokens and pickup PINs.
- Configure HTTPS and a real domain.
- Add rate limiting to public endpoints.
- Replace `ddl-auto: update` with Flyway migration scripts.
- Add SMS outbox/retry processing.
- Add database constraints and integration tests for concurrent assignment and transitions.

## Run the complete stack with Docker

```bash
docker compose -f docker-compose.full.yml up --build
```

Open `http://localhost:4200`.
