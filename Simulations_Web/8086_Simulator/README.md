# 8086 Stack Calculator

This project implements a web-based stack calculator that simulates the behavior of an 8086 microprocessor. The frontend is built with HTML, CSS, and JavaScript, and it communicates with an ESP32 or ESP8266 microcontroller running the calculator's logic. The communication is handled via the Web Serial API, allowing the browser to directly interact with the microcontroller over a USB serial connection.

## Main Components

- **`index.html`**: The main HTML file that structures the calculator's user interface.
- **`style.css`**: The stylesheet that provides the visual design for the calculator, including a retro-futuristic theme.
- **`script.js`**: The core JavaScript file that handles all frontend logic, including button inputs, display updates, and serial communication with the microcontroller.
- **`esp_32/`**: A directory containing the Arduino sketch for the ESP32 microcontroller.
- **`sketch_oct11a/`**: A directory containing a similar Arduino sketch for the ESP8266 microcontroller.

## How It Works

The system follows a client-server model where the web browser is the client and the microcontroller is the server.

1.  **Frontend**: The user interacts with the calculator interface in the browser. When a number is entered and the "PUSH" button is clicked, the number is sent to the microcontroller over the serial connection.
2.  **Backend (Microcontroller)**: The microcontroller receives commands (e.g., `PUSH:123`, `CALC:+`) and processes them. It maintains a stack in memory and performs arithmetic operations as instructed.
3.  **Communication**: The microcontroller sends responses back to the browser to confirm actions (e.g., `PUSH:123`), report errors (e.g., `ERROR:Stack Overflow`), or provide calculation results (e.g., `RESULT:456`).

## Usage Instructions

1.  **Hardware Setup**:
    - Flash either the `esp_32.ino` or `sketch_oct11a.ino` sketch to your ESP32 or ESP8266 board using the Arduino IDE.
    - Connect the microcontroller to your computer via USB.

2.  **Software Setup**:
    - Open the `index.html` file in a modern web browser that supports the Web Serial API (e.g., Google Chrome, Microsoft Edge).
    - Click the "Connect to ESP" button and select the appropriate serial port from the list.

3.  **Performing Calculations**:
    - Enter a number using the keypad.
    - Press **PUSH** to add the number to the stack.
    - Enter a second number and press **PUSH** again.
    - Select an operation (+, -, *, /, MOD).
    - Press **=** to perform the calculation. The result will be displayed and pushed onto the stack.

## File Explanations

- **`index.html`**: Defines the structure of the calculator, including the display, keypad, stack view, and connection panel.
- **`style.css`**: Styles the application with a retro theme, using custom fonts and a color scheme inspired by old-school electronics.
- **`script.js`**: 
    - Manages the connection to the serial device using the Web Serial API.
    - Handles all user interactions, such as button clicks and keyboard input.
    - Sends commands to and processes data from the microcontroller.
    - Updates the UI dynamically to reflect the state of the stack and the calculator.
- **`esp_32/esp_32.ino`**: The firmware for the ESP32. It implements the core stack machine logic, including functions for `push`, `pop`, and `calculate`.
- **`sketch_oct11a/sketch_oct11a.ino`**: A similar firmware version tailored for the ESP8266.
