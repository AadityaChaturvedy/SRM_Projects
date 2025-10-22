# ESP32 Firmware for 8086 Stack Calculator

This directory contains the Arduino sketch (`esp_32.ino`) for the ESP32 microcontroller, which serves as the backend for the 8086 Stack Calculator project.

## Purpose

The firmware implements a simple stack-based calculator that communicates over a serial connection. It is designed to receive commands from a web-based frontend, process them, and send back results or status updates.

## Main Components

- **`esp_32.ino`**: The single source file for the ESP32 firmware.

## Functionality

The firmware provides the following functionalities:

- **Stack Operations**: 
    - `push(value)`: Adds a value to the stack.
    - `pop()`: Removes and returns a value from the stack.
- **Arithmetic Calculations**: 
    - `calculate(op)`: Performs addition, subtraction, multiplication, division, and modulo operations on the top two stack elements.
- **Serial Command Parsing**: The main loop continuously listens for incoming serial commands and processes them. Supported commands include:
    - `PUSH:<value>`: Pushes an integer onto the stack.
    - `POP`: Pops a value from the stack.
    - `CALC:<op>`: Performs a calculation.
    - `CLEAR`: Clears the stack.
    - `PEEK`: Returns the top value of the stack without removing it.
    - `SIZE`: Returns the current number of elements in the stack.

## Dependencies

- **Arduino IDE**: To compile and upload the sketch.
- **ESP32 Board Support**: The ESP32 board package for the Arduino IDE.

## How to Use

1.  Open the `esp_32.ino` file in the Arduino IDE.
2.  Select your ESP32 board from the **Tools > Board** menu.
3.  Select the correct COM port from the **Tools > Port** menu.
4.  Click the **Upload** button to flash the firmware to the ESP32.
