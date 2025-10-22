/*
 * ESP32 5-Stage Pipelined CPU Simulator - FINAL SERIAL COMMUNICATION VERSION
 *
 * This code runs a CPU performance simulation and sends data over the USB
 * serial port as JSON strings. It also receives commands over serial to
 * control the simulation.
 *
 * It is designed to be the backend for a webpage using the Web Serial API.
 *
 * Required Libraries:
 * - ArduinoJson (by Benoit Banchon): Install via Library Manager
 *
 * Serial Commands Received from Webpage:
 * - "start"          : Starts/resumes the simulation.
 * - "pause"          : Pauses the simulation.
 * - "reset"          : Resets the simulation state.
 * - "speed:[ms]"     : Sets simulation speed (e.g., "speed:1000").
 * - "scenario:[name]": Sets simulation scenario (e.g., "scenario:high_mispredict").
 */

#include <ArduinoJson.h>

// --- Simulation State & Metrics ---
struct CpuData {
  float cpi;
  unsigned long totalInstructions;
  unsigned long totalBranchInstructions;
  unsigned long branchMispredictions;
  float mispredictionRate;
};

CpuData currentData;

const int HISTORY_SIZE = 30;
float cpiHistory[HISTORY_SIZE];
float mispredictionRateHistory[HISTORY_SIZE];
int historyIndex = 0;

// --- Simulation Control ---
bool isRunning = false;
unsigned long lastUpdateTime = 0;
unsigned int simulationSpeedMs = 1500;
String currentScenario = "default";

// --- Function Prototypes ---
void updateSimulationData();
void resetSimulation();
void handleSerialCommands();

// --- Setup & Loop ---
void setup() {
  Serial.begin(115200); // Baud rate must be 115200 for the webpage
  resetSimulation();
}

void loop() {
  handleSerialCommands();

  if (isRunning && (millis() - lastUpdateTime > simulationSpeedMs)) {
    updateSimulationData();

    // Create a JSON document to send back to the webpage
    StaticJsonDocument<1536> doc;
    doc["cpi"] = currentData.cpi;
    doc["totalInstructions"] = currentData.totalInstructions;
    doc["totalBranchInstructions"] = currentData.totalBranchInstructions;
    doc["branchMispredictions"] = currentData.branchMispredictions;
    doc["mispredictionRate"] = currentData.mispredictionRate;

    JsonObject history = doc.createNestedObject("history");
    JsonArray cpi = history.createNestedArray("cpi");
    JsonArray mispredictionRate = history.createNestedArray("mispredictionRate");

    for(int i = 0; i < HISTORY_SIZE; i++) {
      cpi.add(cpiHistory[i]);
      mispredictionRate.add(mispredictionRateHistory[i]);
    }

    String output;
    serializeJson(doc, output);
    
    // Send the JSON string over serial, followed by a newline
    Serial.println(output);

    lastUpdateTime = millis();
  }
}

// --- Serial Command Handler ---
void handleSerialCommands() {
  if (Serial.available() > 0) {
    String command = Serial.readStringUntil('\n');
    command.trim();

    if (command == "start") isRunning = true;
    else if (command == "pause") isRunning = false;
    else if (command == "reset") { isRunning = false; resetSimulation(); }
    else if (command.startsWith("speed:")) simulationSpeedMs = command.substring(6).toInt();
    else if (command.startsWith("scenario:")) currentScenario = command.substring(9);
  }
}


// --- Simulation Logic (Unchanged) ---
void resetSimulation() { currentData.cpi=1.25; currentData.totalInstructions=0; currentData.totalBranchInstructions=0; currentData.branchMispredictions=0; currentData.mispredictionRate=0; for(int i=0;i<HISTORY_SIZE;i++){cpiHistory[i]=1.25;mispredictionRateHistory[i]=0;} historyIndex=0; }
void updateSimulationData() { float mispredictChance=0.05,mispredictMultiplier=0.2,branchFreq=0.2; if(currentScenario=="high_mispredict"){mispredictChance=0.35;mispredictMultiplier=0.5;}else if(currentScenario=="low_mispredict"){mispredictChance=0.01;mispredictMultiplier=0.05;}else if(currentScenario=="high_branch_freq"){branchFreq=0.4;}else if(currentScenario=="low_branch_freq"){branchFreq=0.05;}else if(currentScenario=="worst_case_cpi"){mispredictChance=0.5;mispredictMultiplier=0.8;branchFreq=0.3;}else if(currentScenario=="best_case_cpi"){mispredictChance=0;mispredictMultiplier=0;} unsigned long instructionsThisTick=random(5000,15000); unsigned long branchesThisTick=instructionsThisTick*branchFreq; bool mispredictionEvent=(random(0,100)/100.0)<mispredictChance; unsigned long mispredictionsThisTick=mispredictionEvent?(unsigned long)(branchesThisTick*mispredictMultiplier):random(0,3); currentData.totalInstructions+=instructionsThisTick; currentData.totalBranchInstructions+=branchesThisTick; currentData.branchMispredictions+=mispredictionsThisTick; if(currentData.totalBranchInstructions>0){currentData.mispredictionRate=(float)currentData.branchMispredictions/currentData.totalBranchInstructions;} float mispredictionPenalty=(float)(mispredictionsThisTick*10)/instructionsThisTick; currentData.cpi=1.15+mispredictionPenalty+(random(0,100)/100.0-0.5)*0.1; cpiHistory[historyIndex]=currentData.cpi; mispredictionRateHistory[historyIndex]=currentData.mispredictionRate*100.0; historyIndex=(historyIndex+1)%HISTORY_SIZE; }
