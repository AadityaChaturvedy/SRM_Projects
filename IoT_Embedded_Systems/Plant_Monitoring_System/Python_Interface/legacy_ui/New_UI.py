import sys
from collections import deque
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QVBoxLayout, QHBoxLayout
from PyQt5.QtGui import QFont, QColor, QPalette
from PyQt5.QtCore import QTimer
from matplotlib.figure import Figure
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas

class SensorGUI(QWidget):
    """Enhanced GUI for Plant Monitoring System with White Background."""

    def __init__(self):
        super().__init__()
        self.initUI()

        # Simulated data
        self.temp_data = deque(maxlen=50)
        self.humidity_data = deque(maxlen=50)
        self.soil_data = deque(maxlen=50)

        # Timer to refresh UI
        self.timer = QTimer()
        self.timer.timeout.connect(self.update_graphs)
        self.timer.start(1000)  # Update every second

    def initUI(self):
        """Build the enhanced UI."""
        self.setWindowTitle("🌱 Plant Monitoring System")
        self.setGeometry(100, 100, 1100, 750)

        # White Theme
        self.setAutoFillBackground(True)
        palette = QPalette()
        palette.setColor(QPalette.Window, QColor(255, 255, 255))  # White Background
        self.setPalette(palette)

        # Custom fonts & labels
        font = QFont("Arial", 14, QFont.Bold)

        self.temp_label = QLabel("🌡️ Temperature: -- °C")
        self.humidity_label = QLabel("💧 Humidity: -- %")
        self.sunlight_label = QLabel("☀️ Sunlight: --")
        self.soil_label = QLabel("🌿 Soil Moisture: -- %")
        self.pump_label = QLabel("🚰 Pump: --")

        for label in [self.temp_label, self.humidity_label, self.sunlight_label, self.soil_label, self.pump_label]:
            label.setFont(font)
            label.setStyleSheet("color: #333333; padding: 8px; border-radius: 5px; background: rgba(200,200,200,0.3);")

        # Sidebar Layout
        sidebar_layout = QVBoxLayout()
        sidebar_layout.addWidget(self.temp_label)
        sidebar_layout.addWidget(self.humidity_label)
        sidebar_layout.addWidget(self.sunlight_label)
        sidebar_layout.addWidget(self.soil_label)
        sidebar_layout.addWidget(self.pump_label)
        sidebar_layout.addStretch()

        # Graphs
        self.figure = Figure(facecolor="white")
        self.canvas = FigureCanvas(self.figure)

        self.ax_temp = self.figure.add_subplot(311)
        self.ax_humidity = self.figure.add_subplot(312)
        self.ax_soil = self.figure.add_subplot(313)

        for ax in [self.ax_temp, self.ax_humidity, self.ax_soil]:
            ax.set_facecolor("#F8F8F8")  # Light grey background for graphs
            ax.grid(color="#CCCCCC", linestyle="--", linewidth=0.6, alpha=0.6)
            ax.spines["bottom"].set_color("#777777")
            ax.spines["left"].set_color("#777777")
            ax.xaxis.label.set_color("black")
            ax.yaxis.label.set_color("black")

        # Graph Layout
        graph_layout = QVBoxLayout()
        graph_layout.addWidget(self.canvas)

        # Main Layout
        main_layout = QHBoxLayout()
        main_layout.addLayout(sidebar_layout, 1)
        main_layout.addLayout(graph_layout, 3)

        self.setLayout(main_layout)

    def update_graphs(self):
        """Simulate data updates."""
        import random
        self.temp_data.append(random.uniform(20, 30))
        self.humidity_data.append(random.uniform(40, 80))
        self.soil_data.append(random.uniform(10, 60))

        self.ax_temp.clear()
        self.ax_humidity.clear()
        self.ax_soil.clear()

        # Plot new data
        self.ax_temp.plot(self.temp_data, color="#FF5733", linewidth=3.5, label="Temperature (°C)")
        self.ax_humidity.plot(self.humidity_data, color="#3399FF", linewidth=3.5, label="Humidity (%)")
        self.ax_soil.plot(self.soil_data, color="#32CD32", linewidth=3.5, label="Soil Moisture (%)")

        # Labels
        self.ax_temp.set_ylabel("Temperature (°C)", fontsize=11, color="black")
        self.ax_humidity.set_ylabel("Humidity (%)", fontsize=11, color="black")
        self.ax_soil.set_ylabel("Soil Moisture (%)", fontsize=11, color="black")

        # Legends
        self.ax_temp.legend(facecolor="#EEEEEE", edgecolor="none", fontsize=10)
        self.ax_humidity.legend(facecolor="#EEEEEE", edgecolor="none", fontsize=10)
        self.ax_soil.legend(facecolor="#EEEEEE", edgecolor="none", fontsize=10)

        # Y-axis limits for clarity
        self.ax_humidity.set_ylim(0, 100)
        self.ax_soil.set_ylim(0, 100)

        self.canvas.draw_idle()

if __name__ == "__main__":
    app = QApplication(sys.argv)
    gui = SensorGUI()
    gui.show()
    sys.exit(app.exec_())