# 🖥️ Planner Backend

The server-side implementation for the Full-Stack Academic Planner, handling API requests and data persistence.

## 🛠️ Tech Stack
- **Framework**: Spring Boot (Java)
- **Database**: PostgreSQL / Supabase
- **Key Features**: RESTful API endpoints, JWT Authentication, Academia Integration.

## 🚀 Setup & Configuration

This project requires environment variables for database connectivity and external API access.

1.  **Environment Variables**: Create a `.env` file in the root directory (refer to [`.env.template`](../../../.env.template)) and provide:
    - `DB_HOST`, `DB_USERNAME`, `DB_PASSWORD`
    - `SUPABASE_URL`, `SUPABASE_ANON_KEY`
    - `ZOHO_CSRF_TOKEN`, `ZOHO_COOKIE` (Optional, for Academia sync)

2.  **Build**:
    ```bash
    mvn clean install
    ```

3.  **Run**:
    ```bash
    mvn spring-boot:run
    ```

---
*Full-Stack Web Development Portfolio Project.*
