#include <OneWire.h>
#include <DallasTemperature.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClientSecure.h>
#include <WiFiUdp.h>
#include <NTPClient.h>

// --- Pin Definitions ---
#define ONE_WIRE_BUS D4
const int PULSE_SENSOR_PIN = A0;

// --- Calibration ---
const float TEMP_OFFSET = 0.0;

// --- Sensor Objects ---
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature tempSensor(&oneWire);

// --- BPM Calculation Variables ---
const int pulseThreshold = 550;
long IBI = 600;
unsigned long lastBeatTime = 0;
bool pulseDetected = false;

// --- BPM Smoothing ---
const int BPM_AVG_WINDOW = 8;                  // Number of beats to average BPM over
int BPMHistory[BPM_AVG_WINDOW] = {75};         // Start with mid value
int BPMHistIndex = 0;                          // Index
int BPM = 0;

// --- Debounce ---
const unsigned long MIN_IBI = 300;             // Minimum allowed IBI (prevents double-counting)
unsigned long lastDebounceTime = 0;

// --- Temperature Variables ---
float currentTemperatureC = 0.0;
unsigned long lastTempRequest = 0;
const int TEMP_READ_INTERVAL = 2000;

// --- Analog Smoothing ---
const int SMOOTH_WINDOW = 8;
int analogHistory[SMOOTH_WINDOW] = {0};
int analogIndex = 0;

// --- WiFi Credentials ---
const char* ssid = "AadityaiPhone";
const char* password = "REDACTED_WIFI";

// --- Supabase API ---
const char* supabaseUrl = "https://qxxzgqdoifkpfpdleavo.supabase.co/rest/v1/medicos";
const char* supabaseApiKey = "REDACTED_MED_KEY";

// --- NTP Setup for ISO Time ---
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 19800, 60000); // IST offset (+5:30 = 19800 seconds)

String getISO8601(long epoch) {
  time_t rawTime = epoch;
  struct tm* ti = gmtime(&rawTime); // UTC time
  char buf[25];
  snprintf(buf, sizeof(buf), "%04d-%02d-%02dT%02d:%02d:%02dZ",
           ti->tm_year + 1900, ti->tm_mon + 1, ti->tm_mday,
           ti->tm_hour, ti->tm_min, ti->tm_sec);
  return String(buf);
}

void setup() {
  Serial.begin(115200);
  tempSensor.begin();

  // --- Connect to WiFi ---
  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" Connected!");

  // --- Start NTP ---
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
      delay(1000);
    }
    http.end();
  } else {
    Serial.println("WiFi not connected, cannot send data.");
  }
}

void loop() {
  // Temperature read
  if (millis() - lastTempRequest > TEMP_READ_INTERVAL) {
    lastTempRequest = millis();
    tempSensor.requestTemperatures();
    float rawTemperatureC = tempSensor.getTempCByIndex(0);
    if (rawTemperatureC != DEVICE_DISCONNECTED_C) {
      currentTemperatureC = rawTemperatureC + TEMP_OFFSET;
    }
  }

  // Analog signal smoothing (simple moving average)
  analogHistory[analogIndex % SMOOTH_WINDOW] = analogRead(PULSE_SENSOR_PIN);
  analogIndex++;
  int smoothedPulse = 0;
  for (int i = 0; i < SMOOTH_WINDOW; i++) {
    smoothedPulse += analogHistory[i];
  }
  smoothedPulse /= SMOOTH_WINDOW;

  // Heartbeat detection (with debouncing)
  if (smoothedPulse > pulseThreshold && !pulseDetected) {
    unsigned long now = millis();
    unsigned long interval = now - lastBeatTime;
    if (interval > MIN_IBI) { // debounce: minimum IBI check
      IBI = interval;
      lastBeatTime = now;
      int bpm = 60000.0 / IBI;
      // BPM Smoothing (moving average)
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