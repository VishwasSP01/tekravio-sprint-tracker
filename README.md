# Tekravio Project & Sprint Tracker

Spring Boot REST API for managing consulting clients, projects, sprints, tasks, and engineer workloads.

## Features

- Layered `Controller -> Service -> Repository` architecture with DTO-only API responses
- H2 development database and MySQL production configuration
- Versioned Flyway migrations with Hibernate schema validation
- CRUD APIs for all five domain entities with pagination and sorting
- Client soft deletion to preserve historical project data
- Bean validation, consistent response envelopes, and centralized exception handling
- Forward-only task workflow: `TODO -> IN_PROGRESS -> REVIEW -> DONE`
- Engineer availability rule enforcement during task assignment
- Sprint summary, engineer workload, project health, capacity-aware availability, and task filtering APIs
- JWT authentication with `ADMIN` and `ENGINEER` authorization rules
- Task status audit history with the acting user's username
- OpenAPI documentation and Swagger UI
- Multi-stage Docker image and Docker Compose stack for MySQL-backed execution
- JUnit 5, Mockito, MockMvc integration tests, and a JaCoCo service-layer coverage gate

## Requirements

- Java 21+
- Maven 3.9+
- MySQL 8+ only when running the production profile

## Run Locally

```bash
mvn spring-boot:run
```

The default configuration starts with an in-memory H2 database and seeded sample data. The H2
console is available at `http://localhost:8080/h2-console` using JDBC URL
`jdbc:h2:mem:tracker`, username `sa`, and an empty password.

Seeded development accounts:

| Role | Username | Password |
| --- | --- | --- |
| Administrator | `admin` | `Admin@123` |
| Java engineer | `aarav` | `Engineer@123` |
| React engineer | `diya` | `Engineer@123` |

Run the production profile with:

```bash
DB_URL=jdbc:mysql://localhost:3306/tekravio_tracker \
DB_USERNAME=tracker \
DB_PASSWORD=tracker \
JWT_SECRET=replace-with-a-long-random-production-secret \
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Flyway applies the schema migrations in both environments. Hibernate validates that entity mappings
match the migrated schema instead of changing it automatically.

## Run with Docker

```bash
docker compose up --build
```

Docker Compose starts MySQL 8.4 and the tracker API at `http://localhost:8080`. The bundled credentials
and JWT secret are local demonstration values only. Replace them before deploying the stack. Seed data
is disabled by default for the `prod` profile and enabled explicitly by the Compose file for a
ready-to-review environment.

## Verify

```bash
mvn verify
```

This runs unit and integration tests, creates the JaCoCo report at
`target/site/jacoco/index.html`, and fails the build if service-layer line coverage drops below 60%.

## API Overview

All successful responses use:

```json
{"success": true, "data": {}, "message": ""}
```

All list endpoints accept Spring pagination parameters such as `page=0&size=20&sort=name,asc`.

Authenticate with `POST /api/auth/login`, then send the returned token in the
`Authorization: Bearer <token>` header. Swagger UI is available at
`http://localhost:8080/swagger-ui.html`, and the OpenAPI document is available at
`http://localhost:8080/v3/api-docs`.

Access rules:

| Action | Required role |
| --- | --- |
| Login and API documentation | Public |
| Read API data | `ADMIN` or `ENGINEER` |
| Update an assigned task or advance its status | `ADMIN`, or the assigned `ENGINEER` |
| Create, delete, assign, and other write operations | `ADMIN` |

| Resource | Core endpoints |
| --- | --- |
| Authentication | `POST /api/auth/login` |
| Clients | `POST /api/clients`, `GET /api/clients`, `GET /api/clients/{id}`, `PUT /api/clients/{id}`, `DELETE /api/clients/{id}` |
| Projects | `POST /api/projects`, `GET /api/projects`, `GET /api/projects/{id}`, `PUT /api/projects/{id}`, `DELETE /api/projects/{id}` |
| Sprints | `POST /api/sprints`, `GET /api/sprints`, `GET /api/sprints/{id}`, `PUT /api/sprints/{id}`, `DELETE /api/sprints/{id}` |
| Tasks | `POST /api/tasks`, `GET /api/tasks`, `GET /api/tasks/{id}`, `PUT /api/tasks/{id}`, `DELETE /api/tasks/{id}` |
| Engineers | `POST /api/engineers`, `GET /api/engineers`, `GET /api/engineers/{id}`, `PUT /api/engineers/{id}`, `DELETE /api/engineers/{id}` |

Intelligence endpoints:

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/projects/{id}/sprints` | Paginated project sprints |
| `POST` | `/api/sprints/{id}/tasks` | Add a task to a sprint |
| `PUT` | `/api/tasks/{id}/status` | Advance task status by one step |
| `GET` | `/api/tasks/{id}/history` | Review status-transition audit history |
| `PUT` | `/api/tasks/{id}/assign/{engineerId}` | Assign an available engineer |
| `GET` | `/api/sprints/{id}/summary` | Sprint completion and overdue metrics |
| `GET` | `/api/engineers/{id}/workload` | Engineer task and hour totals |
| `GET` | `/api/projects/{id}/health` | Project health score |
| `GET` | `/api/engineers/available?stack=JAVA` | Available engineers with fewer than three active tasks |
| `GET` | `/api/tasks?status=IN_PROGRESS&priority=HIGH&sprintId=1` | Multi-parameter task filtering |

## Assumptions

- New tasks always start in `TODO`; updates must advance exactly one workflow step.
- Tasks may be unassigned, but every task belongs to a sprint.
- Sprint numbers are unique within a project rather than globally.
- Deleting a client is a soft delete because historical project reporting must remain intact.
- Project, sprint, task, and engineer deletion remain hard deletes because only client soft deletion
  is required. A production rollout would revisit archival rules with stakeholders.

## Mindset Questions

### 1. Why this schema? What changes at 10,000 projects?

The schema follows the ownership chain: a client owns projects, a project owns sprints, and a sprint
owns tasks. Engineer assignment is nullable because backlog tasks can exist before staffing. Foreign
keys protect integrity, and indexes cover relationship traversal plus the required task filters.

At 10,000 projects, I would inspect query plans and production traffic before adding more indexes.
Likely next steps are indexed archival flags, cursor pagination for large task feeds, database-level
check constraints, and aggregate read models for reporting endpoints if live calculations become
expensive.

### 2. What is the project health score formula?

```text
score = max(0, completionPercentage * 0.8 - min(20, overdueTasks * 5))
```

Completion contributes up to 80 points. Each overdue task subtracts 5 points, capped at 20, so delays
matter without making the score unusable after one difficult sprint. This formula is deliberately
simple enough to explain and tune with stakeholders.

### 3. What was hardest? What references were needed?

The most careful part was keeping lifecycle rules, reporting metrics, and persistence design
consistent. Sprint summaries need completion timestamps, so task lifecycle timestamps were added
through a Flyway migration rather than hidden schema mutation. The implementation was checked against
the assignment brief and verified through Spring Boot integration tests, Hibernate validation, and
the JaCoCo report.

### 4. What would one more day add?

I would add Testcontainers coverage against MySQL and continuous integration checks. H2 MySQL mode is
useful for fast tests, while a real MySQL container catches dialect-specific migration issues before
deployment. CI would make the existing test, coverage, and packaging gates mandatory for every pull
request.

### 5. What is worth being proud of?

The migration-backed foundation and business-rule tests. The application does more than expose CRUD:
it rejects invalid workflow changes, protects unavailable engineers from assignment, reports useful
operational metrics, records task-status history, protects writes with role-aware JWT authorization,
and fails the build if service coverage falls below the agreed threshold.
