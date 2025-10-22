
# 🚀 IoT_Projects

**IoT_Projects** is a collection of IoT-based systems. Designed for real-world applications, these projects combine **Python**, **Arduino**, and **serial communication** to create efficient, visually striking systems with interactive user interfaces and real-time monitoring features.

> **Platform:** macOS, Windows | **Status:** Actively Maintained 👨‍💻

---

## 📁 Project Directory Structure

```
IoT_Projects/
├── Attendance_Access/
│   └── Attendance_Access.ino
├── BackendModules/
│   ├── SerialPort.py
│   └── SerialPort_AuthenticationLess.py
├── Missile_Defence_System/
│   └── Missile_Defence.ino
├── Plant_Monitoring_System/
│   ├── Arduino_Assets/
│   │   └── Plant_Monitoring_System.ino
│   └── Python_Interface/
│       ├── main.py
│       ├── requirements.txt
│       ├── legacy_ui/
│       └── SerialPort_AuthenticationLess.py
├── RFID_Parking_Gate/
│   ├── Arduino_Assets/
│   └── RFID_Parking_Gate.ino
│   
├── RFID_Toll_System/
│   ├── Arduino_Assets/
│   │   └── RFID_Toll_System.ino
│   └── Python_Interface/
│       ├── main.py
│       ├── requirements.txt
│       ├── legacy_ui/
│       └── SerialPort.py
├── Rain_Alarm/
│   └── Rain_Alarm.ino
├── Smoke_Detector/
│   └── Smoke_Detector.ino
├── Weather_Station/
│   └── Weather_Station.ino
└── README.md
```

---

## 📌 Project Highlights

### 🔹 Toll System

A futuristic toll booth GUI system that:
- Reads **RFID UID** via serial port
- Matches UIDs against a `users.csv` database
- Automatically deducts balance
- 🔥 Features:
  - Animated Scan Status
  - Profile Card UI with Balance Display
  - Glowing Status Panels
  - Mini Terminal for Scan History
  - Cyberpunk GUI made with PyQt5

### 🔹 Plant Monitoring System

A visual dashboard for monitoring plant health with:
- Real-time Arduino-based sensor readings (moisture, temperature, etc.)
- Serial communication with Python
- 💡 Features:
  - Real-Time Updates
  - Visual Panels for Sensor Data
  - Minimalist UI (legacy and modern versions)

---

## 📦 Other Projects

| Project Name            | Description                                 |
|-------------------------|---------------------------------------------|
| Attendance_Access       | Basic attendance logger using RFID          |
| Missile_Defence_System  | Servo-based system for object tracking      |
| Rain_Alarm              | Simple alert system triggered by rain       |
| RFID_Parking_Gate       | Early version of the RFID toll scanner      |
| RFID_Toll_System        | Access control for vehicle entry with UI    |
| Smoke_Detector          | Alerts when smoke is detected               |
| Weather_Station         | Shows environmental parameters live         |

---

## 🔧 Requirements

Python Dependencies (for GUI-based projects):
- `pyserial`
- `PyQt5`
- `pandas` *(for toll system balance logic)*

Install via:
```bash
pip install -r requirements.txt
```

---

## 🧠 BackendModules

Modular Python scripts like `SerialPort.py` and `SerialPort_AuthenticationLess.py` help decouple serial port detection and communication logic for reuse across multiple projects.

---

## 📝 Notes

- Legacy GUIs are preserved under `legacy_ui/` directories.
- `.DS_Store` files are to be ignored (macOS junk, obviously).
- Designed to run out-of-the-box on macOS. Windows users, may the serial gods be with you. 🔌

---

## 👨‍💻 Maintainer

Made with sleepless nights and caffeine for smart systems 🚀
