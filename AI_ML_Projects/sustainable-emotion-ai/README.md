# Sustainable Emotion AI

This project is a real-time emotion recognition application that analyzes video feed from a webcam. It is built with a JavaFX frontend and a Python backend, with a focus on tracking and displaying the performance metrics and energy consumption of the AI model.

## Features

- **Real-time Emotion Recognition:** Detects emotions from a live webcam feed.
- **Multi-face Detection:** Capable of detecting and analyzing multiple faces in the frame.
- **Emotion Statistics:** Provides a summary of detected emotions, including counts, confidence levels, and timestamps.
- **Performance Monitoring:** Displays key performance indicators like CPU usage, memory usage, and processing latency.
- **Energy Consumption Tracking:** Shows an estimate of the energy consumed by the AI processing.
- **User-friendly Interface:** A clean and intuitive interface built with JavaFX.

## System Architecture

The application uses a client-server architecture:

- **Frontend (Client):** A JavaFX application that captures video from the webcam, displays the feed, and visualizes the emotion analysis results received from the backend.
- **Backend (Server):** A Python-based API built with FastAPI that receives video frames, performs emotion analysis using the `deepface` library, and sends the results back to the frontend.

## Technologies Used

- **Frontend:**
  - Java
  - JavaFX
  - OpenCV (for camera interaction)
  - OkHttp (for API communication)
  - Gson (for JSON parsing)

- **Backend:**
  - Python
  - FastAPI
  - `deepface`
  - `uvicorn`
  - `psutil`

## Getting Started

### Prerequisites

- Java JDK 11 or newer
- Python 3.8 or newer
- `pip` for Python package management

### Backend Setup

1. **Navigate to the backend directory:**
   ```bash
   cd backend
   ```

2. **Create and activate a virtual environment:**
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows, use `venv\Scripts\activate`
   ```

3. **Install the required Python packages:**
   ```bash
   pip install -r requirements.txt
   ```

4. **Run the backend server:**
   ```bash
   sh backend_start.sh
   ```
   The server will be running at `http://localhost:8000`.

### Frontend Setup

1. **Navigate to the frontend directory:**
   ```bash
   cd frontend
   ```

2. **Run the frontend application:**
   ```bash
   sh frontend_start.sh
   ```
   This will compile and run the JavaFX application.

## Usage

1. **Start the backend server** as described in the setup instructions.
2. **Launch the frontend application.**
3. **Click the "Start Camera" button** to begin the emotion detection.
4. The application will display the live video feed with bounding boxes around detected faces and their corresponding emotions.
5. The "Current Emotion" panel shows the dominant emotion of the most prominent face.
6. The "Emotion Statistics" table provides a summary of all detected emotions.
7. The "Performance Metrics" and "Energy Consumption" panels display real-time data on the resource usage of the AI model.
8. **Click the "Stop Camera" button** to end the session.
