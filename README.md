# Group 22 – Urban Mobility System
### VU Information Management and Systems Engineering (2026S) · Milestone 2

**Students:** Ekhtyar Sediqullah · Nikben Mohammad Latif · Sharifzai Mesbahuddin

---

## Tech Stack

| Layer      | Technology                        |
|------------|-----------------------------------|
| Backend    | Spring Boot 3.2 (Java 21)         |
| Frontend   | Thymeleaf + Tailwind CSS           |
| RDBMS      | MariaDB 11                        |
| NoSQL      | MongoDB 7                         |
| Containers | Docker + Docker Compose            |

---

## Prerequisites

- **Java 21** — verify with `java -version`
- **Maven** — verify with `mvn -version`
- **Docker Desktop** — [download here](https://www.docker.com/products/docker-desktop/)

---

## How to Run

### Step 1 — Start the databases

```bash
docker compose up -d
```

This starts:
- **MariaDB** on port `3306` (auto-creates schema + sample data from `sql/init.sql`)
- **MongoDB** on port `27017`

Wait about 10 seconds for MariaDB to finish initialising, then verify:

```bash
docker compose ps          # both containers should be "Up"
docker compose logs mariadb # look for "ready for connections"
```

### Step 2 — Run the Spring Boot app

```bash
./mvnw spring-boot:run
```

Or if you don't have the Maven wrapper yet:
```bash
mvn spring-boot:run
```

### Step 3 — Open the app

Visit: **http://localhost:8080**

---

## Project Structure

```
g22-mobility/
├── docker-compose.yml              ← starts MariaDB + MongoDB
├── sql/
│   └── init.sql                    ← schema + sample data (auto-runs)
├── pom.xml
└── src/main/
    ├── java/com/group22/mobility/
    │   ├── MobilityApplication.java
    │   ├── controller/             ← web controllers (one per student)
    │   ├── model/                  ← JPA entity classes
    │   ├── repository/
    │   │   ├── mariadb/            ← Spring Data JPA repos
    │   │   └── mongodb/            ← Spring Data MongoDB repos
    │   ├── service/                ← business logic
    │   └── dto/                    ← form + report DTOs
    └── resources/
        ├── application.properties  ← DB connection config
        └── templates/
            ├── layouts/main.html   ← shared nav layout
            ├── index.html          ← homepage
            ├── student1/           ← Sediqullah's pages
            ├── student2/           ← Mohammad Latif's pages
            └── student3/           ← Mesbahuddin's pages
```

---

## Database Credentials

| DB      | Host      | Port  | Database       | User    | Password    |
|---------|-----------|-------|----------------|---------|-------------|
| MariaDB | localhost | 3306  | mobility_db    | g22user | g22password |
| MongoDB | localhost | 27017 | mobility_nosql | g22user | g22password |

---

## Stopping Everything

```bash
docker compose down        # stop containers (data persists)
docker compose down -v     # stop + DELETE all data (fresh start)
```

---

## Use Cases Implemented

| Student | Use Case | Analytics |
|---------|----------|-----------|
| S1 – Sediqullah | Report Vehicle Damage → INSERT Maintenance_Log | Maintenance Cost per Station |
| S2 – Mohammad Latif | Rent a Vehicle → INSERT Rental | Rental Activity by User |
| S3 – Mesbahuddin | Onboard Technician → INSERT Employee + Technician | Level 2 Technician Report |
