/**
 * @file esp_32.ino
 * @description This firmware runs on an ESP32 and implements a stack-based calculator that mimics
 * the behavior of an 8086 processor. It communicates over a serial connection, accepting commands
 * to push values onto a stack, perform arithmetic operations (ADD, SUB, MUL, DIV, MOD), and manage
 * the stack (POP, PEEK, CLEAR). The firmware is designed to be controlled by a web-based interface
 * that uses the Web Serial API.
 * @author [Your Name/Project Group]
 * @date 2025-10-22
 */

// --- CONSTANTS ---
#define STACK_SIZE 50 // Maximum number of elements in the stack

// --- GLOBAL VARIABLES ---
int stack[STACK_SIZE]; // Array to store stack elements
int stackPointer = -1;   // Initialize stack pointer to -1 (empty stack)

/**
 * @brief Pushes an integer value onto the top of the stack.
 * 
 * Checks for stack overflow before adding the element.
 * Sends a confirmation or error message over serial.
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
 * Checks for stack underflow before removing the element.
 * Sends an error message if the stack is empty.
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
 * Pops two operands, performs the specified operation, and pushes the result back.
 * Handles errors like insufficient operands and division by zero.
 * 
 * @param op The character representing the operation (+, -, *, /, %). 
 */
void calculate(char op) {
  // Check if there are enough operands for a calculation
  if (stackPointer < 1) {
    Serial.println("ERROR:Insufficient operands");
    return;
  }
  
  // Pop the top two operands from the stack
  int b = pop();
  int a = pop();
  int result = 0;

  switch (op) {
    case '+': 
      result = a + b; 
      break;
    case '-': 
      result = a - b; 
      break;
    case '*': 
      result = a * b; 
      break;
    case '/':
      if (b == 0) {
        Serial.println("ERROR:Division by zero");
        // Push operands back onto the stack to preserve state
        push(a); 
        push(b);
        return;
      }
      result = a / b;
      break;
    case '%':
      if (b == 0) {
        Serial.println("ERROR:Modulo by zero");
        // Push operands back onto the stack
        push(a); 
        push(b);
        return;
      }
      result = a % b;
      break;
    default:
      Serial.println("ERROR:Invalid operation");
      // Push operands back if the operation is unknown
      push(a); 
      push(b);
      return;
  }

  // Push the result of the calculation back onto the stack
  push(result);
  Serial.println("RESULT:" + String(result));
}

/**
 * @brief Initializes the ESP32, starts serial communication, and sends a ready signal.
 */
void setup() {
  // Start serial communication at 115200 baud rate
  Serial.begin(115200);
  // Wait for the serial port to be ready
  while (!Serial) { 
    delay(10); 
  }
  // Send ready message to the client
  Serial.println("READY:8086 Stack Calculator");
  Serial.println("STATUS:Initialized");
}

/**
 * @brief Main loop that continuously checks for and processes incoming serial commands.
 */
void loop() {
  // Check if there is data available to read from the serial port
  if (Serial.available()) {
    // Read the incoming command until a newline character is received
    String command = Serial.readStringUntil('\n');
    command.trim(); // Remove any leading/trailing whitespace

    // --- COMMAND PARSER ---
    if (command.startsWith("PUSH:")) {
      // Extract the integer value and push it to the stack
      int val = command.substring(5).toInt();
      push(val);
    } else if (command == "POP") {
      // Pop a value and send it back to the client
      int val = pop();
      if (stackPointer >= -1) { // Check if pop was successful
        Serial.println("POP:" + String(val));
      }
    } else if (command.startsWith("CALC:")) {
      // Extract the operation and perform the calculation
      char op = command.charAt(5);
      calculate(op);
    } else if (command == "CLEAR") {
      // Reset the stack pointer to clear the stack
      stackPointer = -1;
      Serial.println("STATUS:Stack cleared");
    } else if (command == "PEEK") {
      // View the top element without removing it
      if (stackPointer >= 0) {
        Serial.println("PEEK:" + String(stack[stackPointer]));
      } else {
        Serial.println("ERROR:Stack empty");
      }
    } else if (command == "SIZE") {
      // Return the current size of the stack
      Serial.println("SIZE:" + String(stackPointer + 1));
    } else {
      // Handle any unrecognized commands
      Serial.println("ERROR:Unknown command");
    }
  }
  // Small delay to prevent the loop from running too fast
  delay(10);
}