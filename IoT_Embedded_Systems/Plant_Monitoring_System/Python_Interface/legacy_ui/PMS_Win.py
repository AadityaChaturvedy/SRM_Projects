import sys
import time
import serial
import serial.tools.list_ports
from collections import deque
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QVBoxLayout, QHBoxLayout
from PyQt5.QtCore import pyqtSignal, QThread, QTimer
from matplotlib.figure import Figure
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas


import serial
import serial.tools.list_ports

def find_serial_port(baudrate=9600, timeout=3):
    """
    Automatically detects the correct serial port on Windows (COMx).
    It sends a 'ping' command to test each port and returns the first valid one.

    Args:
        baudrate (int): The baud rate for serial communication. Default is 9600.
        timeout (int): The timeout in seconds for serial communication. Default is 1.

    Returns:
        str: The device name of the valid serial port (e.g., 'COM3') or None if no valid port is found.
    """
    # List all the available ports
    ports = serial.tools.list_ports.comports()

    # Print out all available ports for debugging
    if not ports:
        print("⚠️ No COM ports detected!")
    else:
        print("Detected COM ports:")
        for port in ports:
            print(f"Port: {port.device}, Description: {port.description}")

    # Iterate through all available ports
    for port in ports:
        try:
            # Skip irrelevant ports such as Bluetooth or non-serial devices
            if "Bluetooth" in port.description or "IrDA" in port.description:
                print(f"⚠️ Skipping non-serial device: {port.device} ({port.description})")
                continue

            # Attempt to open the port with the specified baudrate and timeout
            print(f"🔄 Trying to open port {port.device}")
            with serial.Serial(port.device, baudrate, timeout=timeout) as ser:
                # Give the port some time to establish connection
                time.sleep(1)

                # Send a simple 'ping' to test if the device responds
                ser.write(b'ping')
                response = ser.read(4)  # Read the response (should be 'ping')

                # Check if the response matches 'Temp'
                if b'Temp' in response:  # Check if 'Temp' is in the response
                    print(f"✅ Found working serial port: {port.device}")
                    return port.device  # Return the valid port if response contains 'Temp'
                else:
                    print(f"⚠️ No valid response from {port.device}: {response}")

        except (serial.SerialException, OSError) as e:
            print(f"⚠️ Skipping {port.device}: {e}")

    # If no valid COM port is found, return None
    print("❌ No valid COM port found!")
    return None


class SerialReader(QThread):
    """Thread for reading serial data."""
    data_received = pyqtSignal(str)

    def __init__(self, baudrate=9600):
        super().__init__()
        self.port = find_serial_port()
        self.baudrate = baudrate
        self.running = True

    def run(self):
        """Continuously read serial data and emit signals."""
        if not self.port:
            print("🚨 No serial port found! Exiting thread.")
            return

        try:
            with serial.Serial(self.port, self.baudrate, timeout=1) as ser:
                ser.flushInput()
                while self.running:
                    raw_data = ser.readline()
                    if raw_data:
                        decoded_data = raw_data.decode('utf-8', errors='replace').strip()
                        print(f"📡 Received Serial Data: {decoded_data}")
                        self.data_received.emit(decoded_data)
        except Exception as e:
            print(f"❌ Serial Error: {e}")

    def stop(self):
        """Stop the serial thread."""
        self.running = False
        self.quit()
        self.wait()

class SensorGUI(QWidget):
    """Main GUI application."""

    def __init__(self):
        super().__init__()
        self.initUI()

        # Data storage
        self.temp_data = deque(maxlen=50)
        self.humidity_data = deque(maxlen=50)
        self.soil_data = deque(maxlen=50)

        # Serial reader thread
        self.serial_thread = SerialReader()
        self.serial_thread.data_received.connect(self.process_serial_data)
        self.serial_thread.start()

        # Timer for force-updating the graph every second
        self.timer = QTimer()
        self.timer.timeout.connect(self.update_graphs)
        self.timer.start(500)  # Faster refresh rate (0.5 sec)

    def initUI(self):
        """Initialize UI layout."""
        self.setWindowTitle("Plant Monitoring System (Windows)")
        self.setGeometry(100, 100, 900, 1000)

        # Labels
        self.temp_label = QLabel("Temperature: -- °C")
        self.humidity_label = QLabel("Humidity: -- %")
        self.sunlight_label = QLabel("Sunlight: --")
        self.soil_label = QLabel("Soil Moisture: -- %")
        self.pump_label = QLabel("Pump: --")

        # Layouts
        label_layout = QVBoxLayout()
        label_layout.addWidget(self.temp_label)
        label_layout.addWidget(self.humidity_label)
        label_layout.addWidget(self.sunlight_label)
        label_layout.addWidget(self.soil_label)
        label_layout.addWidget(self.pump_label)

        # Graphs
        self.figure = Figure()
        self.canvas = FigureCanvas(self.figure)

        self.ax_temp = self.figure.add_subplot(311)
        self.ax_humidity = self.figure.add_subplot(312)
        self.ax_soil = self.figure.add_subplot(313)

        self.figure.subplots_adjust(hspace=0.4)

        # Layout Management
        graph_layout = QVBoxLayout()
        graph_layout.addWidget(self.canvas)

        main_layout = QHBoxLayout()
        main_layout.addLayout(label_layout)
        main_layout.addLayout(graph_layout)

        self.setLayout(main_layout)

    def process_serial_data(self, data):
        """Processes incoming serial data and updates UI."""
        try:
            data_parts = data.split(" | ")

            # Ensuring correct data format
            if len(data_parts) < 5:
                print("⚠️ Incomplete data received, skipping...")
                return

            # Update labels
            self.temp_label.setText(data_parts[0])
            self.humidity_label.setText(data_parts[1])
            self.sunlight_label.setText(data_parts[2])
            self.soil_label.setText(data_parts[3])
            self.pump_label.setText(data_parts[4])

            # Extract numeric values safely
            try:
                temp_value = float(data_parts[0].split(":")[1].strip().replace("°C", ""))
            except ValueError:
                temp_value = None
            try:
                humidity_value = float(data_parts[1].split(":")[1].strip().replace("%", ""))
            except ValueError:
                humidity_value = None
            try:
                soil_value = float(data_parts[3].split(":")[1].strip().replace("%", ""))
            except ValueError:
                soil_value = None

            # Store only valid values
            if temp_value is not None:
                self.temp_data.append(temp_value)
            if humidity_value is not None:
                self.humidity_data.append(humidity_value)
            if soil_value is not None:
                self.soil_data.append(soil_value)

            print(f"✅ Parsed Temp: {temp_value}°C, Humidity: {humidity_value}%, Soil: {soil_value}%")

        except Exception as e:
            print(f"⚠️ Data Processing Error: {e}")

    def update_graphs(self):
        """Update the live graphs with new data."""
        self.ax_temp.clear()
        self.ax_humidity.clear()
        self.ax_soil.clear()

        if self.temp_data:
            self.ax_temp.plot(range(len(self.temp_data)), self.temp_data, label="Temperature (°C)", color="red")
        if self.humidity_data:
            self.ax_humidity.plot(range(len(self.humidity_data)), self.humidity_data, label="Humidity (%)", color="blue")
        if self.soil_data:
            self.ax_soil.plot(range(len(self.soil_data)), self.soil_data, label="Soil Moisture (%)", color="green")

        self.ax_humidity.set_ylim(0, 100)
        self.ax_soil.set_ylim(0, 100)

        self.ax_temp.set_ylabel("Temperature (°C)")
        self.ax_temp.set_xlabel("Time (Seconds)")
        self.ax_humidity.set_ylabel("Humidity (%)")
        self.ax_humidity.set_xlabel("Time (Seconds)")
        self.ax_soil.set_ylabel("Soil Moisture (%)")
        self.ax_soil.set_xlabel("Time (Seconds)")

        if self.temp_data:
            self.ax_temp.legend()
        if self.humidity_data:
            self.ax_humidity.legend()
        if self.soil_data:
            self.ax_soil.legend()

        self.ax_temp.relim()
        self.ax_temp.autoscale_view()

        self.canvas.draw_idle()

    def closeEvent(self, event):
        """Cleanup on close."""
        print("🛑 Closing Application. Stopping Serial Thread...")
        self.serial_thread.stop()
        event.accept()

if __name__ == "__main__":
    app = QApplication(sys.argv)
    gui = SensorGUI()
    gui.show()
    sys.exit(app.exec_())