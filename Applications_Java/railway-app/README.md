# 🚄 Railway Management System

A robust, multi-tier application for railway ticketing, scheduling, and administrative management. This project demonstrates a clean separation of concerns between a Spring Boot backend and a JavaFX desktop frontend.

## 🛠️ Key Components

### 1. [Spring Boot Backend](./spring-backend)
- **Framework**: Spring Boot 3.x
- **Database**: SQLite (`railway.db`)
- **Core Features**: RESTful APIs for ticketing, user management, and schedule queries.
- **ORM**: Spring Data JPA with Hibernate.

### 2. [JavaFX Frontend](./client-javafx)
- **UI Framework**: JavaFX 17+
- **Features**: Interactive dashboard, ticket booking interface, and administrative controls.
- **Integration**: Communicates with the backend via HTTP/REST.

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- Maven 3.6+

### Execution

1.  **Start the Backend**:
    ```bash
    cd spring-backend
    mvn spring-boot:run
    ```

2.  **Start the Client**:
    ```bash
    cd client-javafx
    mvn javafx:run
    ```

---
*Enterprise Java Architecture Portfolio Project.*
