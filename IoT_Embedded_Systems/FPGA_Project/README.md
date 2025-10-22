# FPGA CPU Performance Monitor

This project implements a hardware-based CPU performance monitor on an FPGA. The monitor collects key metrics from a simulated CPU, and an ESP32 microcontroller reads this data via UART. The ESP32 then serves the data to a web-based dashboard, allowing for real-time visualization of the CPU's performance.

## Project Structure

- **`Verilog_Code/`**: Contains the Verilog source files for the FPGA design.
    - `performance_monitor_esp.v`: The core module that monitors CPU signals and sends data via UART.
    - `uart_tx.v`: A generic UART transmitter module.
    - `top.v`: The top-level module that instantiates the performance monitor and provides it with test signals.
    - `tang_nano_20k.cst`: The pin constraint file for the Tang Nano 20K FPGA board.
- **`Esp32_UART_Receive.cpp`**: The C++ firmware for the ESP32. It reads the UART data from the FPGA, parses it, and serves it via a Wi-Fi AP.
- **`frontend/`**: Contains the web dashboard files (HTML, CSS, JS) for visualizing the data. This is functionally identical to the dashboard in the `CPU_Dashboard` project.
- **`Implementation_Pictures/`**: Screenshots of the project in action.
- **`Tang_Project_Docs.pdf`**: Documentation for the Tang Nano project.

## How It Works

1.  **FPGA Side**: The `performance_monitor_esp` module on the FPGA continuously monitors simulated CPU signals (`instr_valid`, `is_branch`, etc.). It calculates metrics like CPI and branch misprediction rate. Periodically, it serializes this data into a packet and sends it to the ESP32 using the `uart_tx` module.
2.  **ESP32 Side**: The `Esp32_UART_Receive.cpp` firmware listens for incoming UART data from the FPGA. It parses the received packets to extract the performance metrics. The ESP32 also creates a Wi-Fi Access Point and runs a web server.
3.  **Frontend Side**: A user connects to the ESP32's Wi-Fi network and navigates to its IP address. The web dashboard, served by the ESP32, then fetches the performance data from a JSON API (`/metrics`) and displays it in real-time charts and graphs.

## File Explanations

- **`Verilog_Code/performance_monitor_esp.v`**: This is the heart of the FPGA design. It includes counters for instructions, branches, and mispredictions, and calculates CPI and misprediction rates. It uses a state machine to packetize and send this data.
- **`Verilog_Code/top.v`**: This module integrates the performance monitor and generates a stream of test data to simulate a CPU, allowing the monitor to be tested standalone.
- **`Esp32_UART_Receive.cpp`**: This firmware is responsible for receiving the UART data from the FPGA. It includes logic to parse the specific packet format sent by the Verilog module, convert fixed-point numbers to floats, and serve the data as JSON.
- **`frontend/`**: The web dashboard provides a user-friendly interface for visualizing the performance data. It is built with standard HTML, CSS, and JavaScript (including the Chart.js library).

## Usage

1.  **Program the FPGA**: Synthesize the Verilog code and flash it to the Tang Nano 20K board.
2.  **Program the ESP32**: Flash the `Esp32_UART_Receive.cpp` firmware to an ESP32.
3.  **Connect Hardware**: Connect the UART TX pin of the FPGA to the RX pin of the ESP32.
4.  **View Dashboard**: Connect a device to the `FPGA_MONITOR` Wi-Fi network and open a browser to the ESP32's IP address (typically `192.168.4.1`).
