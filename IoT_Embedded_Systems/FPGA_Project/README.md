# 🧩 FPGA Interfacing & Verilog Design

A low-level hardware project demonstrating the integration of FPGA logic with serial communication for external control and data acquisition.

## 🌟 Features
- **Hardware Acceleration**: Custom Verilog modules for high-performance logic operations.
- **Serial Interface**: Robust UART/Serial communication between FPGA and PC/ESP32.
- **Interactive UI**: Frontend dashboard for real-time hardware status monitoring.

## 🏗️ Project Structure
- **[Verilog_Code](./Verilog_Code)**: Core hardware description files for the FPGA (Tang Nano 9K).
- **[frontend](./frontend)**: Web-based visualization for hardware telemetry.
- **`Esp32_UART_Receive.cpp`**: C++ logic for interfacing between ESP32 and the FPGA.

## 🚀 Execution Guide
1.  **FPGA**: Synthesize and upload Verilog code using the Gowin EDA.
2.  **Interfacing**: Deploy C++ code to the ESP32 for serial bridging.
3.  **Frontend**: Launch the web interface to view hardware state changes.

---
*Hardware Design & Embedded Systems Portfolio.*
