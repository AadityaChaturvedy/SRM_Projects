# FPGA Performance Monitor Frontend

This directory contains the frontend files for the FPGA Performance Monitor dashboard. These files are served by the ESP32 web server to provide a real-time visualization of the CPU performance data received from the FPGA.

## Files

- **`index.html`**: The main HTML file that structures the web-based dashboard.
- **`dashboard_styles.css`**: The CSS file that styles the dashboard, providing a dark, modern theme.
- **`dashboard_script.js`**: The JavaScript file that contains the logic for fetching data from the ESP32, updating the UI, and rendering charts.

## Functionality

This frontend is functionally identical to the one used in the `CPU_Dashboard` project. It is designed to be a generic interface for displaying CPU performance metrics.

- It periodically polls the `/metrics` endpoint of the ESP32's web server.
- It parses the JSON response to get the latest CPI, misprediction rate, and other stats.
- It updates the stat cards and time-series charts (using Chart.js) to provide a real-time view of the data.
- It includes controls for managing the simulation, although in this project, the simulation is running on the FPGA and is not controlled by the dashboard.

## Usage

These files are intended to be hosted on the ESP32. When a user connects to the ESP32's Wi-Fi network and navigates to its IP address, these files are served to their browser to create the interactive dashboard experience.
