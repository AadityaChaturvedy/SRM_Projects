/**
 * @file esp32_standalone_server.ino
 * @description This firmware runs on an ESP32 and creates a standalone Wi-Fi Access Point (AP).
 * It serves a web dashboard that displays simulated performance metrics for both a single-cycle and
 * a 5-stage pipelined CPU. The ESP32 runs the simulation, provides a JSON API for the data, and
 * accepts HTTP commands to control the simulation.
 * @author [Your Name/Project Group]
 * @date 2025-10-22
 */

#include <WiFi.h>
#include <WebServer.h>
#include <ArduinoJson.h>

// --- Wi-Fi Access Point Credentials ---
const char* ssid = "FPGA_MONITOR";
const char* password = "12345678";

// --- Web Server (runs on port 80) ---
WebServer server(80);

// --- Simulation State & Metrics ---

/**
 * @struct CpuData
 * @brief Holds all performance metrics for a single CPU simulation.
 */
struct CpuData {
  float cpi;                      // Cycles Per Instruction
  unsigned long totalInstructions;      // Total instructions executed
  unsigned long totalBranchInstructions; // Total branch instructions encountered
  unsigned long branchMispredictions;   // Total branch mispredictions
  float mispredictionRate;          // Rate of mispredictions (mispredictions / total branches)
};

CpuData pipelinedData;
CpuData singleCycleData;

// --- History for Charts ---
const int HISTORY_SIZE = 30; // Number of historical data points to store
float cpiHistory[HISTORY_SIZE];
float mispredictionRateHistory[HISTORY_SIZE];
float singleCpiHistory[HISTORY_SIZE];
float singleMispredictHistory[HISTORY_SIZE];
int historyIndex = 0; // Current index for the circular history buffer

// --- Simulation Control ---
bool isRunning = false; // Flag to control if the simulation is running or paused
unsigned long lastUpdateTime = 0; // Timestamp of the last simulation update
unsigned int simulationSpeedMs = 1500; // Delay between simulation updates
String currentScenario = "default"; // Current simulation scenario

// --- Function Prototypes ---
void updateSimulationData();
void updateSingleCycleData();
void resetSimulation();
void handleCommand(String cmd);

/**
 * @brief Main setup function. Initializes serial communication, resets the simulation,
 * sets up the Wi-Fi AP, and defines server endpoints.
 */
void setup() {
  Serial.begin(115200);
  resetSimulation();

  // --- Wi-Fi AP Setup ---
  WiFi.mode(WIFI_AP);
  WiFi.softAP(ssid, password);
  Serial.print("AP IP address: ");
  Serial.println(WiFi.softAPIP()); // The dashboard is accessible at this IP

  // --- Web Server Endpoints ---

  // Root endpoint
  server.on("/", []() {
    server.sendHeader("Access-Control-Allow-Origin", "*");
    server.send(200, "text/plain", "ESP32 CPU Simulator. Visit /metrics for data.");
  });

  // JSON metrics endpoint
  server.on("/metrics", HTTP_GET, []() {
    // Update simulation data if it's time to do so
    if (isRunning && (millis() - lastUpdateTime > simulationSpeedMs)) {
      updateSimulationData();
      updateSingleCycleData();
      lastUpdateTime = millis();
    }

    // Create a JSON document to hold all the data
    StaticJsonDocument<2048> doc;
    
    // Add pipelined CPU data
    JsonObject pipelined = doc.createNestedObject("PipelinedCPU");
    pipelined["cpi"] = pipelinedData.cpi;
    pipelined["totalInstructions"] = pipelinedData.totalInstructions;
    pipelined["totalBranchInstructions"] = pipelinedData.totalBranchInstructions;
    pipelined["branchMispredictions"] = pipelinedData.branchMispredictions;
    pipelined["mispredictionRate"] = pipelinedData.mispredictionRate;

    // Add single-cycle CPU data
    JsonObject single = doc.createNestedObject("SingleCycleCPU");
    single["cpi"] = singleCycleData.cpi;
    single["totalInstructions"] = singleCycleData.totalInstructions;
    single["totalBranchInstructions"] = singleCycleData.totalBranchInstructions;
    single["branchMispredictions"] = singleCycleData.branchMispredictions;
    single["mispredictionRate"] = singleCycleData.mispredictionRate;

    // Add historical data for charts
    JsonObject history = doc.createNestedObject("history");
    JsonArray pipelinedCpi = history.createNestedArray("cpi");
    JsonArray pipelinedMispredict = history.createNestedArray("mispredictionRate");
    JsonArray singleCpi = history.createNestedArray("singleCpi");
    JsonArray singleMispred = history.createNestedArray("singleMispredictionRate");
    for (int i = 0; i < HISTORY_SIZE; i++) {
      pipelinedCpi.add(cpiHistory[i]);
      pipelinedMispredict.add(mispredictionRateHistory[i]);
      singleCpi.add(singleCpiHistory[i]);
      singleMispred.add(singleMispredictHistory[i]);
    }

    // Calculate and add the speedup factor
    float speedup = singleCycleData.cpi / ((pipelinedData.cpi > 0.01) ? pipelinedData.cpi : 1.0);
    doc["speedupFactor"] = speedup;

    // Serialize JSON and send the response
    String output;
    serializeJson(doc, output);
    server.sendHeader("Access-Control-Allow-Origin", "*");
    server.send(200, "application/json", output);
  });

  // Command endpoint
  server.on("/cmd", HTTP_GET, []() {
    server.sendHeader("Access-Control-Allow-Origin", "*");
    if (server.hasArg("set")) {
      handleCommand(server.arg("set"));
      server.send(200, "text/plain", "OK");
    } else {
      server.send(400, "text/plain", "Missing ?set= command");
    }
  });

  server.begin();
  Serial.println("HTTP server started");
}

/**
 * @brief Main loop. Handles incoming client requests.
 */
void loop() {
  server.handleClient();
}

/**
 * @brief Parses and handles commands received via the /cmd endpoint.
 * @param command The command string to process.
 */
void handleCommand(String command) {
  command.trim();
  command.toLowerCase();
  if (command == "start") isRunning = true;
  else if (command == "pause") isRunning = false;
  else if (command == "reset") { isRunning = false; resetSimulation(); }
  else if (command.startsWith("speed:")) simulationSpeedMs = command.substring(6).toInt();
  else if (command.startsWith("scenario:")) currentScenario = command.substring(9);
}

/**
 * @brief Resets all simulation metrics and history to their initial states.
 */
void resetSimulation() {
  pipelinedData = {1.25, 0, 0, 0, 0};
  singleCycleData = {1.0, 0, 0, 0, 0};
  
  for (int i = 0; i < HISTORY_SIZE; i++) {
    cpiHistory[i] = 1.25;
    mispredictionRateHistory[i] = 0;
    singleCpiHistory[i] = 1.0;
    singleMispredictHistory[i] = 0;
  }
  historyIndex = 0;
}

/**
 * @brief Updates the pipelined CPU simulation data for one time tick based on the current scenario.
 */
void updateSimulationData() {
  // --- Scenario-based parameters ---
  float mispredictChance = 0.05, mispredictMultiplier = 0.2, branchFreq = 0.2;
  if (currentScenario == "high_mispredict") { mispredictChance = 0.35; mispredictMultiplier = 0.5; }
  else if (currentScenario == "low_mispredict") { mispredictChance = 0.01; mispredictMultiplier = 0.05; }
  else if (currentScenario == "high_branch_freq") { branchFreq = 0.4; }
  else if (currentScenario == "low_branch_freq") { branchFreq = 0.05; }
  else if (currentScenario == "worst_case_cpi") { mispredictChance = 0.5; mispredictMultiplier = 0.8; branchFreq = 0.3; }
  else if (currentScenario == "best_case_cpi") { mispredictChance = 0; mispredictMultiplier = 0; }

  // --- Simulate this tick's activity ---
  unsigned long instructionsThisTick = random(5000, 15000);
  unsigned long branchesThisTick = instructionsThisTick * branchFreq;
  bool mispredictionEvent = (random(0, 100) / 100.0) < mispredictChance;
  unsigned long mispredictionsThisTick = mispredictionEvent ? (unsigned long)(branchesThisTick * mispredictMultiplier) : random(0, 3);

  // --- Update Metrics ---
  pipelinedData.totalInstructions += instructionsThisTick;
  pipelinedData.totalBranchInstructions += branchesThisTick;
  pipelinedData.branchMispredictions += mispredictionsThisTick;

  if (pipelinedData.totalBranchInstructions > 0) {
    pipelinedData.mispredictionRate = (float)pipelinedData.branchMispredictions / pipelinedData.totalBranchInstructions;
  }

  // Calculate CPI based on a baseline and a penalty for mispredictions
  float mispredictionPenalty = (float)(mispredictionsThisTick * 10) / instructionsThisTick;
  pipelinedData.cpi = 1.15 + mispredictionPenalty + (random(0, 100) / 100.0 - 0.5) * 0.1; // Add some noise

  // --- Update History ---
  cpiHistory[historyIndex] = pipelinedData.cpi;
  mispredictionRateHistory[historyIndex] = pipelinedData.mispredictionRate * 100.0;
}

/**
 * @brief Updates the single-cycle CPU simulation data for one time tick.
 */
void updateSingleCycleData() {
  // --- Scenario-based parameters ---
  float branchFreq = 0.2;
  if (currentScenario == "high_branch_freq") branchFreq = 0.4;
  else if (currentScenario == "low_branch_freq") branchFreq = 0.05;

  // --- Simulate this tick's activity ---
  unsigned long instructionsThisTick = random(5000, 15000);
  unsigned long branchesThisTick = instructionsThisTick * branchFreq;

  // A single-cycle CPU has a baseline CPI of 1.0, but we can simulate stalls.
  float baselineCpi = 1.0;
  
  // Simulate memory stalls (e.g., cache misses)
  float cacheMissRate = 0.01; // 1% miss rate
  unsigned long memAccessesThisTick = instructionsThisTick / 3; // Assume 1/3 of instructions are memory accesses
  unsigned long cacheMissesThisTick = memAccessesThisTick * cacheMissRate;
  float extraStallCycles = cacheMissesThisTick * 40; // Each miss costs 40 cycles

  // Effective CPI = (base cycles + extra stalls) / total instructions
  float effectiveCpi = baselineCpi; // Simplified for this model

  // Single-cycle CPUs don't have pipeline hazards or branch mispredictions in the same way.
  // We model a very low misprediction rate for realism.
  float mispredRateThisTick = 0.001; // 0.1%
  unsigned long mispredictionsThisTick = branchesThisTick * mispredRateThisTick;

  // --- Update Metrics ---
  singleCycleData.totalInstructions += instructionsThisTick;
  singleCycleData.totalBranchInstructions += branchesThisTick;
  singleCycleData.branchMispredictions += mispredictionsThisTick;
  singleCycleData.mispredictionRate = (singleCycleData.totalBranchInstructions > 0) ?
      ((float)singleCycleData.branchMispredictions / singleCycleData.totalBranchInstructions) : 0;
  singleCycleData.cpi = effectiveCpi;

  // --- Update History ---
  singleCpiHistory[historyIndex] = singleCycleData.cpi;
  singleMispredictHistory[historyIndex] = singleCycleData.mispredictionRate * 100.0;
  
  // Increment history index for the next update
  historyIndex = (historyIndex + 1) % HISTORY_SIZE;
}