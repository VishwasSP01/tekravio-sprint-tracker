# Tekravio Project & Sprint Tracker

Spring Boot REST API for Tekravio's internal consulting project tracker.

## Current Scope

Task 01 establishes the Maven project, layered package structure, five JPA entities, H2 development
configuration, MySQL production configuration, versioned Flyway migration, and startup seed data.

## Run Locally

Requirements: Java 21+ and Maven 3.9+.

```bash
mvn spring-boot:run
```

The default development profile uses an in-memory H2 database. Start the production configuration
with `--spring.profiles.active=prod` and set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.

## Foundation Decisions

- Maven keeps the build conventional and gives the later JaCoCo requirement a small configuration
  footprint.
- Parent references use lazy loading so routine queries do not fetch an entire object graph.
- Collections are bidirectional where the domain naturally needs parent-to-child navigation.
- Sprint numbers are unique within a project rather than globally.
- A task may be unassigned, but every task belongs to a sprint.
- Clients have a soft-delete flag because the required DELETE API must preserve historical data.
- Flyway owns schema creation in every environment. Hibernate validates that mappings still match
  the migration instead of silently changing a production database.

The detailed schema tradeoffs, health score formula, and mindset answers will be completed with the
final submission.
