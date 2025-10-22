# Medico - Real-Time Health Dashboard

This project is a real-time health monitoring system that uses an ESP8266 to collect temperature and heart rate data and displays it on a web-based dashboard. The data is sent to a Supabase backend for storage, and the frontend fetches this data to provide a live view of the user's vitals.

## Project Components

- **`medicalProj.ino`**: The Arduino sketch for the ESP8266. It reads data from a DS18B20 temperature sensor and a pulse sensor, and then sends the data to a Supabase database.
- **`index.html`**: The main HTML file for the web dashboard.
- **`style.css`**: The stylesheet for the dashboard, which includes a light and dark theme.
- **`script.js`**: The JavaScript file that handles fetching data from Supabase and updating the dashboard UI in real-time.

## How It Works

1.  **Hardware (ESP8266)**: The `medicalProj.ino` firmware continuously reads the temperature and pulse sensors. It calculates the heart rate (BPM) and smooths the readings to get a stable value. Once a new reading is available, it sends the temperature and heart rate to the Supabase backend via an HTTP POST request.

2.  **Backend (Supabase)**: A Supabase project is used as a simple and effective backend. It has a table (named `medicos`) that stores the incoming temperature and heart rate data along with a timestamp.

3.  **Frontend (Web Dashboard)**: The `index.html`, `style.css`, and `script.js` files create a user-friendly dashboard. The JavaScript fetches the latest data from the Supabase API every 5 seconds. It then updates the UI to show the most recent temperature and heart rate, a historical chart of the heart rate, and a log of the last 20 readings.

## Features

- **Real-Time Monitoring**: The dashboard automatically updates with the latest sensor data.
- **Data Visualization**: A line chart displays the heart rate trend over time.
- **Data Logging**: A log shows the last 20 readings with timestamps.
- **Theming**: The dashboard supports both light and dark modes.
- **Alerts**: A simple browser alert is triggered if the heart rate falls outside a safe range.

## Usage

1.  **Hardware Setup**:
    - Connect the DS18B20 temperature sensor and the pulse sensor to your ESP8266 according to the pin definitions in `medicalProj.ino`.
    - Update the Wi-Fi credentials (`ssid` and `password`) in the `.ino` file to match your network.
    - Flash the `medicalProj.ino` sketch to your ESP8266.

2.  **Backend Setup**:
    - Create a new project in Supabase.
    - Create a table named `medicos` with columns for `temperature` (float), `heartRate` (integer), and `created_at` (timestamp).
    - Update the `supabaseUrl` and `supabaseApiKey` in both `medicalProj.ino` and `script.js` with your Supabase project's details.

3.  **Frontend Usage**:
    - Open the `index.html` file in a web browser. The dashboard will automatically start fetching and displaying data from the Supabase backend.
