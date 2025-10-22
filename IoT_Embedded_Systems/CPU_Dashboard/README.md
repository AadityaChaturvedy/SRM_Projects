# 5-Stage Pipelined CPU Dashboard

This project provides a web-based dashboard to visualize and compare the performance of a simulated 5-stage pipelined CPU against a single-cycle CPU. The backend is an ESP32 microcontroller that runs the simulation and serves the data via a Wi-Fi Access Point.

## Main Components

- **`data/`**: This directory contains the frontend files for the web dashboard.
    - **`index.html`**: The main HTML file for the dashboard UI.
    - **`dashboard_styles.css`**: The stylesheet for the dashboard.
    - **`dashboard_script.js`**: The JavaScript file that handles data fetching, UI updates, and user interactions.
- **`esp32_standalone_server/`**: This directory contains the Arduino sketch for the ESP32.
    - **`esp32_standalone_server.ino`**: The firmware that creates a Wi-Fi AP, runs the CPU simulations, and serves the data via a web server.

## How It Works

1.  **ESP32 Backend**: The ESP32 creates a Wi-Fi network with the SSID `FPGA_MONITOR`. It runs a web server that exposes two main endpoints:
    - `/metrics`: A JSON API that provides real-time performance data for both the pipelined and single-cycle CPU simulations.
    - `/cmd`: An endpoint that accepts commands to control the simulation (start, pause, reset, change speed, change scenario).
2.  **Web Dashboard Frontend**: The user connects their device (laptop, phone) to the ESP32's Wi-Fi network and navigates to the ESP32's IP address (usually `192.168.4.1`) in a web browser. The JavaScript on the dashboard then periodically fetches data from the `/metrics` endpoint and updates the charts and statistics.

## Usage Instructions

1.  **Hardware Setup**:
    - Flash the `esp32_standalone_server.ino` sketch to your ESP32 board using the Arduino IDE.
2.  **Connect to the ESP32**:
    - On your computer or mobile device, connect to the Wi-Fi network named `FPGA_MONITOR` (password: `12345678`).
    - Open a web browser and navigate to `http://192.168.4.1`.
3.  **Interact with the Dashboard**:
    - Use the controls to start, pause, and reset the simulation.
    - Adjust the simulation speed with the slider.
    - Select different simulation scenarios to see how they affect CPU performance.
    - Toggle between the pipelined and single-cycle CPU views to compare their metrics.

## File Explanations

- **`data/index.html`**: Defines the structure of the dashboard, including sections for controls, statistics, charts, and a comparison table.
- **`data/dashboard_styles.css`**: Provides the styling for the dashboard, creating a modern, dark-themed interface.
- **`data/dashboard_script.js`**: 
    - Manages the periodic polling of the `/metrics` endpoint.
    - Updates the UI with the fetched data, including stat cards and charts (using Chart.js).
    - Sends commands to the `/cmd` endpoint based on user interactions.
- **`esp32_standalone_server/esp32_standalone_server.ino`**:
    - Sets up a Wi-Fi Access Point.
    - Implements a web server with endpoints for data and commands.
    - Runs the CPU simulation logic, generating performance data based on different scenarios.
    - Formats the simulation data into a JSON response.
