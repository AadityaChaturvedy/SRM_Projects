# 🚂 Railway Booking System

This project is a full-stack railway booking application featuring a JavaFX client and a Spring Boot backend. It provides a comprehensive set of features for users to search for trains, book tickets, view their booking history, and manage their journey.

## ✨ Features

- **User Authentication**: Secure registration and login for users.
- **Train Search**: Search for trains between two stations, with an optional "via" station for more specific routing.
- **Dynamic Date Filtering**: The travel date picker only shows dates for which trains are available on the selected route.
- **Class-Based Pricing**: Ticket prices are calculated based on the selected travel class (Sleeper, AC, First Class).
- **Seat Availability**: Real-time checking of available seats.
- **Multi-Traveler Booking**: Book tickets for multiple travelers at once.
- **Payment Integration**: A simulated payment process with options for UPI, Card, and Net Banking.
- **PDF Ticket Generation**: After a successful booking, a PDF ticket with all relevant details (including a unique PNR) is generated and can be saved by the user.
- **Booking Management**: Users can view their booking history and cancel confirmed tickets.
- **CSV Export**: Export booking history to a CSV file.

## 🛠️ Tech Stack

- **Backend**:
  - **Framework**: Spring Boot 3
  - **Language**: Java 17
  - **Database**: SQLite with Spring Data JPA & Hibernate
  - **API**: RESTful API
- **Frontend**:
  - **Framework**: JavaFX 21
  - **Language**: Java 17
  - **HTTP Client**: OkHttp3
  - **JSON Parsing**: Gson
- **PDF Generation**: iTextPDF
- **Build Tool**: Maven

## 🚀 How to Run the Application

### Prerequisites

- JDK 17 or later
- Maven 3.6 or later

### 1. Run the Backend

Navigate to the `spring-backend` directory and run the Spring Boot application:

```bash
cd spring-backend
mvn spring-boot:run
```

The backend server will start on `http://localhost:8080`.

### 2. Run the Frontend

Navigate to the `client-javafx` directory and run the JavaFX application:

```bash
cd client-javafx
mvn clean javafx:run
```

The JavaFX client will launch, and you can start using the application.

## 🏗️ Project Structure

The project is divided into two main modules: `spring-backend` and `client-javafx`.

### `spring-backend`

- `src/main/java/com/railway/booking`:
  - `controller`: Contains the `RailwayController` which handles all API requests.
  - `dto`: Data Transfer Objects (`UserDTO`, `BookingRequest`) for handling request bodies.
  - `model`: JPA entities (`User`, `Train`, `Booking`) that map to the database tables.
  - `repository`: Spring Data JPA repositories (`UserRepository`, `TrainRepository`, `BookingRepository`) for database operations.
  - `BookingApplication.java`: The main entry point for the Spring Boot application.
- `src/main/resources`:
  - `application.properties`: Configuration for the Spring application, including database connection details.
  - `populate_trains.sql`: An SQL script to populate the database with initial train data.

### `client-javafx`

- `src/main/java/com/railway/booking`:
  - `App.java`: The main entry point for the JavaFX application, handling scene management.
  - `model`: Client-side data models (e.g., `Train`).
  - `service`:
    - `ApiClient.java`: Handles all communication with the backend REST API.
    - `PdfService.java`: Responsible for generating PDF tickets.
  - `util`:
    - `UIUtils.java`: Provides utility functions for showing alerts.
  - `view`: Contains all the UI components of the application, separated into different views for each screen (e.g., `LoginView`, `DashboardView`, `SearchTrainView`).
- `src/main/resources`:
  - `styles/style.css`: CSS file for styling the JavaFX application.

## 🔄 Application Flow

1.  **Login/Register**: The user is first presented with a login screen. They can either log in with existing credentials or register for a new account.
2.  **Dashboard**: After a successful login, the user is taken to the main dashboard, which has two tabs: "Search Trains" and "My Bookings".
3.  **Search Trains**:
    - The user can search for trains by selecting a source, destination, and travel date. An optional "via" station can also be specified.
    - The search results are displayed in a table, showing train details, price, and seat availability.
4.  **Booking**:
    - Clicking the "Book Now" button opens a dialog to select the travel class and number of seats.
    - The total price is calculated and displayed for confirmation.
    - The user then selects a payment method and enters the required details.
    - Finally, the user enters the information for each traveler.
5.  **Ticket Generation**:
    - Upon successful booking, a unique PNR is generated.
    - A PDF ticket is created with all the booking and traveler details, and the user is prompted to save it.
6.  **My Bookings**:
    - This tab displays a history of all the user's bookings.
    - The user can cancel any "CONFIRMED" booking.
    - The booking history can be exported to a CSV file.
