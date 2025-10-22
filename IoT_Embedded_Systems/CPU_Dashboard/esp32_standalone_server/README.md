# ESP32 Standalone Server Firmware

This directory contains the Arduino sketch (`esp32_standalone_server.ino`) that runs on the ESP32. This firmware is the backend for the CPU Dashboard project.

## Purpose

The primary purpose of this firmware is to:

1.  **Create a Wi-Fi Access Point**: It establishes a local Wi-Fi network that allows other devices to connect to it directly.
2.  **Run a Web Server**: It hosts a simple web server to handle HTTP requests from the connected clients.
3.  **Simulate CPU Performance**: It runs a continuous simulation of a single-cycle and a 5-stage pipelined CPU, generating performance metrics like CPI and branch misprediction rates.
4.  **Provide a Data API**: It offers a JSON endpoint (`/metrics`) where the frontend can fetch the latest simulation data.
5.  **Accept Control Commands**: It has a command endpoint (`/cmd`) to allow the frontend to control the simulation (e.g., start, stop, reset).

## Main Components

- **`esp32_standalone_server.ino`**: The complete source code for the firmware.

## Functionality Details

- **Wi-Fi**: The ESP32 is configured as a software-based Access Point (AP) with the SSID `FPGA_MONITOR` and a pre-defined password.
- **Web Server**: The server listens for incoming connections on port 80. It has three main routes:
    - `/`: A simple root page.
    - `/metrics`: Responds with a JSON object containing the latest performance data for both simulated CPUs and their historical data.
    - `/cmd`: Accepts a `set` parameter to control the simulation (e.g., `/cmd?set=start`).
- **Simulation**: The simulation runs in the main loop and updates based on a selected scenario. The scenarios adjust parameters like branch frequency and misprediction chance to model different workloads.

## Dependencies

- **Arduino IDE**
- **ESP32 Board Support** for the Arduino IDE
- **`WiFi.h`**: For Wi-Fi functionality.
- **`WebServer.h`**: For creating the web server.
- **`ArduinoJson.h`**: For creating and parsing JSON data.
