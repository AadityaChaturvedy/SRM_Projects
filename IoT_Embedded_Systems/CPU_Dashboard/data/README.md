# CPU Dashboard Frontend

This directory contains all the frontend files for the 5-Stage Pipelined CPU Dashboard. These files are served to the user when they connect to the ESP32's web server.

## Files

- **`index.html`**: The main entry point and structure of the web application. It defines all the containers for the dashboard's elements, such as the header, controls, stats, and charts.

- **`dashboard_styles.css`**: This file contains all the CSS rules for styling the dashboard. It is responsible for the visual appearance, including the dark theme, layout, and responsive design.

- **`dashboard_script.js`**: This is the core of the frontend application. Its responsibilities include:
    - **Data Fetching**: Periodically polling the ESP32's `/metrics` endpoint to get the latest simulation data.
    - **UI Updates**: Updating the values in the stat cards, and refreshing the charts with new data points.
    - **Interactivity**: Handling user input from the control buttons (start, pause, reset), the speed slider, and the scenario selectors.
    - **Command Sending**: Sending commands to the ESP32's `/cmd` endpoint to control the simulation.
    - **Charting**: Using the Chart.js library to create and update the time-series charts for CPI and misprediction rate.

## How It Works

When a user navigates to the ESP32's IP address, the `index.html` file is loaded. This file, in turn, loads the `dashboard_styles.css` for styling and the `dashboard_script.js` for functionality. The JavaScript then takes over, creating a dynamic and interactive experience by communicating with the ESP32 backend.
