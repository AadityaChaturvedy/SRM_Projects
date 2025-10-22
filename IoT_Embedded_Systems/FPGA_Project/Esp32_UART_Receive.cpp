/**
 * @file Esp32_UART_Receive.cpp
 * @brief This firmware runs on an ESP32 and acts as a bridge between an FPGA and a web dashboard.
 * It receives performance data from the FPGA over UART, processes it, and serves it via a Wi-Fi
 * Access Point to a web-based dashboard.
 * @author [Your Name/Project Group]
 * @date 2025-10-22
 */

#include <Arduino.h>
#include <ArduinoJson.h>
#include <HardwareSerial.h>
#include <WiFi.h>
#include <WebServer.h>

// --- PIN & UART Configuration ---
#define FPGA_RX_PIN 16      // ESP32 RX pin connected to the FPGA's TX pin
#define FPGA_TX_PIN 17      // ESP32 TX pin (not used for receiving, but defined for clarity)
#define FPGA_BAUD 115200    // Baud rate for UART communication with the FPGA

HardwareSerial FPGASerial(2); // Use UART2 for communication with the FPGA

// --- Wi-Fi AP Credentials ---
const char* ssid = "FPGA_MONITOR";
const char* password = "12345678";

// --- Web Server ---
WebServer server(80);

// --- Global Variables for Storing Performance Metrics ---
float current_cpi = 0.0;
uint32_t current_branchMispred = 0;
uint32_t current_totalInstr = 0;
uint32_t current_totalBranch = 0;
float current_mispredRate = 0.0;

// --- History Buffers for Charts ---
const int HISTORY_SIZE = 30;
float cpi_history[HISTORY_SIZE];
float mispred_rate_history[HISTORY_SIZE];
int history_index = 0;

// --- Function Prototypes ---
void receiveFPGAData();
float fixedToFloat(uint32_t fixed_point);

/**
 * @brief Main setup function. Initializes serial ports, Wi-Fi AP, web server, and data buffers.
 */
void setup() {
  Serial.begin(115200); // For debugging
  FPGASerial.begin(FPGA_BAUD, SERIAL_8N1, FPGA_RX_PIN, FPGA_TX_PIN);

  // Initialize history buffers to zero
  for (int i = 0; i < HISTORY_SIZE; i++) {
    cpi_history[i] = 0.0;
    mispred_rate_history[i] = 0.0;
  }

  // --- Wi-Fi AP Setup ---
  WiFi.mode(WIFI_AP);
  WiFi.softAP(ssid, password);
  Serial.print("AP IP address: ");
  Serial.println(WiFi.softAPIP());

  // --- Web Server Endpoints ---
  // Root endpoint
  server.on("/", []() {
    server.sendHeader("Access-Control-Allow-Origin", "*");
    server.send(200, "text/plain", "ESP32 FPGA Performance Monitor\nVisit /metrics for data.");
  });

  // JSON metrics endpoint
  server.on("/metrics", HTTP_GET, []() {
    StaticJsonDocument<2048> doc;
    doc["cpi"] = current_cpi;
    doc["branchMispredictions"] = current_branchMispred;
    doc["totalInstructions"] = current_totalInstr;
    doc["totalBranchInstructions"] = current_totalBranch;
    doc["mispredictionRate"] = current_mispredRate;
    
    // Add history data for charts
    JsonObject history = doc.createNestedObject("history");
    JsonArray cpi_array = history.createNestedArray("cpi");
    JsonArray mispred_array = history.createNestedArray("mispredictionRate");
    for (int i = 0; i < HISTORY_SIZE; i++) {
      cpi_array.add(cpi_history[i]);
      mispred_array.add(mispred_rate_history[i]);
    }
    
    String output;
    serializeJson(doc, output);
    server.sendHeader("Access-Control-Allow-Origin", "*");
    server.send(200, "application/json", output);
  });

  // Command endpoint (currently just acknowledges)
  server.on("/cmd", HTTP_GET, []() {
    server.sendHeader("Access-Control-Allow-Origin", "*");
    server.send(200, "text/plain", "OK");
  });

  server.begin();
  Serial.println("HTTP server started");
  Serial.println("ESP32 FPGA Performance Monitor Started");
}

/**
 * @brief Main loop. Continuously checks for FPGA data and handles web server clients.
 */
void loop() {
  receiveFPGAData();
  server.handleClient();
  delay(10);
}

/**
 * @brief Receives and parses a data packet from the FPGA over UART.
 * The packet has a specific structure with start and end markers.
 */
void receiveFPGAData() {
  static uint8_t packet[23];
  static uint8_t packet_index = 0;
  static bool receiving = false;

  while (FPGASerial.available()) {
    uint8_t byte = FPGASerial.read();
    
    // Wait for the start marker (0xAA)
    if (!receiving && byte == 0xAA) {
      packet[0] = byte;
      packet_index = 1;
      receiving = true;
    } else if (receiving) {
      packet[packet_index++] = byte;
      
      // Check for a complete and valid packet
      if (packet_index >= 23 && packet[22] == 0xFF && packet[1] == 0x55) {
        // --- Packet Parsing ---
        // Reconstruct 32-bit values from 4 bytes (Big Endian)
        uint32_t cpi_raw = ((uint32_t)packet[2] << 24) | ((uint32_t)packet[3] << 16) | ((uint32_t)packet[4] << 8) | packet[5];
        uint32_t mispred_count = ((uint32_t)packet[6] << 24) | ((uint32_t)packet[7] << 16) | ((uint32_t)packet[8] << 8) | packet[9];
        uint32_t instr_count = ((uint32_t)packet[10] << 24) | ((uint32_t)packet[11] << 16) | ((uint32_t)packet[12] << 8) | packet[13];
        uint32_t branch_count = ((uint32_t)packet[14] << 24) | ((uint32_t)packet[15] << 16) | ((uint32_t)packet[16] << 8) | packet[17];
        uint32_t mispred_rate_raw = ((uint32_t)packet[18] << 24) | ((uint32_t)packet[19] << 16) | ((uint32_t)packet[20] << 8) | packet[21];
        
        // Convert fixed-point values to float
        current_cpi = fixedToFloat(cpi_raw);
        current_mispredRate = fixedToFloat(mispred_rate_raw);
        
        // Update global metric variables
        current_branchMispred = mispred_count;
        current_totalInstr = instr_count;
        current_totalBranch = branch_count;

        // Update history buffers for the charts
        cpi_history[history_index] = current_cpi;
        mispred_rate_history[history_index] = current_mispredRate;
        history_index = (history_index + 1) % HISTORY_SIZE; // Circular buffer

        // Reset for the next packet
        receiving = false;
        packet_index = 0;
      } else if (packet_index >= 23) {
        // If the packet is full but invalid, reset
        receiving = false;
        packet_index = 0;
      }
    }
  }
}

/**
 * @brief Converts a 32-bit fixed-point number (16.16 format) to a float.
 * @param fixed_point The 32-bit fixed-point number.
 * @return The converted float value.
 */
float fixedToFloat(uint32_t fixed_point) {
  int32_t integer_part = fixed_point >> 16;
  uint32_t fractional_part = fixed_point & 0xFFFF;
  return (float)integer_part + ((float)fractional_part / 65536.0);
}
