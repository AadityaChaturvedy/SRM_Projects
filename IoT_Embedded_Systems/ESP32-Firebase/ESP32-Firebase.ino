#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <DHT.h>

/* 
 * SECURITY NOTICE: Hardcoded credentials have been redacted.
 * To use this project, create a file named 'config.h' in this directory with:
 * #define WIFI_SSID "your_ssid"
 * #define WIFI_PASSWORD "your_password"
 * #define FIREBASE_HOST "your-project.firebaseio.com"
 * #define FIREBASE_AUTH "your-auth-token"
 */

// --- UNIQUE DEVICE ID ---
#define DEVICE_ID "DEVICE-001"

// --- Wi-Fi Credentials (REDACTED) ---
const char* ssid = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";

// --- Firebase Details (REDACTED) ---
#define FIREBASE_HOST "https://your-project.firebasedatabase.app/"
#define FIREBASE_AUTH "YOUR_FIREBASE_AUTH_TOKEN"

// --- Pin Definitions for ESP32 ---
#define NPK_PIN         34
#define SOIL_PIN        35
#define LDR_PIN         32
#define DHT_PIN         2
#define RELAY_IN1_WATER 26
#define RELAY_IN2_NPK   27
#define LED_NPK_RED     4
#define LED_NPK_GREEN   5

// --- DHT Sensor & Thresholds ---
#define DHT_TYPE DHT11
DHT dht(DHT_PIN, DHT_TYPE);
#define NPK_THRESHOLD   50
#define SOIL_THRESHOLD  40

int readPercentage(int pin, bool invert = false) {
  int raw = analogRead(pin);
  int pct = map(raw, 0, 4095, 0, 100);
  if (invert) pct = 100 - pct;
  return constrain(pct, 0, 100);
}

void setup() {
  Serial.begin(115200);
  dht.begin();
  pinMode(LED_NPK_RED, OUTPUT);
  pinMode(LED_NPK_GREEN, OUTPUT);
  pinMode(RELAY_IN1_WATER, OUTPUT);
  pinMode(RELAY_IN2_NPK, OUTPUT);
  digitalWrite(RELAY_IN1_WATER, HIGH);
  digitalWrite(RELAY_IN2_NPK, HIGH);

  WiFi.begin(ssid, password);
  Serial.print("Connecting to Wi-Fi");
  int wifi_timeout_counter = 0;
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
    if (wifi_timeout_counter++ >= 40) {
      Serial.println("\nFailed to connect. Restarting...");
      ESP.restart();
    }
  }
  Serial.println("\nWi-Fi Connected!");
}

void loop() {
  int npkPct = readPercentage(NPK_PIN);
  int soilPct = readPercentage(SOIL_PIN);
  int lightPct = readPercentage(LDR_PIN, true);
  float temp = dht.readTemperature();
  float hum = dht.readHumidity();
  if (isnan(temp)) temp = -99.0;
  if (isnan(hum)) hum = -99.0;
  bool npkLow = (npkPct < NPK_THRESHOLD);
  bool soilDry = (soilPct < SOIL_THRESHOLD);
  digitalWrite(LED_NPK_RED, npkLow);
  digitalWrite(LED_NPK_GREEN, !npkLow);
  digitalWrite(RELAY_IN2_NPK, !npkLow);
  digitalWrite(RELAY_IN1_WATER, !soilDry);

  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    String path = "/devices/" + String(DEVICE_ID) + "/data";
    String url = String(FIREBASE_HOST) + path + ".json?auth=" + String(FIREBASE_AUTH);

    StaticJsonDocument<256> jsonDoc;
    jsonDoc["temperature"] = temp;
    jsonDoc["humidity"] = hum;
    jsonDoc["soilMoisture"] = soilPct;
    jsonDoc["npkLevel"] = npkPct;
    jsonDoc["lightStatus"] = (lightPct > 50) ? "Bright" : "Dim";
    jsonDoc["waterPumpStatus"] = soilDry ? "ON" : "OFF";
    jsonDoc["npkPumpStatus"] = npkLow ? "ON" : "OFF";
    
    String payload;
    serializeJson(jsonDoc, payload);

    http.begin(url);
    http.addHeader("Content-Type", "application/json");
    int httpResponseCode = http.PUT(payload);

    if (httpResponseCode > 0) {
      Serial.printf("Device %s sent data, HTTP Response: %d\n", DEVICE_ID, httpResponseCode);
    } else {
      Serial.printf("Device %s Error: %d\n", DEVICE_ID, httpResponseCode);
    }
    http.end();
  }
  delay(5000);
}
