# Tripflow

**Tripflow** is a Java backend project for managing trips and their participants. The project is being developed as a practical learning project to explore backend development with Spring Boot, relational databases, and event-driven communication using Apache Kafka.
The main goal is to build a REST API, work with persistent data, and gain practical experience with backend development tools and technologies.

## Features

- **Trip management** — create, retrieve, and delete trips through a REST API.
- **Participant management** — associate users with trips.
- **Database integration** — persist application data in PostgreSQL.
- **ORM and data access** — use Spring Data JPA and Hibernate to work with database entities and relationships.
- **Event-driven communication** — publish trip creation events to Apache Kafka and process them with a Kafka consumer.
- **Containerized infrastructure** — run PostgreSQL and Kafka using Docker Compose.
- **Automated testing** — initial automated tests for application components and Spring Boot configuration.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.1 | Backend application framework |
| Spring Web | REST API and HTTP request handling |
| Spring Data JPA | Data access and repository abstraction |
| Hibernate | ORM and entity mapping |
| PostgreSQL 17 | Relational database |
| Apache Kafka 4.0.0 | Event-driven communication |
| Maven | Dependency management and build automation |
| Docker | Containerization |
| Docker Compose | Local infrastructure management |
| Git and GitHub | Version control and source code hosting |

## Architecture

The application follows a layered backend architecture.

```text
Client
  |
  v
REST API
  |
  v
Controller
  |
  v
Service
  |
  +--------------------+
  |                    |
  v                    v
Repository         Kafka Producer
  |                    |
  v                    v
PostgreSQL          Kafka Topic
                       |
                       v
                  Kafka Consumer
```

### Main Components

- **Controller layer** — receives HTTP requests and exposes REST endpoints.
- **Service layer** — contains application logic.
- **Repository layer** — provides access to persistent data through Spring Data JPA.
- **Entity layer** — represents application data and relationships between entities.
- **Kafka producer** — publishes trip creation events.
- **Kafka consumer** — receives and processes events from Kafka.

## Database

PostgreSQL is used as the primary relational database.
The application uses Spring Data JPA and Hibernate to map Java entities to database tables.
The domain model includes trips, users, and trip participants. A trip participant represents the relationship between a user and a trip.
Entity relationships are configured using JPA annotations, including `@ManyToOne`.

## Apache Kafka Integration

Tripflow includes a learning implementation of event-driven communication using Apache Kafka.
The current event flow is:
1. A client sends a request to create a trip.
2. The application processes the request and persists the trip in PostgreSQL.
3. The application publishes a `TripCreatedEvent` to the `trip-events` Kafka topic.
4. A Kafka consumer in the `tripflow-group` consumer group receives and processes the event.

This integration demonstrates the basic interaction between a Spring Boot application and Kafka using a producer, a topic, and a consumer.
The Kafka integration is currently a learning component and may be extended as the project evolves.

## Getting Started
### Prerequisites

Make sure you have installed:
- JDK 21
- Docker Desktop
- Git
Maven can be run through the Maven Wrapper included in the repository.

### 1. Clone the repository

```bash
git clone https://github.com/yaktomy/tripflow.git
cd tripflow
```

### 2. Start the infrastructure

Start the PostgreSQL and Kafka containers defined in the Docker Compose configuration:
```bash
docker compose up -d
```
Check the container status:
```bash
docker compose ps
```

### 3. Configure the application

Check the application configuration files under:
```text
src/main/resources/
```
Make sure the PostgreSQL connection settings match your local Docker configuration.
If Kafka settings are configured separately, make sure the application can connect to the Kafka broker.

### 4. Run the application

On Windows PowerShell:
```powershell
.\mvnw.cmd spring-boot:run
```
Alternatively, build the project:
```powershell
.\mvnw.cmd clean package
```
Then run the generated JAR from the `target` directory:
```powershell
java -jar target/tripflow-0.0.1-SNAPSHOT.jar
```
The application starts on the port configured in its application settings.

### 5. Stop the infrastructure

To stop the Docker Compose services:
```bash
docker compose down
```

## API Overview

The application exposes REST endpoints for working with trips and their participants.
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/trips` | Create a trip |
| `GET` | `/trips` | Retrieve trips |
| `POST` | `/trips/{tripId}/members/{userId}` | Add an existing user to a trip |
Trip deletion is also implemented. Check the trip controller for the exact HTTP method and endpoint.

### Example: Create a Trip

**Request**
```http
POST /trips
Content-Type: application/json
```
```json
{
  "title": "Trip to Italy",
  "description": "Rome and Florence"
}
```

**Retrieve trips**

```http
GET /trips
```
The available endpoints and request models may evolve as development continues.

## Testing

The project includes an initial set of automated tests using the Spring Boot testing infrastructure.
Run the tests with:
```powershell
.\mvnw.cmd test
```
Test coverage is incomplete, and additional unit and integration tests are planned.

## Current Limitations

Tripflow is an ongoing learning project rather than a production-ready application.
Current limitations include:
- Authentication and authorization are not implemented.
- Automated test coverage is incomplete.
- The Kafka integration is primarily intended to practise event-driven communication.
- API functionality and error handling can be extended further.

## Future Improvements

Potential next steps include:
- Expanding unit and integration test coverage.
- Improving request validation and error handling.
- Reviewing entity relationships and API response serialization.
- Improving the Kafka integration and event processing.
- Adding authentication and authorization.
- Improving API documentation.

## Author

**Marselina Zarifullina**
Aspiring Java Backend Developer.

GitHub: [@yaktomy](https://github.com/yaktomy)
