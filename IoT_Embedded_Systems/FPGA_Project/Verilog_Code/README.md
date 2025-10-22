# Verilog Code for FPGA Performance Monitor

This directory contains the Verilog source files for implementing the CPU performance monitor on an FPGA.

## Files

- **`top.v`**: The top-level module for the FPGA design. It instantiates the `performance_monitor_esp` and provides it with a stream of test signals to simulate CPU activity. It also includes a simple LED blinker to confirm that the FPGA is running.

- **`performance_monitor_esp.v`**: This is the core logic of the performance monitor. It takes in signals that would typically come from a CPU (like `instr_valid`, `is_branch`, etc.) and uses them to count key performance events. It calculates metrics like CPI and branch misprediction rate and sends them out over a UART interface.

- **`uart_tx.v`**: A standard, reusable UART transmitter module. It takes 8 bits of data and transmits them serially according to the 8-N-1 protocol. The baud rate is configurable.

- **`tang_nano_20k.cst`**: This is the constraint file for the Tang Nano 20K FPGA board. It maps the top-level ports of the Verilog design (like the clock, reset, and UART pin) to the physical pins on the FPGA.

## Design Overview

The design is centered around the `performance_monitor_esp` module. This module performs the following key tasks:

1.  **Counting**: It uses a set of counters to track the total number of instructions, branches, and mispredictions.
2.  **Calculation**: At a regular interval (approximately once per second), it calculates the Cycles Per Instruction (CPI) and the branch misprediction rate. These values are kept in a 16.16 fixed-point format for precision.
3.  **Packetization**: The calculated metrics and raw counts are assembled into a 27-byte data packet.
4.  **Transmission**: The data packet is sent byte-by-byte using the `uart_tx` module to an external device (the ESP32).

## Synthesis and Implementation

To use this code, you will need an FPGA development environment that supports Verilog, such as the Gowin EDA for the Tang Nano board. The `top.v` module is the entry point for synthesis. The `tang_nano_20k.cst` file provides the necessary pin constraints for the Tang Nano 20K board.
