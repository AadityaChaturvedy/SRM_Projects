/**
 * @file medicalProj.ino
 * @brief This firmware runs on an ESP8266 to create a real-time health monitoring device.
 * It reads data from a DS18B20 temperature sensor and a pulse sensor, processes the data,
 * and sends it to a Supabase backend for storage and visualization.
 * 
 * SECURITY NOTICE: Hardcoded credentials have been redacted.
 * To use this project, update the WiFi and Supabase constants below with your own values.
 */

#include <OneWire.h>
#include <DallasTemperature.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClientSecure.h>
#include <WiFiUdp.h>
#include <NTPClient.h>

// --- Pin Definitions ---
#define ONE_WIRE_BUS D4         // Pin for the DS18B20 temperature sensor
const int PULSE_SENSOR_PIN = A0; // Analog pin for the pulse sensor

// --- Calibration ---
const float TEMP_OFFSET = 0.0; // Temperature calibration offset

// --- Sensor Objects ---
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature tempSensor(&oneWire);

// --- BPM Calculation Variables ---
const int pulseThreshold = 550; // Threshold for detecting a pulse
long IBI = 600;                 // Inter-Beat Interval (time between beats)
unsigned long lastBeatTime = 0; // Timestamp of the last detected beat
bool pulseDetected = false;       // Flag to avoid detecting the same pulse multiple times

// --- BPM Smoothing (Moving Average) ---
const int BPM_AVG_WINDOW = 8; // Number of beats to average for a stable BPM reading
int BPMHistory[BPM_AVG_WINDOW] = {75}; // Initialize with a typical heart rate
int BPMHistIndex = 0;
int BPM = 0; // The final, smoothed BPM value

// --- Debounce ---
const unsigned long MIN_IBI = 300; // Minimum allowed IBI (200 BPM) to prevent double-counting

// --- Temperature Reading Variables ---
float currentTemperatureC = 0.0;
unsigned long lastTempRequest = 0;
const int TEMP_READ_INTERVAL = 2000; // Read temperature every 2 seconds

// --- Analog Signal Smoothing (Moving Average) ---
const int SMOOTH_WINDOW = 8; // Window size for smoothing the pulse sensor's analog signal
int analogHistory[SMOOTH_WINDOW] = {0};
int analogIndex = 0;

// --- WiFi Credentials (REDACTED) ---
const char* ssid = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";

// --- Supabase API Configuration (REDACTED) ---
const char* supabaseUrl = "https://your-project.supabase.co/rest/v1/medicos";
const char* supabaseApiKey = "YOUR_SUPABASE_ANON_KEY";

// --- NTP Client for Accurate Timestamps ---
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 19800, 60000); // IST offset (+5:30)

void setup() {
  Serial.begin(115200);
  tempSensor.begin();

  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" Connected!");

  timeClient.begin();
  timeClient.update();
}

void sendToSupabase(float temp, int hr) {
  if (WiFi.status() == WL_CONNECTED) {
    if (temp < 20 || temp > 45) return;
    if (hr < 40 || hr > 200) return;

    WiFiClientSecure client;
    client.setInsecure();

    HTTPClient http;
    http.begin(client, supabaseUrl);

    http.addHeader("Content-Type", "application/json");
    http.addHeader("apikey", supabaseApiKey);
    http.addHeader("Authorization", String("Bearer ") + supabaseApiKey);

    String payload = "{\"temperature\":" + String(temp, 2) +
                     ",\"heartRate\":" + String(hr) + "}";

    int httpResponseCode = http.POST(payload);

    Serial.print("Supabase response: ");
    Serial.println(httpResponseCode);

    if (httpResponseCode > 0) {
      Serial.println(http.getString());
    } else {
      Serial.println("Error sending to Supabase.");
    }
    http.end();
  } else {
    Serial.println("WiFi not connected, cannot send data.");
  }
}

void loop() {
  if (millis() - lastTempRequest > TEMP_READ_INTERVAL) {
    lastTempRequest = millis();
    tempSensor.requestTemperatures();
    float rawTemperatureC = tempSensor.getTempCByIndex(0);
    if (rawTemperatureC != DEVICE_DISCONNECTED_C) {
      currentTemperatureC = rawTemperatureC + TEMP_OFFSET;
    }
  }

  analogHistory[analogIndex % SMOOTH_WINDOW] = analogRead(PULSE_SENSOR_PIN);
  analogIndex++;
  int smoothedPulse = 0;
  for (int i = 0; i < SMOOTH_WINDOW; i++) {
    smoothedPulse += analogHistory[i];
  }
  smoothedPulse /= SMOOTH_WINDOW;

  if (smoothedPulse > pulseThreshold && !pulseDetected) {
    unsigned long now = millis();
    unsigned long interval = now - lastBeatTime;
    
    if (interval > MIN_IBI) {
      IBI = interval;
      lastBeatTime = now;
      int bpm = 60000.0 / IBI;
      
      BPMHistory[BPMHistIndex++ % BPM_AVG_WINDOW] = bpm;
      int sumBPM = 0;
      for (int i = 0; i < BPM_AVG_WINDOW; ++i) sumBPM += BPMHistory[i];
      BPM = sumBPM / BPM_AVG_WINDOW;
      
      Serial.print("Temperature: ");
      Serial.print(currentTemperatureC);
      Serial.print(" *C  |  Heart Rate (avg): ");
      Serial.print(BPM);
      Serial.println(" BPM");

      sendToSupabase(currentTemperatureC, BPM);
    }
    pulseDetected = true;
  }

  if (smoothedPulse < pulseThreshold) {
    pulseDetected = false;
  }

  delay(10);
}
