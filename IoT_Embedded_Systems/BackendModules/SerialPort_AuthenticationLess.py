import serial
import serial.tools.list_ports
import time

def find_serial_port(timeout=10):
    """Detects and returns a valid serial port connected to an Arduino-like device."""
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

                    if response:
                        print(f"✅ Valid device on {port.device} (Received: '{response}')")
                        return port.device
                    else:
                        print(f"❓ No response from {port.device}")
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