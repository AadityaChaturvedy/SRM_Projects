# 🎓 SRM Projects Portfolio

Welcome to my comprehensive portfolio of engineering projects developed during my academic journey at SRM. This repository serves as a curated collection of diverse technical implementations across AI/ML, Java Development, Full-Stack Web, and IoT/Embedded Systems.

---

## 🏛️ Repository Architecture

The repository is structured logically by domain to facilitate easy navigation and understanding of each project's scope.

### 🧠 [AI & Machine Learning](./AI_ML_Projects)
Deep learning and predictive modeling projects focusing on real-world applications.
*   **[Sustainable Emotion AI](./AI_ML_Projects/sustainable-emotion-ai)**: Emotion recognition system using CNNs and sustainable computing practices.

### ☕ [Java Applications](./Applications_Java)
A wide array of enterprise-level and desktop applications built using Swing, JavaFX, and Spring Boot.
*   **[Railway Management System](./Applications_Java/railway-app)**: Multi-tier architecture with Spring Boot backend and JavaFX frontend.
*   **[Hostel Management System](./Applications_Java/hostel-management-system)**: Automated record-keeping for student and facility management.
*   **[Japanese Restaurant Manager](./Applications_Java/japanese-restaurant-manager)**: Point-of-Sale and back-office management system.
*   **[File Encryption Utility](./Applications_Java/FileEncryptionApp)**: Advanced cryptographic tool for securing local and transmitted data.
*   **[EduTrack](./Applications_Java/EduTrack)** & **[EduBase](./Applications_Java/EduBase)**: Integrated systems for tracking academic progress and student records.
*   **[Additional Utilities](./Applications_Java)**: Including Alzheimer's management, cash recording, and parking systems.

### 📟 [IoT & Embedded Systems](./IoT_Embedded_Systems)
Hardware-centric projects involving ESP32, Arduino, and FPGA interfacing.
*   **[CPU Dashboard](./IoT_Embedded_Systems/CPU_Dashboard)**: Real-time system monitoring using ESP32 with a web interface.
*   **[Medical Monitoring System](./IoT_Embedded_Systems/Medical)**: Integrated health tracking using hardware sensors and Firebase.
*   **[FPGA Interfacing](./IoT_Embedded_Systems/FPGA_Project)**: Low-level hardware description and C++ serial communication.
*   **[Industrial Modules](./IoT_Embedded_Systems)**: Specialized systems like Missile Defense, Plant Monitoring, and RFID Toll Collection.

### 🌐 [Web & Simulations](./Simulations_Web)
High-fidelity simulations and modern web applications.
*   **[8086 Microprocessor Simulator](./Simulations_Web/8086_Simulator)**: A feature-rich emulator for 8086 assembly programming and debugging.
*   **[Full-Stack Planner](./FullStack_Web/Planner)**: A modern timetable management tool for academic scheduling.

---

## 🛠️ Technology Stack & Expertise

| Category | Technologies |
| :--- | :--- |
| **Languages** | Java, C++, Python, Verilog, JavaScript (ES6+), SQL |
| **Frameworks** | Spring Boot, JavaFX, Maven, FastAPI, React.js |
| **Hardware** | ESP32, Arduino Uno/Nano, FPGA (Tang Nano 9K) |
| **Cloud/DB** | Firebase, SQLite, MySQL, JSON-based storage |

---

## 🚀 Getting Started & Execution

Each project directory includes a detailed `README.md` with setup instructions. General guidelines:

1.  **Java Projects**: Require **JDK 17+** and **Maven**. Build using `mvn clean install`.
2.  **IoT Projects**: Compatible with **Arduino IDE**. Ensure required libraries (found in `IoT_Embedded_Systems/libraries`) are installed.
3.  **Web Projects**: Run `index.html` for frontend-only projects or follow `npm install` instructions for modern web apps.

---

## 🔐 Security & Configuration

To protect sensitive information, this repository uses environment variables for configuration. 

1.  **Backend Services**: Copy the [`.env.template`](./.env.template) to `.env` in the root or respective project directory and fill in your credentials.
2.  **IoT/Arduino**: For projects in `IoT_Embedded_Systems`, hardcoded credentials have been redacted. Create a `config.h` file in the project directory using the placeholders provided in the source code.

---

## 👨‍💻 Engineering Standards

- **Clean Code**: Adherence to SOLID principles and idiomatic coding styles.
- **Documentation**: Comprehensive READMEs for all major modules.
- **Modularity**: Separation of concerns across frontend, backend, and hardware layers.

---
*© 2026 Aaditya Chaturvedy. All rights reserved.*
