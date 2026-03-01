# 📟 8086 Microprocessor Simulator

A high-fidelity, web-based emulator for the Intel 8086 microprocessor, designed for academic learning and assembly language debugging.

## 🌟 Features
- **Full Register Visualization**: Real-time tracking of AX, BX, CX, DX, SI, DI, BP, SP, and Flags.
- **Memory Map**: Interactive view of system memory and stack operations.
- **Instruction Support**: Emulates core 8086 instructions including data movement, arithmetic, and logic.
- **Step-by-Step Execution**: Debug assembly code with cycle-accurate precision.
- **Hardware Interfacing**: Includes simulated integration with ESP32 for external I/O.

## 🏗️ Project Structure
- **`index.html`, `style.css`, `script.js`**: Core web-based simulator logic.
- **[esp_32](./esp_32)**: Hardware interfacing logic for external control.
- **[sketch_oct11a](./sketch_oct11a)**: Arduino/ESP32 firmware for hardware-linked simulations.

## 🚀 Execution Guide
1.  **Web Simulator**: Open `index.html` in any modern web browser.
2.  **Hardware Link**: Flash the ESP32 with the code in `esp_32/` or `sketch_oct11a/` if physical I/O simulation is required.

---
*Computer Architecture & Simulations Portfolio Project.*
