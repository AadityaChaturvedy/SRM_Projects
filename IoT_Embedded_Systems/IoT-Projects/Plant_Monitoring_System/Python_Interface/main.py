import sys
import time
import serial
import serial.tools.list_ports
from SerialPort_AuthenticationLess import find_serial_port
from collections import deque
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QVBoxLayout, QHBoxLayout
from PyQt5.QtGui import QFont, QColor, QPalette
from PyQt5.QtCore import pyqtSignal, QThread, QTimer
from matplotlib.figure import Figure
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas

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
        self.setWindowTitle("🌱 Plant Monitoring System")
        self.setGeometry(100, 100, 1200, 1000)

        # Set dark background for the entire window
        palette = QPalette()
        palette.setColor(QPalette.Window, QColor(34, 34, 34))  # #222222
        self.setAutoFillBackground(True)
        self.setPalette(palette)

        # Create styled labels
        font = QFont("Arial", 12, QFont.Bold)
        self.temp_label = QLabel("🌡️ Temperature: -- °C")
        self.humidity_label = QLabel("💧 Humidity: -- %")
        self.sunlight_label = QLabel("🌞 Sunlight: --")
        self.soil_label = QLabel("🌿 Soil Moisture: -- %")
        self.pump_label = QLabel("🚰 Pump: --")

        for label in [self.temp_label, self.humidity_label, self.sunlight_label, self.soil_label, self.pump_label]:
            label.setFont(font)
            label.setStyleSheet("color: white; padding: 8px; border-radius: 5px; background: rgba(80,80,80,0.4);")

        # Label layout
        label_layout = QVBoxLayout()
        label_layout.addWidget(self.temp_label)
        label_layout.addWidget(self.humidity_label)
        label_layout.addWidget(self.sunlight_label)
        label_layout.addWidget(self.soil_label)
        label_layout.addWidget(self.pump_label)
        label_layout.addStretch()

        # Graph setup with dark theme
        self.figure = Figure(facecolor="#EEEEEE")
        self.canvas = FigureCanvas(self.figure)
        self.ax_temp = self.figure.add_subplot(311)
        self.ax_humidity = self.figure.add_subplot(312)
        self.ax_soil = self.figure.add_subplot(313)

        for ax in [self.ax_temp, self.ax_humidity, self.ax_soil]:
            ax.set_facecolor("#222222")
            ax.grid(True, color="#888888", linestyle="--", linewidth=0.6, alpha=0.6)
            ax.spines["bottom"].set_color("black")
            ax.spines["left"].set_color("black")
            ax.xaxis.label.set_color("black")
            ax.yaxis.label.set_color("black")
            ax.tick_params(colors="black")

        # Graph layout
        graph_layout = QVBoxLayout()
        graph_layout.addWidget(self.canvas)

        # Main layout
        main_layout = QHBoxLayout()
        main_layout.addLayout(label_layout, 1)
        main_layout.addLayout(graph_layout, 3)
        self.setLayout(main_layout)

    def process_serial_data(self, data):
        try:
            parts = data.split(" | ")
            if len(parts) < 5:
                print("⚠️ Incomplete data, skipping")
                return

            # Extract and clean values
            temp_val = float(parts[0].split(":")[1].replace("°C", "").strip())
            humid_val = float(parts[1].split(":")[1].replace("%", "").strip())
            sunlight_val = parts[2].split(":")[1].strip()
            soil_val = float(parts[3].split(":")[1].replace("%", "").strip())
            pump_status = parts[4].split(":")[1].strip()

            # Format labels properly with emojis and cleaned values
            self.temp_label.setText(f"🌡️ Temperature: {temp_val} °C")
            self.humidity_label.setText(f"💧 Humidity: {humid_val} %")
            self.sunlight_label.setText(f"🌞 Sunlight: {sunlight_val}")
            self.soil_label.setText(f"🌿 Soil Moisture: {soil_val} %")
            self.pump_label.setText(f"🚰 Pump: {pump_status}")

            # Update graph data
            self.temp_data.append(temp_val)
            self.humidity_data.append(humid_val)
            self.soil_data.append(soil_val)

            print(f"✅ Parsed | Temp: {temp_val}°C | Humidity: {humid_val}% | Soil: {soil_val}%")

        except Exception as e:
            print(f"⚠️ Data parsing error: {e}")

    def update_graphs(self):
        # Clear axes
        self.ax_temp.clear()
        self.ax_humidity.clear()
        self.ax_soil.clear()

        # Plotting the data
        self.ax_temp.plot(self.temp_data, color="#FF5733", linewidth=3.5, label="Temperature (°C)")
        self.ax_humidity.plot(self.humidity_data, color="#3399FF", linewidth=3.5, label="Humidity (%)")
        self.ax_soil.plot(self.soil_data, color="#32CD32", linewidth=3.5, label="Soil Moisture (%)")

        # Styling for all axes
        for ax in [self.ax_temp, self.ax_humidity, self.ax_soil]:
            ax.set_facecolor("#222222")
            ax.grid(True, color="#888888", linestyle="--", linewidth=0.6, alpha=0.6)
            ax.spines["bottom"].set_color("black")
            ax.spines["left"].set_color("black")
            ax.tick_params(colors="black")
            ax.xaxis.label.set_color("black")
            ax.yaxis.label.set_color("black")

        # Y-axis labels
        self.ax_temp.set_ylabel("Temperature (°C)", fontsize=11, color="black")
        self.ax_humidity.set_ylabel("Humidity (%)", fontsize=11, color="black")
        self.ax_soil.set_ylabel("Soil Moisture (%)", fontsize=11, color="black")

        # X-axis labels (only bottom plot for neatness, or all three if you’re feeling generous)
        self.ax_temp.set_xlabel("Time (s)", fontsize=11, color="black")
        self.ax_humidity.set_xlabel("Time (s)", fontsize=11, color="black")
        self.ax_soil.set_xlabel("Time (s)", fontsize=11, color="black")

        # Legends
        for ax in [self.ax_temp, self.ax_humidity, self.ax_soil]:
            ax.legend(facecolor="#333333", edgecolor="none", fontsize=10, labelcolor="white")

        # Axis limits
        self.ax_humidity.set_ylim(0, 100)
        self.ax_soil.set_ylim(0, 100)
        self.figure.subplots_adjust(hspace=0.4)

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