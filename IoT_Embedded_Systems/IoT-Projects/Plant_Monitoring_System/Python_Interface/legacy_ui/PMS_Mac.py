import sys
import time
import serial
import serial.tools.list_ports
from collections import deque
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QVBoxLayout, QHBoxLayout
from PyQt5.QtCore import pyqtSignal, QThread, QTimer
from matplotlib.figure import Figure
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas


def find_serial_port():
    """Quickly detects the correct serial port."""
    ports = serial.tools.list_ports.comports()
    print(f"\U0001F50D Found {len(ports)} port(s): {[p.device for p in ports]}")

    for port in ports:
        if "debug-console" in port.device.lower() or "soundcore" in port.device.lower() or "Bluetooth" in port.device:
            print(f"⚠️ Skipping non-Arduino device: {port.device}")
            continue

        try:
            print(f"\U0001F50D Testing {port.device}...")
            with serial.Serial(port.device, 9600, timeout=1) as ser:
                time.sleep(0.5)
                ser.write(b'?\n')
                response = ser.readline().strip()
                if response:
                    print(f"✅ Using serial port: {port.device} (Received: {response})")
                    return port.device
        except Exception as e:
            print(f"⚠️ Error with {port.device}: {e}")

    print("❌ No valid serial port found! Using fallback.")
    return "/dev/tty.usbserial-A5069RR4"


class SerialReader(QThread):
    data_received = pyqtSignal(str)

    def __init__(self, baudrate=9600):
        super().__init__()
        self.port = find_serial_port()
        self.baudrate = baudrate
        self.running = True

    def run(self):
        try:
            with serial.Serial(self.port, self.baudrate, timeout=1) as ser:
                ser.flushInput()
                while self.running:
                    raw_data = ser.readline()
                    if raw_data:
                        decoded_data = raw_data.decode('utf-8', errors='replace').strip()
                        print(f"📡 Received Serial Data: {decoded_data}")
                        self.data_received.emit(decoded_data)
                    time.sleep(0.3)
        except Exception as e:
            print(f"❌ Serial Error: {e}")

    def stop(self):
        self.running = False
        self.quit()
        self.wait()


class SensorGUI(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

        self.temp_data = deque(maxlen=50)
        self.humidity_data = deque(maxlen=50)
        self.soil_data = deque(maxlen=50)

        self.serial_thread = SerialReader()
        self.serial_thread.data_received.connect(self.process_serial_data)
        self.serial_thread.start()

        self.timer = QTimer()
        self.timer.timeout.connect(self.update_graphs)
        self.timer.start(1000)

    def initUI(self):
        self.setWindowTitle("Plant Monitoring System")
        self.setGeometry(100, 100, 900, 600)

        self.temp_label = QLabel("Temperature: -- °C")
        self.humidity_label = QLabel("Humidity: -- %")
        self.sunlight_label = QLabel("Sunlight: --")
        self.soil_label = QLabel("Soil Moisture: -- %")
        self.pump_label = QLabel("Pump: --")

        label_layout = QVBoxLayout()
        label_layout.addWidget(self.temp_label)
        label_layout.addWidget(self.humidity_label)
        label_layout.addWidget(self.sunlight_label)
        label_layout.addWidget(self.soil_label)
        label_layout.addWidget(self.pump_label)

        self.figure = Figure()
        self.canvas = FigureCanvas(self.figure)
        self.ax_temp = self.figure.add_subplot(311)
        self.ax_humidity = self.figure.add_subplot(312)
        self.ax_soil = self.figure.add_subplot(313)
        self.figure.subplots_adjust(hspace=0.4)

        graph_layout = QVBoxLayout()
        graph_layout.addWidget(self.canvas)

        main_layout = QHBoxLayout()
        main_layout.addLayout(label_layout)
        main_layout.addLayout(graph_layout)
        self.setLayout(main_layout)

    def process_serial_data(self, data):
        try:
            data_parts = data.split(" | ")
            if len(data_parts) < 5:
                print("⚠️ Incomplete data received, skipping...")
                return

            self.temp_label.setText(data_parts[0])
            self.humidity_label.setText(data_parts[1])
            self.sunlight_label.setText(data_parts[2])
            self.soil_label.setText(data_parts[3])
            self.pump_label.setText(data_parts[4])

            try:
                temp_value = float(data_parts[0].split(":")[1].strip().replace("°C", ""))
                humidity_value = float(data_parts[1].split(":")[1].strip().replace("%", ""))
                soil_value = float(data_parts[3].split(":")[1].strip().replace("%", ""))
                self.temp_data.append(temp_value)
                self.humidity_data.append(humidity_value)
                self.soil_data.append(soil_value)
                print(f"✅ Parsed Temp: {temp_value}°C, Humidity: {humidity_value}%, Soil: {soil_value}%")
            except ValueError:
                print("⚠️ Error parsing values")

        except Exception as e:
            print(f"⚠️ Data Processing Error: {e}")

    def update_graphs(self):
        self.ax_temp.clear()
        self.ax_humidity.clear()
        self.ax_soil.clear()

        if self.temp_data:
            self.ax_temp.plot(range(len(self.temp_data)), self.temp_data, label="Temperature (°C)", color="red")
        if self.humidity_data:
            self.ax_humidity.plot(range(len(self.humidity_data)), self.humidity_data, label="Humidity (%)",
                                  color="blue")
        if self.soil_data:
            self.ax_soil.plot(range(len(self.soil_data)), self.soil_data, label="Soil Moisture (%)", color="green")

        self.ax_humidity.set_ylim(0, 100)
        self.ax_soil.set_ylim(0, 100)
        self.ax_temp.set_ylabel("Temperature (°C)")
        self.ax_humidity.set_ylabel("Humidity (%)")
        self.ax_soil.set_ylabel("Soil Moisture (%)")

        self.canvas.draw_idle()

    def closeEvent(self, event):
        print("🛑 Closing Application. Stopping Serial Thread...")
        self.serial_thread.stop()
        event.accept()


if __name__ == "__main__":
    app = QApplication(sys.argv)
    gui = SensorGUI()
    gui.show()
    sys.exit(app.exec_())