import serial
import serial.tools.list_ports
import time
import os
import sys
from PyQt5.QtWidgets import QApplication, QInputDialog, QMessageBox

KEY_FILE = "auth_key.dat"

def get_or_create_key_gui():
    """Uses a PyQt5 input dialog to get or load the authentication key."""
    if os.path.exists(KEY_FILE):
        with open(KEY_FILE, 'r') as f:
            key = f.read().strip()
            print(f"🔐 Loaded authentication key from {KEY_FILE}")
            return key
    else:
        app = QApplication(sys.argv)
        key, ok = QInputDialog.getText(None, "Enter Arduino Key", "Enter the authentication key from Arduino:")

        if ok and key:
            with open(KEY_FILE, 'w') as f:
                f.write(key.strip())
            QMessageBox.information(None, "Key Saved", f"✅ Key saved to {KEY_FILE}")
            return key.strip()
        else:
            QMessageBox.critical(None, "No Key", "❌ No key entered. Exiting.")
            sys.exit(1)

def find_serial_port(timeout=10):
    """Detects and returns a valid serial port connected to an Arduino-like device."""
    expected_key = get_or_create_key_gui()
    ports = serial.tools.list_ports.comports()
    print(f"🔍 Found {len(ports)} port(s): {[p.device for p in ports]}")

    skip_keywords = ["debug-console", "soundcore", "bluetooth"]
    start_time = time.time()

    while time.time() - start_time < timeout:
        for port in ports:
            device_name = port.device.lower()
            if any(keyword in device_name for keyword in skip_keywords):
                print(f"⚠️ Skipping non-Arduino device: {port.device}")
                continue

            try:
                print(f"🔌 Testing {port.device}...")
                with serial.Serial(port.device, 9600, timeout=2) as ser:
                    time.sleep(1.5)
                    ser.write(b'?\n')
                    response = ser.readline().decode('utf-8', errors='replace').strip()

                    if response and response == expected_key:
                        print(f"✅ Valid device on {port.device} (Received: '{response}')")
                        return port.device
                    else:
                        print(f"❓ Invalid response from {port.device} (Got: '{response}')")
            except serial.SerialException as e:
                print(f"🚫 SerialException with {port.device}: {e}")
            except Exception as e:
                print(f"⚠️ Unexpected error with {port.device}: {e}")

        print("⏳ Retrying after 2s...")
        time.sleep(2)
        ports = serial.tools.list_ports.comports()

    print("❌ No valid serial port found after timeout.")
    return None

# Test it
if __name__ == "__main__":
    port = find_serial_port()
    print(f"🎯 Final Selected Port: {port}")