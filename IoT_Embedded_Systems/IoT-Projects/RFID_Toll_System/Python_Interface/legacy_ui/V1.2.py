import sys
import time
from PyQt5.QtWidgets import (
    QApplication, QWidget, QLabel, QVBoxLayout, QHBoxLayout,
    QGroupBox, QPushButton, QMessageBox, QGridLayout, QMenu, QAction,
    QProgressBar
)
from PyQt5.QtCore import Qt, QTimer, QTime, QSize
from PyQt5.QtGui import QFont, QMovie, QIcon


class TollSystemGUI(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        self.setWindowTitle("💳 Toll Booth System")
        self.setGeometry(100, 100, 1100, 650)
        self.setStyleSheet("background-color: #0f111a;")

        # 🕒 Top Bar
        self.time_label = QLabel()
        self.time_label.setFont(QFont("Consolas", 14))
        self.time_label.setStyleSheet("color: #00ffff;")

        self.title_label = QLabel("TOLL SYSTEM")
        self.title_label.setFont(QFont("Segoe UI", 18, QFont.Bold))
        self.title_label.setStyleSheet("color: #FFFFFF;")
        self.title_label.setAlignment(Qt.AlignCenter)

        self.settings_button = QPushButton("⚙️")
        self.settings_button.setFixedSize(40, 40)
        self.settings_button.setStyleSheet("background-color: transparent; color: white; font-size: 20px;")
        self.settings_button.clicked.connect(self.show_settings_menu)

        top_bar = QHBoxLayout()
        top_bar.addWidget(self.time_label, alignment=Qt.AlignLeft)
        top_bar.addWidget(self.title_label, stretch=1)
        top_bar.addWidget(self.settings_button, alignment=Qt.AlignRight)

        self.timer = QTimer(self)
        self.timer.timeout.connect(self.update_time)
        self.timer.start(1000)
        self.update_time()

        # 👤 User Details
        user_box = QGroupBox("👤 User Details")
        user_box.setStyleSheet("QGroupBox { color: #00BFFF; font-size: 16px; font-weight: bold; border: 2px solid #00BFFF; border-radius: 8px; padding: 10px; }")
        user_layout = QVBoxLayout()

        self.uid_label = QLabel("UID: 63D6D20F")
        self.name_label = QLabel("Name: Johnny Silverhand")
        self.balance_label = QLabel("Balance: ₹420.00")

        for label in [self.uid_label, self.name_label, self.balance_label]:
            label.setFont(QFont("Courier", 13, QFont.Bold))
            label.setStyleSheet("color: #7FFFD4; background-color: #1c1f2b; padding: 6px; border-radius: 6px;")
            user_layout.addWidget(label)

        user_box.setLayout(user_layout)

        # 🌀 Scan Status with Animation and Progress
        animation_box = QGroupBox("🌀 Scan Status")
        animation_box.setStyleSheet("QGroupBox { color: #ADFF2F; font-size: 16px; font-weight: bold; border: 2px solid #ADFF2F; border-radius: 8px; padding: 10px; }")
        anim_layout = QVBoxLayout()

        self.anim_label = QLabel()
        self.anim = QMovie("scanner.gif")
        self.anim.setScaledSize(QSize(200, 200))
        self.anim_label.setMovie(self.anim)
        self.anim.start()

        self.progress_bar = QProgressBar()
        self.progress_bar.setValue(65)
        self.progress_bar.setStyleSheet("QProgressBar { background-color: #1c1f2b; border: 1px solid #ADFF2F; color: white; height: 15px; border-radius: 7px; } QProgressBar::chunk { background-color: #ADFF2F; border-radius: 7px; }")

        anim_layout.addWidget(self.anim_label, alignment=Qt.AlignCenter)
        anim_layout.addWidget(self.progress_bar)
        animation_box.setLayout(anim_layout)

        # 📢 Messages
        message_box = QGroupBox("📢 Messages")
        message_box.setStyleSheet("QGroupBox { color: #FF69B4; font-size: 16px; font-weight: bold; border: 2px solid #FF69B4; border-radius: 8px; padding: 10px; }")
        msg_layout = QVBoxLayout()

        self.message_label = QLabel("✅ Safe journey, rebel!")
        self.message_label.setFont(QFont("Segoe UI", 13, QFont.Bold))
        self.message_label.setStyleSheet("color: #FFFFFF; background-color: #2e2e3a; padding: 8px; border-radius: 6px;")
        self.message_label.setAlignment(Qt.AlignCenter)

        self.timer_label = QLabel("Next vehicle in: 05s")
        self.timer_label.setFont(QFont("Segoe UI", 11))
        self.timer_label.setStyleSheet("color: #CCCCCC; padding: 4px;")
        self.timer_label.setAlignment(Qt.AlignCenter)

        msg_layout.addWidget(self.message_label)
        msg_layout.addWidget(self.timer_label)
        message_box.setLayout(msg_layout)

        # Layout
        main_layout = QVBoxLayout()
        main_layout.addLayout(top_bar)

        box_layout = QHBoxLayout()
        box_layout.addWidget(user_box, 2)
        box_layout.addWidget(animation_box, 2)
        box_layout.addWidget(message_box, 2)

        main_layout.addLayout(box_layout)
        self.setLayout(main_layout)

        # Start vehicle countdown
        self.start_countdown()

    def update_time(self):
        current_time = QTime.currentTime().toString("hh:mm:ss AP")
        self.time_label.setText(current_time)

    def start_countdown(self):
        self.count = 5
        self.count_timer = QTimer(self)
        self.count_timer.timeout.connect(self.update_countdown)
        self.count_timer.start(1000)

    def update_countdown(self):
        self.count -= 1
        if self.count < 0:
            self.count = 5
        self.timer_label.setText(f"Next vehicle in: {self.count:02d}s")

    def show_settings_menu(self):
        menu = QMenu()

        clear_action = QAction("🧹 Clear Screen", self)
        clear_action.triggered.connect(self.clear_screen)

        show_log_action = QAction("📜 Show Log", self)
        show_log_action.triggered.connect(self.show_log)

        menu.addAction(clear_action)
        menu.addAction(show_log_action)

        menu.exec_(self.settings_button.mapToGlobal(self.settings_button.rect().bottomLeft()))

    def clear_screen(self):
        self.uid_label.setText("UID: --")
        self.name_label.setText("Name: --")
        self.balance_label.setText("Balance: --")
        self.message_label.setText("📢 Messages cleared")

    def show_log(self):
        QMessageBox.information(self, "System Log", "🗂 Log feature coming soon!")


if __name__ == '__main__':
    app = QApplication(sys.argv)
    toll_gui = TollSystemGUI()
    toll_gui.show()
    sys.exit(app.exec_())