#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

// Access Point credentials
const char* ap_ssid = "SRAM_Simulator";           // Access Point name
const char* ap_password = "YOUR_AP_PASSWORD";              // AP Password (min 8 characters)

// Access Point IP configuration
IPAddress local_ip(192, 168, 4, 1);               // ESP8266 IP address
IPAddress gateway(192, 168, 4, 1);                 // Gateway address
IPAddress subnet(255, 255, 255, 0);                // Subnet mask

// Create web server on port 80
ESP8266WebServer server(80);

// SRAM simulation variables
byte sramMemory[16] = {0}; // 16-bit SRAM array (4x4)
bool bitLinePrecharge = false;
String lastOperation = "System initialized";
unsigned long operationTimestamp = 0;

// HTML page stored in PROGMEM to save RAM
const char MAIN_PAGE[] PROGMEM = R"=====(
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SRAM Simulator - ESP8266</title>
  <style>
    :root {
      --primary-color: #008080;
      --secondary-color: #ffa500;
      --accent-color: #3e3e3e;
      --bg-dark: #222;
      --bg-medium: #f1f1f1;
      --bg-light: #ffffff;
      --text-primary: #222;
      --text-secondary: #666;
      --success-color: #228B22;
      --error-color: #b22222;
      --warning-color: #ffdd57;
    }
    
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }
    
    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: linear-gradient(135deg, var(--bg-medium) 0%, #e0e0e0 100%);
      color: var(--text-primary);
      min-height: 100vh;
      padding: 20px;
    }
    
    .container {
      max-width: 1200px;
      margin: 0 auto;
    }
    
    header {
      background: var(--bg-dark);
      color: var(--bg-light);
      padding: 25px;
      border-radius: 12px;
      margin-bottom: 25px;
      box-shadow: 0 4px 15px rgba(0,0,0,0.2);
    }
    
    h1 {
      font-size: 2.2em;
      margin-bottom: 8px;
      letter-spacing: 1px;
    }
    
    .subtitle {
      color: var(--warning-color);
      font-size: 1em;
      font-weight: 300;
    }
    
    .connection-info {
      background: var(--primary-color);
      color: var(--bg-light);
      padding: 12px;
      border-radius: 8px;
      margin-top: 15px;
      font-size: 0.9em;
    }
    
    .main-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 25px;
      margin-bottom: 25px;
    }
    
    .card {
      background: var(--bg-light);
      padding: 25px;
      border-radius: 12px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }
    
    .card h2 {
      color: var(--primary-color);
      margin-bottom: 20px;
      font-size: 1.5em;
      border-bottom: 3px solid var(--primary-color);
      padding-bottom: 10px;
    }
    
    .memory-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 12px;
      margin-top: 20px;
    }
    
    .memory-cell {
      aspect-ratio: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      border: 3px solid var(--accent-color);
      border-radius: 8px;
      font-weight: bold;
      font-size: 1.4em;
      transition: all 0.3s ease;
      background: var(--bg-light);
      cursor: pointer;
      position: relative;
    }
    
    .memory-cell:hover {
      transform: translateY(-3px);
      box-shadow: 0 4px 12px rgba(0,128,128,0.3);
    }
    
    .memory-cell.active {
      background: var(--primary-color);
      color: var(--bg-light);
      border-color: var(--primary-color);
      animation: pulse 0.5s ease;
    }
    
    .memory-cell.selected {
      border-color: var(--secondary-color);
      border-width: 4px;
    }
    
    .cell-address {
      font-size: 0.5em;
      position: absolute;
      top: 5px;
      left: 8px;
      color: var(--text-secondary);
    }
    
    @keyframes pulse {
      0%, 100% { transform: scale(1); }
      50% { transform: scale(1.05); }
    }
    
    .input-group {
      margin-bottom: 20px;
    }
    
    label {
      display: block;
      margin-bottom: 8px;
      color: var(--text-secondary);
      font-weight: 600;
      font-size: 0.95em;
    }
    
    input[type="number"] {
      width: 100%;
      padding: 12px;
      border: 2px solid var(--accent-color);
      border-radius: 6px;
      font-size: 1em;
      transition: border-color 0.3s ease;
    }
    
    input[type="number"]:focus {
      outline: none;
      border-color: var(--primary-color);
    }
    
    .button-group {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 12px;
      margin-top: 20px;
    }
    
    button {
      padding: 14px 20px;
      border: none;
      border-radius: 8px;
      font-size: 1em;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    
    .btn-primary {
      background: var(--primary-color);
      color: var(--bg-light);
    }
    
    .btn-primary:hover {
      background: #006666;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0,128,128,0.4);
    }
    
    .btn-secondary {
      background: var(--secondary-color);
      color: var(--bg-dark);
    }
    
    .btn-secondary:hover {
      background: #ff8c00;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(255,165,0,0.4);
    }
    
    .btn-warning {
      background: var(--warning-color);
      color: var(--bg-dark);
    }
    
    .btn-warning:hover {
      background: #ffd700;
    }
    
    .btn-danger {
      background: var(--error-color);
      color: var(--bg-light);
    }
    
    .btn-danger:hover {
      background: #8b0000;
    }
    
    .status-panel {
      background: var(--bg-medium);
      padding: 18px;
      border-radius: 8px;
      border-left: 5px solid var(--primary-color);
      margin-top: 20px;
    }
    
    .status-message {
      font-size: 1em;
      line-height: 1.6;
    }
    
    .status-success {
      color: var(--success-color);
      font-weight: 600;
    }
    
    .status-error {
      color: var(--error-color);
      font-weight: 600;
    }
    
    .bitline-indicator {
      display: flex;
      justify-content: space-around;
      margin-top: 20px;
      padding: 15px;
      background: var(--bg-medium);
      border-radius: 8px;
    }
    
    .bitline {
      text-align: center;
      padding: 10px;
      border-radius: 6px;
      min-width: 100px;
    }
    
    .bitline.precharged {
      background: var(--warning-color);
      color: var(--bg-dark);
      font-weight: bold;
    }
    
    .stats-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 15px;
      margin-top: 20px;
    }
    
    .stat-box {
      background: var(--bg-medium);
      padding: 15px;
      border-radius: 8px;
      text-align: center;
    }
    
    .stat-value {
      font-size: 2em;
      font-weight: bold;
      color: var(--primary-color);
    }
    
    .stat-label {
      color: var(--text-secondary);
      font-size: 0.9em;
      margin-top: 5px;
    }
    
    @media (max-width: 768px) {
      .main-grid {
        grid-template-columns: 1fr;
      }
      
      .memory-grid {
        gap: 8px;
      }
      
      h1 {
        font-size: 1.6em;
      }
    }
  </style>
</head>
<body>
  <div class="container">
    <header>
      <h1>⚡ SRAM Simulator</h1>
      <div class="subtitle">16-bit Static RAM with ESP8266 | Real-time Memory Operations</div>
      <div class="connection-info">
        📡 Access Point Mode | IP: 192.168.4.1 | Network: SRAM_Simulator
      </div>
    </header>
    
    <div class="main-grid">
      <div class="card">
        <h2>Memory Array</h2>
        <div class="memory-grid" id="memoryGrid"></div>
        <div class="bitline-indicator">
          <div class="bitline" id="blIndicator">
            <div>BL (Bit Line)</div>
            <div id="blState">Idle</div>
          </div>
          <div class="bitline" id="blBarIndicator">
            <div>BL' (Bit Line')</div>
            <div id="blBarState">Idle</div>
          </div>
        </div>
      </div>
      
      <div class="card">
        <h2>Control Panel</h2>
        <div class="input-group">
          <label>Memory Address (0-15)</label>
          <input type="number" id="address" min="0" max="15" value="0">
        </div>
        <div class="input-group">
          <label>Data Bit (0 or 1)</label>
          <input type="number" id="data" min="0" max="1" value="0">
        </div>
        <div class="button-group">
          <button class="btn-primary" onclick="writeMemory()">Write</button>
          <button class="btn-primary" onclick="readMemory()">Read</button>
          <button class="btn-warning" onclick="precharge()">Precharge</button>
          <button class="btn-danger" onclick="clearMemory()">Clear All</button>
        </div>
        <div class="status-panel">
          <div class="status-message" id="statusMessage">Ready for operation</div>
        </div>
      </div>
    </div>
    
    <div class="card">
      <h2>System Statistics</h2>
      <div class="stats-grid">
        <div class="stat-box">
          <div class="stat-value" id="onesCount">0</div>
          <div class="stat-label">Bits Set to 1</div>
        </div>
        <div class="stat-box">
          <div class="stat-value" id="zerosCount">16</div>
          <div class="stat-label">Bits Set to 0</div>
        </div>
        <div class="stat-box">
          <div class="stat-value" id="utilization">0%</div>
          <div class="stat-label">Memory Utilization</div>
        </div>
        <div class="stat-box">
          <div class="stat-value" id="operations">0</div>
          <div class="stat-label">Total Operations</div>
        </div>
      </div>
    </div>
  </div>
  
  <script>
    let selectedAddress = 0;
    let operationCount = 0;
    
    function initMemoryGrid() {
      const grid = document.getElementById('memoryGrid');
      grid.innerHTML = '';
      for(let i = 0; i < 16; i++) {
        const cell = document.createElement('div');
        cell.className = 'memory-cell';
        cell.id = 'cell-' + i;
        cell.innerHTML = '<span class="cell-address">' + i + '</span>0';
        cell.onclick = () => selectAddress(i);
        grid.appendChild(cell);
      }
    }
    
    function selectAddress(addr) {
      document.getElementById('address').value = addr;
      selectedAddress = addr;
      updateSelectedCell();
    }
    
    function updateSelectedCell() {
      document.querySelectorAll('.memory-cell').forEach(cell => {
        cell.classList.remove('selected');
      });
      const addr = parseInt(document.getElementById('address').value);
      if(addr >= 0 && addr <= 15) {
        document.getElementById('cell-' + addr).classList.add('selected');
        selectedAddress = addr;
      }
    }
    
    function updateMemoryDisplay(memoryData) {
      for(let i = 0; i < 16; i++) {
        const cell = document.getElementById('cell-' + i);
        const value = memoryData[i];
        cell.innerHTML = '<span class="cell-address">' + i + '</span>' + value;
        if(value === 1) {
          cell.classList.add('active');
        } else {
          cell.classList.remove('active');
        }
      }
      updateStats(memoryData);
    }
    
    function updateStats(memoryData) {
      let ones = memoryData.filter(x => x === 1).length;
      let zeros = 16 - ones;
      let util = Math.round((ones / 16) * 100);
      
      document.getElementById('onesCount').textContent = ones;
      document.getElementById('zerosCount').textContent = zeros;
      document.getElementById('utilization').textContent = util + '%';
      document.getElementById('operations').textContent = operationCount;
    }
    
    function showStatus(message, isError = false) {
      const statusEl = document.getElementById('statusMessage');
      statusEl.textContent = message;
      statusEl.className = 'status-message ' + (isError ? 'status-error' : 'status-success');
    }
    
    function animateBitLines(state) {
      const bl = document.getElementById('blIndicator');
      const blBar = document.getElementById('blBarIndicator');
      
      if(state === 'precharge') {
        bl.classList.add('precharged');
        blBar.classList.add('precharged');
        document.getElementById('blState').textContent = 'Vdd';
        document.getElementById('blBarState').textContent = 'Vdd';
      } else {
        bl.classList.remove('precharged');
        blBar.classList.remove('precharged');
        document.getElementById('blState').textContent = 'Idle';
        document.getElementById('blBarState').textContent = 'Idle';
      }
    }
    
    async function writeMemory() {
      const addr = parseInt(document.getElementById('address').value);
      const data = parseInt(document.getElementById('data').value);
      
      if(addr < 0 || addr > 15) {
        showStatus('Error: Address must be between 0 and 15', true);
        return;
      }
      
      if(data !== 0 && data !== 1) {
        showStatus('Error: Data must be 0 or 1', true);
        return;
      }
      
      try {
        const response = await fetch('/write?addr=' + addr + '&data=' + data);
        const result = await response.json();
        updateMemoryDisplay(result.memory);
        showStatus('Write operation successful: Data ' + data + ' written to address ' + addr);
        operationCount++;
        updateStats(result.memory);
      } catch(error) {
        showStatus('Error: Write operation failed', true);
      }
    }
    
    async function readMemory() {
      const addr = parseInt(document.getElementById('address').value);
      
      if(addr < 0 || addr > 15) {
        showStatus('Error: Address must be between 0 and 15', true);
        return;
      }
      
      animateBitLines('precharge');
      
      setTimeout(async () => {
        try {
          const response = await fetch('/read?addr=' + addr);
          const result = await response.json();
          updateMemoryDisplay(result.memory);
          showStatus('Read operation successful: Data ' + result.value + ' read from address ' + addr);
          document.getElementById('data').value = result.value;
          operationCount++;
          updateStats(result.memory);
          
          setTimeout(() => {
            animateBitLines('idle');
          }, 1000);
        } catch(error) {
          showStatus('Error: Read operation failed', true);
          animateBitLines('idle');
        }
      }, 500);
    }
    
    async function precharge() {
      try {
        const response = await fetch('/precharge');
        const result = await response.json();
        animateBitLines('precharge');
        showStatus('Bit lines precharged to Vdd');
        setTimeout(() => {
          animateBitLines('idle');
        }, 2000);
      } catch(error) {
        showStatus('Error: Precharge operation failed', true);
      }
    }
    
    async function clearMemory() {
      if(!confirm('Clear all memory cells? This will reset all values to 0.')) {
        return;
      }
      
      try {
        const response = await fetch('/clear');
        const result = await response.json();
        updateMemoryDisplay(result.memory);
        showStatus('All memory cells cleared to 0');
        operationCount++;
        updateStats(result.memory);
      } catch(error) {
        showStatus('Error: Clear operation failed', true);
      }
    }
    
    async function refreshMemory() {
      try {
        const response = await fetch('/status');
        const result = await response.json();
        updateMemoryDisplay(result.memory);
      } catch(error) {
        console.error('Failed to refresh memory');
      }
    }
    
    document.getElementById('address').addEventListener('input', updateSelectedCell);
    
    initMemoryGrid();
    updateSelectedCell();
    setInterval(refreshMemory, 2000);
  </script>
</body>
</html>
)=====";

void setup() {
  Serial.begin(115200);
  delay(100);
  
  // Initialize SRAM memory to 0
  for(int i = 0; i < 16; i++) {
    sramMemory[i] = 0;
  }
  
  Serial.println();
  Serial.println("=================================");
  Serial.println("SRAM Simulator - Access Point Mode");
  Serial.println("=================================");
  
  // Configure Access Point
  WiFi.mode(WIFI_AP);
  WiFi.softAPConfig(local_ip, gateway, subnet);
  
  // Start Access Point
  bool apStarted = WiFi.softAP(ap_ssid, ap_password);
  
  if(apStarted) {
    Serial.println("Access Point started successfully!");
    Serial.println("--------------------------------");
    Serial.print("AP SSID: ");
    Serial.println(ap_ssid);
    Serial.print("AP Password: ");
    Serial.println(ap_password);
    Serial.print("AP IP Address: ");
    Serial.println(WiFi.softAPIP());
    Serial.println("--------------------------------");
    Serial.println("Connect to the WiFi network above");
    Serial.println("Then open browser and go to:");
    Serial.println("http://192.168.4.1");
    Serial.println("=================================");
  } else {
    Serial.println("Failed to start Access Point!");
  }
  
  // Setup server routes
  server.on("/", handleRoot);
  server.on("/write", handleWrite);
  server.on("/read", handleRead);
  server.on("/clear", handleClear);
  server.on("/precharge", handlePrecharge);
  server.on("/status", handleStatus);
  
  server.begin();
  Serial.println("SRAM Simulator Server started");
  Serial.println("Waiting for client connections...");
}

void loop() {
  server.handleClient();
  
  // Optional: Monitor connected clients
  static int previousClients = 0;
  int currentClients = WiFi.softAPgetStationNum();
  
  if(currentClients != previousClients) {
    Serial.print("Connected clients: ");
    Serial.println(currentClients);
    previousClients = currentClients;
  }
}

void handleRoot() {
  server.send_P(200, "text/html", MAIN_PAGE);
}

void handleWrite() {
  if(server.hasArg("addr") && server.hasArg("data")) {
    int addr = server.arg("addr").toInt();
    int data = server.arg("data").toInt();
    
    if(addr >= 0 && addr < 16 && (data == 0 || data == 1)) {
      // Simulate write operation
      sramMemory[addr] = data;
      lastOperation = "Write: Data " + String(data) + " written to address " + String(addr);
      operationTimestamp = millis();
      
      Serial.println(lastOperation);
      
      sendMemoryJSON();
    } else {
      server.send(400, "application/json", "{\"error\":\"Invalid parameters\"}");
    }
  } else {
    server.send(400, "application/json", "{\"error\":\"Missing parameters\"}");
  }
}

void handleRead() {
  if(server.hasArg("addr")) {
    int addr = server.arg("addr").toInt();
    
    if(addr >= 0 && addr < 16) {
      // Simulate read operation with bit line precharge
      bitLinePrecharge = true;
      delay(10); // Simulate precharge delay
      
      int value = sramMemory[addr];
      lastOperation = "Read: Data " + String(value) + " read from address " + String(addr);
      operationTimestamp = millis();
      
      Serial.println(lastOperation);
      
      // Send response with read value
      String json = "{\"memory\":[";
      for(int i = 0; i < 16; i++) {
        json += String(sramMemory[i]);
        if(i < 15) json += ",";
      }
      json += "],\"value\":" + String(value) + "}";
      
      server.send(200, "application/json", json);
      
      bitLinePrecharge = false;
    } else {
      server.send(400, "application/json", "{\"error\":\"Invalid address\"}");
    }
  } else {
    server.send(400, "application/json", "{\"error\":\"Missing address parameter\"}");
  }
}

void handleClear() {
  // Clear all memory cells
  for(int i = 0; i < 16; i++) {
    sramMemory[i] = 0;
  }
  
  lastOperation = "Clear: All memory cells reset to 0";
  operationTimestamp = millis();
  
  Serial.println(lastOperation);
  
  sendMemoryJSON();
}

void handlePrecharge() {
  // Simulate bit line precharge operation
  bitLinePrecharge = true;
  lastOperation = "Precharge: Bit lines charged to Vdd";
  operationTimestamp = millis();
  
  Serial.println(lastOperation);
  
  delay(50); // Simulate precharge time
  bitLinePrecharge = false;
  
  server.send(200, "application/json", "{\"status\":\"precharged\"}");
}

void handleStatus() {
  sendMemoryJSON();
}

void sendMemoryJSON() {
  String json = "{\"memory\":[";
  for(int i = 0; i < 16; i++) {
    json += String(sramMemory[i]);
    if(i < 15) json += ",";
  }
  json += "]}";
  
  server.send(200, "application/json", json);
}
