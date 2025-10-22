# ESP8266 Firmware for 8086 Stack Calculator

This directory contains the Arduino sketch (`sketch_oct11a.ino`) for the ESP8266 microcontroller, which serves as an alternative backend for the 8086 Stack Calculator project.

## Purpose

This firmware provides the same functionality as the ESP32 version but is tailored for the ESP8266. It implements a simple stack-based calculator that communicates over a serial connection, receiving commands from a web-based frontend to perform stack and arithmetic operations.

## Main Components

- **`sketch_oct11a.ino`**: The single source file for the ESP8266 firmware.

## Functionality

The firmware's functionality is identical to the ESP32 version:

- **Stack Operations**: `push(value)` and `pop()`.
- **Arithmetic Calculations**: `calculate(op)` for addition, subtraction, multiplication, division, and modulo.
- **Serial Command Parsing**: The main loop processes commands like `PUSH`, `POP`, `CALC`, `CLEAR`, `PEEK`, and `SIZE`.

## Dependencies

- **Arduino IDE**: To compile and upload the sketch.
- **ESP8266 Board Support**: The ESP8266 board package for the Arduino IDE.

## How to Use

1.  Open the `sketch_oct11a.ino` file in the Arduino IDE.
2.  Select your ESP8266 board (e.g., "NodeMCU 1.0") from the **Tools > Board** menu.
3.  Select the correct COM port from the **Tools > Port** menu.
4.  Click the **Upload** button to flash the firmware to the ESP8266.
