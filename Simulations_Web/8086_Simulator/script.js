/**
 * @file script.js
 * @description This script handles the frontend logic for the 8086 Stack Calculator, including DOM manipulation,
 * event handling, and serial communication with an ESP8266/ESP32 microcontroller. It uses the Web Serial API
 * to connect to the device, send commands, and receive data, simulating the behavior of an 8086-based stack machine.
 * @author [Your Name/Project Group]
 * @date 2025-10-22
 */

// 8086 Stack Calculator - ESP8266 Serial Communication

// --- GLOBAL VARIABLES --- //
let port;
let reader;
let writer;
let isConnected = false;
let currentDisplay = '0'; // Stores the current number being entered
let currentOperation = null; // Stores the selected arithmetic operation
let stack = []; // A local mirror of the stack on the microcontroller

// --- DOM ELEMENT REFERENCES --- //
const display = document.getElementById('display');
const statusDot = document.getElementById('statusDot');
const statusText = document.getElementById('statusText');
const stackSize = document.getElementById('stackSize');
const stackContent = document.getElementById('stackContent');
const connectBtn = document.getElementById('connectBtn');
const pushBtn = document.getElementById('pushBtn');
const popBtn = document.getElementById('popBtn');
const peekBtn = document.getElementById('peekBtn');
const clearBtn = document.getElementById('clearBtn');
const equalsBtn = document.getElementById('equalsBtn');
const clearStackBtn = document.getElementById('clearStackBtn');

/**
 * Initializes all event listeners for the calculator buttons and keyboard input.
 */
function initEventListeners() {
    // --- Number Pad Event Listeners ---
    document.querySelectorAll('.number-key').forEach(button => {
        button.addEventListener('click', () => handleNumber(button.dataset.value));
    });

    // --- Operation Key Event Listeners ---
    document.querySelectorAll('.operation-key').forEach(button => {
        button.addEventListener('click', () => handleOperation(button.dataset.operation));
    });

    // --- Control Key Event Listeners ---
    connectBtn.addEventListener('click', handleConnect);
    pushBtn.addEventListener('click', handlePush);
    popBtn.addEventListener('click', handlePop);
    peekBtn.addEventListener('click', handlePeek);
    clearBtn.addEventListener('click', handleClear);
    equalsBtn.addEventListener('click', handleEquals);
    clearStackBtn.addEventListener('click', handleClearStack);

    // --- Keyboard Input Support ---
    document.addEventListener('keydown', handleKeyboard);
}

/**
 * Handles numeric input from the keypad.
 * @param {string} value - The number key pressed (e.g., '7', '0').
 */
function handleNumber(value) {
    animateButton(event.target);
    if (currentDisplay === '0') {
        currentDisplay = value;
    } else {
        currentDisplay += value;
    }
    updateDisplay(currentDisplay);
}

/**
 * Sets the current arithmetic operation.
 * @param {string} operation - The operation to perform (e.g., '+', '-', '*', '/').
 */
function handleOperation(operation) {
    animateButton(event.target);
    currentOperation = operation;
    showNotification(`Operation set: ${operation}`, 'info');
}

/**
 * Pushes the current display value onto the stack.
 * Sends a "PUSH" command to the microcontroller.
 */
async function handlePush() {
    animateButton(pushBtn);
    if (!isConnected) {
        showNotification('Error: Not connected to ESP device.', 'error');
        return;
    }

    const value = parseInt(currentDisplay);
    if (isNaN(value)) {
        showNotification('Error: Invalid number.', 'error');
        return;
    }

    try {
        // Send command to the device
        await sendCommand(`PUSH:${value}\n`);
        // Update local stack for immediate UI feedback
        stack.push(value);
        updateStackDisplay();
        // Reset display for next input
        currentDisplay = '0';
        updateDisplay(currentDisplay);
        showNotification(`Pushed ${value} to stack`, 'success');
    } catch (error) {
        showNotification('Error: Failed to push value.', 'error');
    }
}

/**
 * Pops the top value from the stack.
 * Sends a "POP" command to the microcontroller.
 */
async function handlePop() {
    animateButton(popBtn);
    if (!isConnected) {
        showNotification('Error: Not connected to ESP device.', 'error');
        return;
    }

    if (stack.length === 0) {
        showNotification('Warning: Stack is empty.', 'error');
        return;
    }

    try {
        await sendCommand('POP\n');
        // Update local stack and display
        const value = stack.pop();
        updateStackDisplay();
        currentDisplay = value.toString();
        updateDisplay(currentDisplay);
        showNotification(`Popped ${value} from stack`, 'success');
    } catch (error) {
        showNotification('Error: Failed to pop value.', 'error');
    }
}

/**
 * Peeks at the top value of the stack without removing it.
 * Sends a "PEEK" command to the microcontroller.
 */
async function handlePeek() {
    animateButton(peekBtn);
    if (!isConnected) {
        showNotification('Error: Not connected to ESP device.', 'error');
        return;
    }

    if (stack.length === 0) {
        showNotification('Warning: Stack is empty.', 'error');
        return;
    }

    try {
        await sendCommand('PEEK\n');
        // Display the top value
        const value = stack[stack.length - 1];
        currentDisplay = value.toString();
        updateDisplay(currentDisplay);
        showNotification(`Top of stack: ${value}`, 'info');
    } catch (error) {
        showNotification('Error: Failed to peek at stack.', 'error');
    }
}

/**
 * Clears the main display and resets the current operation.
 */
function handleClear() {
    animateButton(clearBtn);
    currentDisplay = '0';
    currentOperation = null;
    updateDisplay(currentDisplay);
    showNotification('Display cleared', 'info');
}

/**
 * Clears the entire stack on both the frontend and the microcontroller.
 * Sends a "CLEAR" command.
 */
async function handleClearStack() {
    animateButton(clearStackBtn);
    if (!isConnected) {
        showNotification('Error: Not connected to ESP device.', 'error');
        return;
    }

    try {
        await sendCommand('CLEAR\n');
        // Clear local stack and update UI
        stack = [];
        updateStackDisplay();
        showNotification('Stack cleared', 'success');
    } catch (error) {
        showNotification('Error: Failed to clear stack.', 'error');
    }
}

/**
 * Executes the selected calculation on the microcontroller.
 * Sends a "CALC" command with the current operation.
 */
async function handleEquals() {
    animateButton(equalsBtn);
    if (!isConnected) {
        showNotification('Error: Not connected to ESP device.', 'error');
        return;
    }

    if (!currentOperation) {
        showNotification('Error: No operation selected.', 'error');
        return;
    }

    if (stack.length < 2) {
        showNotification('Error: At least two numbers are required for calculation.', 'error');
        return;
    }

    try {
        // Send calculation command
        await sendCommand(`CALC:${currentOperation}\n`);
        // A short delay to allow the device to process and respond
        await new Promise(resolve => setTimeout(resolve, 200));
        showNotification('Calculation initiated!', 'success');
    } catch (error) {
        showNotification('Error: Calculation failed.', 'error');
    }
}

/**
 * Handles the connection and disconnection to the serial device.
 * Uses the Web Serial API.
 */
async function handleConnect() {
    // If already connected, disconnect
    if (isConnected) {
        await disconnect();
        return;
    }

    // Check for browser compatibility
    if (!('serial' in navigator)) {
        showNotification('Web Serial API not supported in this browser!', 'error');
        alert('Please use a modern browser like Chrome, Edge, or Opera for Web Serial API support.');
        return;
    }

    try {
        // Request user to select a serial port
        port = await navigator.serial.requestPort();
        await port.open({ baudRate: 115200 });

        // Set up text decoder and encoder streams
        const textDecoder = new TextDecoderStream();
        port.readable.pipeTo(textDecoder.writable);
        reader = textDecoder.readable.getReader();

        const textEncoder = new TextEncoderStream();
        textEncoder.readable.pipeTo(port.writable);
        writer = textEncoder.writable.getWriter();

        isConnected = true;
        updateConnectionStatus(true);
        showNotification('Successfully connected to ESP device!', 'success');

        // Begin listening for incoming serial data
        readFromSerial();

    } catch (error) {
        showNotification(`Connection failed: ${error.message}`, 'error');
        console.error('Connection error:', error);
    }
}

/**
 * Disconnects from the serial port and cleans up resources.
 */
async function disconnect() {
    try {
        if (reader) {
            await reader.cancel();
        }
        if (writer) {
            await writer.close();
        }
        if (port) {
            await port.close();
        }
        isConnected = false;
        updateConnectionStatus(false);
        showNotification('Disconnected from ESP device', 'info');
    } catch (error) {
        showNotification(`Disconnect error: ${error.message}`, 'error');
    }
}

/**
 * Continuously reads data from the serial port and processes it.
 */
async function readFromSerial() {
    try {
        while (isConnected) {
            const { value, done } = await reader.read();
            if (done) break;

            // Process each line of incoming data
            const lines = value.split('\n');
            for (let line of lines) {
                line = line.trim();
                if (line) {
                    processSerialData(line);
                }
            }
        }
    } catch (error) {
        console.error('Serial read error:', error);
        if (isConnected) {
            showNotification('Communication error occurred.', 'error');
            await disconnect();
        }
    }
}

/**
 * Parses incoming data from the microcontroller and updates the UI accordingly.
 * @param {string} data - A single line of data from the serial port.
 */
function processSerialData(data) {
    console.log('Received:', data);

    if (data.startsWith('READY:')) {
        showNotification('ESP device is ready!', 'success');
    } else if (data.startsWith('RESULT:')) {
        const result = data.split(':')[1];
        currentDisplay = result;
        updateDisplay(currentDisplay);
        // Update local stack to reflect the calculation (pop two operands, push result)
        stack.pop();
        stack.pop();
        stack.push(parseInt(result));
        updateStackDisplay();
    } else if (data.startsWith('ERROR:')) {
        const errorMsg = data.split(':')[1];
        showNotification(`Device Error: ${errorMsg}`, 'error');
    } else if (data.startsWith('STATUS:')) {
        const status = data.split(':')[1];
        showNotification(`Status: ${status}`, 'info');
    } else if (data.startsWith('SIZE:')) {
        const size = data.split(':')[1];
        stackSize.textContent = size;
    }
}

/**
 * Sends a command string to the connected serial device.
 * @param {string} command - The command to send (e.g., "PUSH:123\n").
 */
async function sendCommand(command) {
    if (!isConnected || !writer) {
        throw new Error('Device not connected.');
    }
    try {
        await writer.write(command);
        console.log('Sent:', command.trim());
    } catch (error) {
        console.error('Serial write error:', error);
        throw error;
    }
}

/**
 * Updates the calculator's main display with a new value.
 * @param {string} value - The value to display.
 */
function updateDisplay(value) {
    display.value = value;
    // Trigger a subtle glow animation for visual feedback
    display.style.animation = 'none';
    setTimeout(() => {
        display.style.animation = 'displayGlow 0.3s ease-out';
    }, 10);
}

/**
 * Renders the current state of the local stack in the UI.
 */
function updateStackDisplay() {
    stackSize.textContent = stack.length;
    
    if (stack.length === 0) {
        stackContent.innerHTML = '<div class="empty-stack">Stack Empty</div>';
    } else {
        stackContent.innerHTML = '';
        // Display stack with the top element shown first
        for (let i = stack.length - 1; i >= 0; i--) {
            const item = document.createElement('div');
            item.className = 'stack-item';
            item.innerHTML = "\
                <span class=\"stack-item-index\">[${i}]</span>\
                <span class=\"stack-item-value\">${stack[i]}</span>
            ";
            stackContent.appendChild(item);
        }
    }
}

/**
 * Updates the UI elements to reflect the current connection status.
 * @param {boolean} connected - Whether the device is connected.
 */
function updateConnectionStatus(connected) {
    if (connected) {
        statusDot.classList.add('connected');
        statusText.textContent = 'Connected';
        connectBtn.querySelector('.btn-text').textContent = 'Disconnect';
        connectBtn.style.background = 'linear-gradient(135deg, #ff3366, #ff6633)';
    } else {
        statusDot.classList.remove('connected');
        statusText.textContent = 'Disconnected';
        connectBtn.querySelector('.btn-text').textContent = 'Connect to ESP8266';
        connectBtn.style.background = 'linear-gradient(135deg, var(--primary-color), var(--secondary-color))';
    }
}

/**
 * Displays a temporary notification message on the screen.
 * @param {string} message - The message to display.
 * @param {'info' | 'success' | 'error'} type - The type of notification.
 */
function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    // Apply dynamic styles for the notification
    notification.style.cssText = "position: fixed; top: 20px; right: 20px; background: ${type === 'success' ? 'rgba(0, 255, 136, 0.9)' : 
                     type === 'error' ? 'rgba(255, 51, 102, 0.9)' : 
                     'rgba(0, 255, 255, 0.9)'}; color: #000; padding: 15px 25px; border-radius: 10px; font-family: 'Rajdhani', sans-serif; font-weight: 600; font-size: 14px; z-index: 10000; animation: slideInRight 0.3s ease-out, slideOutRight 0.3s ease-in 2.7s; box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
    ";
    document.body.appendChild(notification);
    // Automatically remove the notification after 3 seconds
    setTimeout(() => notification.remove(), 3000);
}

/**
 * Provides a simple animation for button presses.
 * @param {HTMLElement} button - The button element to animate.
 */
function animateButton(button) {
    button.style.transform = 'scale(0.95)';
    setTimeout(() => {
        button.style.transform = '';
    }, 100);
}

/**
 * Handles keyboard inputs for calculator functionality.
 * @param {KeyboardEvent} event - The keyboard event.
 */
function handleKeyboard(event) {
    if (event.key >= '0' && event.key <= '9') {
        handleNumber(event.key);
    } else if (['+', '-', '*', '/'].includes(event.key)) {
        handleOperation(event.key);
    } else if (event.key === 'Enter' || event.key === '=') {
        handleEquals();
    } else if (event.key === 'Escape' || event.key.toLowerCase() === 'c') {
        handleClear();
    }
}

/**
 * Injects CSS keyframe animations into the document head.
 */
function addCssAnimations() {
    const style = document.createElement('style');
    style.textContent = "@keyframes displayGlow { 0% { box-shadow: inset 0 0 30px rgba(0, 255, 255, 0.2); } 50% { box-shadow: inset 0 0 50px rgba(0, 255, 255, 0.5); } 100% { box-shadow: inset 0 0 30px rgba(0, 255, 255, 0.2); } } @keyframes slideInRight { from { opacity: 0; transform: translateX(100px); } to { opacity: 1; transform: translateX(0); } } @keyframes slideOutRight { from { opacity: 1; transform: translateX(0); } to { opacity: 0; transform: translateX(100px); } }";
    document.head.appendChild(style);
}

// --- APPLICATION INITIALIZATION ---
document.addEventListener('DOMContentLoaded', () => {
    initEventListeners();
    addCssAnimations();
    updateDisplay('0');
    updateStackDisplay();
    showNotification('8086 Calculator Initialized', 'info');
});

// --- PAGE UNLOAD HANDLER ---
window.addEventListener('beforeunload', async () => {
    // Ensure the serial connection is gracefully closed if the user navigates away
    if (isConnected) {
        await disconnect();
    }
});