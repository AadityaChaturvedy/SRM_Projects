/**
 * @file sketch_oct11a.ino
 * @description This firmware is designed for an ESP8266 and implements a stack-based calculator that mimics
 * the behavior of an 8086 processor. It communicates over a serial connection, accepting commands to manage a
 * stack and perform arithmetic operations. This sketch is functionally similar to the ESP32 version and is
 * intended to be controlled by a web-based interface using the Web Serial API.
 * @author [Your Name/Project Group]
 * @date 2025-10-22
 */

// --- PIN DEFINITIONS (for reference, not used in this serial-only implementation) ---
#define RX_PIN 3  // Corresponds to D9 on some ESP8266 boards
#define TX_PIN 1  // Corresponds to D10 on some ESP8266 boards

// --- CONSTANTS ---
#define STACK_SIZE 50 // Maximum number of elements in the stack

// --- GLOBAL VARIABLES ---
int stack[STACK_SIZE]; // Array to store stack elements
int stackPointer = -1;   // Initialize stack pointer to -1 (empty stack)

/**
 * @brief Pushes an integer value onto the top of the stack.
 * 
 * Checks for stack overflow and sends a confirmation or error message over serial.
 * 
 * @param value The integer to push onto the stack.
 */
void push(int value) {
  if (stackPointer < STACK_SIZE - 1) {
    stack[++stackPointer] = value;
    Serial.println("PUSH:" + String(value));
  } else {
    Serial.println("ERROR:Stack Overflow");
  }
}

/**
 * @brief Pops an integer value from the top of the stack.
 * 
 * Checks for stack underflow and sends an error message if the stack is empty.
 * 
 * @return The integer value popped from the stack. Returns 0 on underflow.
 */
int pop() {
  if (stackPointer >= 0) {
    int value = stack[stackPointer--];
    return value;
  } else {
    Serial.println("ERROR:Stack Underflow");
    return 0; // Return a default value on error
  }
}

/**
 * @brief Performs a calculation using the top two elements of the stack.
 * 
 * Pops two operands, performs the operation, and pushes the result back.
 * Handles errors like insufficient operands and division by zero.
 * 
 * @param operation The character representing the operation (+, -, *, /, %). 
 */
void calculate(char operation) {
  // Ensure there are at least two operands on the stack
  if (stackPointer < 1) {
    Serial.println("ERROR:Insufficient operands");
    return;
  }
  
  // Pop the top two operands
  int operand2 = pop();
  int operand1 = pop();
  int result = 0;
  
  switch(operation) {
    case '+':
      result = operand1 + operand2;
      break;
    case '-':
      result = operand1 - operand2;
      break;
    case '*':
      result = operand1 * operand2;
      break;
    case '/':
      if (operand2 == 0) {
        Serial.println("ERROR:Division by zero");
        // Restore stack state on error
        push(operand1);
        push(operand2);
        return;
      }
      result = operand1 / operand2;
      break;
    case '%':
      if (operand2 == 0) {
        Serial.println("ERROR:Modulo by zero");
        // Restore stack state on error
        push(operand1);
        push(operand2);
        return;
      }
      result = operand1 % operand2;
      break;
    default:
      Serial.println("ERROR:Invalid operation");
      // Restore stack state for unknown operations
      push(operand1);
      push(operand2);
      return;
  }
  
  // Push the final result back onto the stack
  push(result);
  Serial.println("RESULT:" + String(result));
}

/**
 * @brief Initializes the ESP8266, starts serial communication, and sends a ready signal.
 */
void setup() {
  // Begin serial communication at a baud rate of 115200
  Serial.begin(115200);
  // Wait for the serial port to connect
  while (!Serial) {
    ; 
  }
  // Notify the client that the device is ready
  Serial.println("READY:8086 Stack Calculator");
  Serial.println("STATUS:Initialized");
}

/**
 * @brief Main loop that continuously checks for and processes incoming serial commands.
 */
void loop() {
  // Proceed only if there is data in the serial buffer
  if (Serial.available() > 0) {
    // Read the incoming command as a string until a newline is received
    String command = Serial.readStringUntil('\n');
    command.trim(); // Clean up any whitespace
    
    // --- COMMAND PARSING LOGIC ---
    if (command.startsWith("PUSH:")) {
      int value = command.substring(5).toInt();
      push(value);
    } else if (command.startsWith("POP")) {
      if (stackPointer >= 0) {
        int value = pop();
        Serial.println("POP:" + String(value));
      } else {
        Serial.println("ERROR:Stack empty");
      }
    } else if (command.startsWith("CALC:")) {
      char operation = command.charAt(5);
      calculate(operation);
    } else if (command.startsWith("CLEAR")) {
      stackPointer = -1; // Reset stack
      Serial.println("STATUS:Stack cleared");
    } else if (command.startsWith("PEEK")) {
      if (stackPointer >= 0) {
        Serial.println("PEEK:" + String(stack[stackPointer]));
      } else {
        Serial.println("ERROR:Stack empty");
      }
    } else if (command.startsWith("SIZE")) {
      Serial.println("SIZE:" + String(stackPointer + 1));
    } else {
      Serial.println("ERROR:Unknown command");
    }
  }
  
  // A brief delay to yield to the ESP8266's other tasks
  delay(10);
}