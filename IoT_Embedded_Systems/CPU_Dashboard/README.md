# 📊 CPU Dashboard (ESP32)

A real-time system monitoring dashboard that captures hardware statistics and serves them over a web interface via an ESP32.

## 🌟 Features
- **Real-Time Data Streaming**: Dynamic updates for CPU usage, temperature, and memory.
- **Web-Based Visualization**: High-fidelity dashboard accessible from any browser on the local network.
- **Low-Latency Communication**: Optimized WebSocket/HTTP handling for minimal lag.

## 🏗️ Project Structure
- **[esp32_standalone_server](./esp32_standalone_server)**: Arduino-based firmware for the ESP32 server.
- **[data](./data)**: Frontend assets (HTML/CSS/JS) served by the ESP32 SPIFFS/LittleFS.

## 🚀 Setup
1.  **Hardware**: ESP32 DevKit V1.
2.  **Firmware**: Upload `esp32_standalone_server.ino` using the Arduino IDE.
3.  **Data**: Upload the contents of the `data/` folder to the ESP32 file system.

---
*Embedded Systems Engineering Portfolio Project.*
