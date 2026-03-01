import sys
import csv
import time
from PyQt5.QtWidgets import (
    QApplication, QWidget, QLabel, QVBoxLayout, QPushButton, QMessageBox
)
from PyQt5.QtGui import QFont, QColor, QPalette
from PyQt5.QtCore import Qt, QTimer
from SerialPort import find_serial_port
import serial
from register_user import RegisterUserDialog  # Import the registration dialog

class TollSystem(QWidget):
    def __init__(self):
        super().__init__()
        self.port = find_serial_port()
        self.ser = serial.Serial(self.port, 9600, timeout=1)
        self.first_read_skipped = False

        self.initUI()
        self.timer = QTimer()
        self.timer.timeout.connect(self.read_uid)
        self.timer.start(1000)

    def initUI(self):
        self.setWindowTitle("🚗 Toll Booth System")
        self.setGeometry(300, 100, 500, 300)

        palette = QPalette()
        palette.setColor(QPalette.Window, QColor("#1e1e2f"))
        self.setPalette(palette)
        self.setAutoFillBackground(True)

        self.header = QLabel("💳 Welcome to Toll Scanner")
        self.header.setFont(QFont("Segoe UI", 16, QFont.Bold))
        self.header.setStyleSheet("color: white;")
        self.header.setAlignment(Qt.AlignCenter)

        self.uid_label = QLabel("UID: --")
        self.uid_label.setFont(QFont("Consolas", 14))
        self.uid_label.setStyleSheet("color: #00FFFF;")
        self.uid_label.setAlignment(Qt.AlignCenter)

        self.name_label = QLabel("Name: --")
        self.name_label.setFont(QFont("Consolas", 14))
        self.name_label.setStyleSheet("color: #FFA500;")
        self.name_label.setAlignment(Qt.AlignCenter)

        self.balance_label = QLabel("Balance: --")
        self.balance_label.setFont(QFont("Consolas", 14))
        self.balance_label.setStyleSheet("color: #90EE90;")
        self.balance_label.setAlignment(Qt.AlignCenter)

        self.status_label = QLabel("Status: Waiting for UID...")
        self.status_label.setFont(QFont("Segoe UI", 12))
        self.status_label.setStyleSheet("color: #FFD700;")
        self.status_label.setAlignment(Qt.AlignCenter)

        self.clear_btn = QPushButton("Clear")
        self.clear_btn.setStyleSheet("background-color: #444; color: white; border-radius: 10px; padding: 6px;")
        self.clear_btn.clicked.connect(self.reset_labels)

        layout = QVBoxLayout()
        layout.addWidget(self.header)
        layout.addWidget(self.uid_label)
        layout.addWidget(self.name_label)
        layout.addWidget(self.balance_label)
        layout.addWidget(self.status_label)
        layout.addWidget(self.clear_btn)

        self.setLayout(layout)

    def reset_labels(self):
        self.uid_label.setText("UID: --")
        self.name_label.setText("Name: --")
        self.balance_label.setText("Balance: --")
        self.status_label.setText("Status: Waiting for UID...")
        self.status_label.setStyleSheet("color: #FFD700;")

    def read_uid(self):
        if self.ser.in_waiting:
            raw_data = self.ser.readline().decode('utf-8').strip()
            if raw_data:
                if not self.first_read_skipped:
                    print("⚠️ Skipping first read:", raw_data)
                    self.first_read_skipped = True
                    return
                print(f"📡 UID Read: {raw_data}")
                self.process_uid(raw_data)

    def process_uid(self, uid):
        found = False
        rows = []
        toll_amount = 50  # Fixed amount to deduct
        name = "Unknown"
        balance = "--"

        try:
            with open("users.csv", "r") as f:
                reader = csv.reader(f)
                for row in reader:
                    if row and row[0] == uid:
                        name = row[1]
                        balance = float(row[2])
                        found = True
                        if balance >= toll_amount:
                            balance -= toll_amount
                            self.status_label.setText("🟢 Toll Deducted Successfully")
                            self.status_label.setStyleSheet("color: #00FF00;")
                            self.ser.write(b'GRANT\n')
                        else:
                            self.status_label.setText("🔴 Insufficient Balance")
                            self.status_label.setStyleSheet("color: red;")
                            self.ser.write(b'DENY\n')
                        row[2] = str(balance)
                    rows.append(row)

            if not found:
                self.register_new_user(uid)
                return

            # Always update labels regardless of balance
            self.uid_label.setText(f"UID: {uid}")
            self.name_label.setText(f"Name: {name}")
            self.balance_label.setText(f"Balance: ₹{balance}")

            with open("users.csv", "w", newline='') as f:
                writer = csv.writer(f)
                writer.writerows(rows)
                f.flush()

        except Exception as e:
            QMessageBox.critical(self, "Error", f"Error reading or writing file: {e}")

    def register_new_user(self, uid):
        dialog = RegisterUserDialog(uid)
        if dialog.exec_():
            name, balance = dialog.get_data()
            with open("users.csv", "a", newline='') as f:
                writer = csv.writer(f)
                writer.writerow([uid, name, balance])
                f.flush()

            print(f"✅ New user registered: {uid}, {name}, ₹{balance}")
            self.process_uid(uid)

    def closeEvent(self, event):
        print("🛑 Closing serial port")
        self.ser.close()
        event.accept()

if __name__ == '__main__':
    app = QApplication(sys.argv)
    toll_app = TollSystem()
    toll_app.show()
    sys.exit(app.exec_())
