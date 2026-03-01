# 🏥 Medical Monitoring System

An integrated IoT solution for patient health tracking, combining hardware sensors with cloud persistence and a modern web interface.

## 🌟 Features
- **Vital Sensing**: Real-time tracking of heart rate, temperature, and movement.
- **Cloud Integration**: Automated data synchronization with Google Firebase.
- **Web Dashboard**: Interactive interface for healthcare professionals to monitor patients remotely.
- **Alert System**: Critical notifications triggered by anomalous health readings.

## 🏗️ Project Structure
- **`medicalProj.ino`**: Firmware for the ESP32/Arduino controller.
- **`index.html`, `style.css`, `script.js`**: Frontend dashboard implementation.

## 🚀 Setup
1.  **Firebase**: Configure your Firebase project and update credentials in `medicalProj.ino`.
2.  **Hardware**: Connect relevant sensors (DHT, Pulse) to the ESP32.
3.  **Frontend**: Open `index.html` in a browser or host via Firebase Hosting.

---
*Embedded Systems Engineering Portfolio Project.*
