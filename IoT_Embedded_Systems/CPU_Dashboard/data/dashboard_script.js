/**
 * @file dashboard_script.js
 * @description This script provides the frontend logic for the 5-Stage Pipelined CPU Dashboard.
 * It periodically fetches performance metrics from an ESP32 web server, updates statistics and charts,
 * and sends control commands to the simulation.
 * @author [Your Name/Project Group]
 * @date 2025-10-22
 */

// --- CONSTANTS & STATE ---
const HISTORY_SIZE = 30; // Number of data points to show in charts
let simStatus = "paused"; // Simulation status: "paused" or "running"
let charts = { cpi: null, misprediction: null }; // Chart.js instances
let timeLabels = Array(HISTORY_SIZE).fill(""); // X-axis labels for charts
let latestData = null; // Stores the most recent data from the ESP32
let currentCpuMode = "pipelined"; // The CPU mode currently being displayed: "pipelined" or "single"

// --- DOM ELEMENT REFERENCES ---
const dom = {
  statusText: document.getElementById("status-text"),
  mainContent: document.getElementById("main-content"),
  startPauseBtn: document.getElementById("start-pause-btn"),
  resetBtn: document.getElementById("reset-btn"),
  speedSlider: document.getElementById("speed-slider"),
  speedValue: document.getElementById("speed-value"),
  scenariosPanel: document.getElementById("scenarios-panel"),
  cpiValue: document.getElementById("cpi-value"),
  mispredictionsValue: document.getElementById("mispredictions-value"),
  instructionsValue: document.getElementById("instructions-value"),
  mispredictionRateValue: document.getElementById("misprediction-rate-value"),
  cpiChartCanvas: document.getElementById("cpi-chart"),
  mispredictionChartCanvas: document.getElementById("misprediction-chart"),
};

/**
 * @brief Updates the connection status display.
 * @param {string} status - The connection status ("connected", "error", "disconnected").
 * @param {string} message - The message to display.
 */
function updateStatusUI(status, message) {
  dom.statusText.textContent = message;
  dom.statusText.className = "status-text " + status;
  // Disable the main content if not connected
  if (status === "connected") {
    dom.mainContent.classList.remove("disabled");
  } else {
    dom.mainContent.classList.add("disabled");
  }
}

/**
 * @brief Polls the ESP32 server for the latest performance metrics.
 */
async function pollESP() {
  try {
    const res = await fetch("http://192.168.4.1/metrics");
    const data = await res.json();
    latestData = data;
    updateUI(latestData);
    updateStatusUI("connected", "Connected (HTTP)");
  } catch (err) {
    updateStatusUI("error", "Cannot connect. Is ESP32 running?");
  }
}

/**
 * @brief Updates the entire UI with new data from the ESP32.
 * @param {object} data - The JSON data object from the /metrics endpoint.
 */
function updateUI(data) {
  let cpuMetrics;
  // Select the correct CPU data based on the current mode
  if (currentCpuMode === "single" && data.SingleCycleCPU) {
    cpuMetrics = data.SingleCycleCPU;
  } else {
    cpuMetrics = data.PipelinedCPU;
  }

  // Update stat cards
  dom.cpiValue.textContent = cpuMetrics.cpi.toFixed(3);
  dom.mispredictionsValue.textContent = cpuMetrics.branchMispredictions.toLocaleString();
  dom.instructionsValue.textContent = cpuMetrics.totalInstructions.toLocaleString();
  dom.mispredictionRateValue.textContent = (cpuMetrics.mispredictionRate * 100).toFixed(2);

  // Update chart data
  timeLabels = [...timeLabels.slice(1), new Date().toLocaleTimeString()];
  if (currentCpuMode === "single") {
    updateChart(charts.cpi, timeLabels, data.history.singleCpi);
    updateChart(charts.misprediction, timeLabels, data.history.singleMispredictionRate);
  } else {
    updateChart(charts.cpi, timeLabels, data.history.cpi);
    updateChart(charts.misprediction, timeLabels, data.history.mispredictionRate);
  }

  // Update the comparison table
  renderCpuComparisonTable(data);
}

/**
 * @brief Renders the CPU comparison table.
 * @param {object} data - The JSON data object from the /metrics endpoint.
 */
function renderCpuComparisonTable(data) {
  if (!data.PipelinedCPU || !data.SingleCycleCPU) return;
  const pipelined = data.PipelinedCPU, singleCycle = data.SingleCycleCPU, speedup = data.speedupFactor;
  
  let tableHtml = `
  <table class="cpu-comparison-table">
    <thead>
      <tr>
        <th>Parameter</th>
        <th>Single-Cycle CPU</th>
        <th>5-Stage Pipelined CPU</th>
      </tr>
    </thead>
    <tbody>
      <tr><td>CPI</td><td>${singleCycle.cpi.toFixed(3)}</td><td>${pipelined.cpi.toFixed(3)}</td></tr>
      <tr><td>Total Instructions</td><td>${singleCycle.totalInstructions.toLocaleString()}</td><td>${pipelined.totalInstructions.toLocaleString()}</td></tr>
      <tr><td>Branch Mispredictions</td><td>${singleCycle.branchMispredictions.toLocaleString()}</td><td>${pipelined.branchMispredictions.toLocaleString()}</td></tr>
      <tr><td>Misprediction Rate (%)</td><td>${(singleCycle.mispredictionRate * 100).toFixed(2)}</td><td>${(pipelined.mispredictionRate * 100).toFixed(2)}</td></tr>
      <tr style="font-weight:bold;background:#E6F7FF;"><td>SPEEDUP FACTOR</td><td colspan="2" style="text-align:center;">${speedup.toFixed(2)}x</td></tr>
    </tbody>
  </table>
  `;
  document.getElementById("cpu-comparison-table-container").innerHTML = tableHtml;
}

/**
 * @brief Creates a new Chart.js instance.
 * @param {HTMLCanvasElement} canvas - The canvas element to render the chart on.
 * @param {string} label - The label for the chart dataset.
 * @returns {Chart} A new Chart.js instance.
 */
function createChart(canvas, label) {
  const ctx = canvas.getContext("2d");
  const gradient = ctx.createLinearGradient(0, 0, 0, 320);
  gradient.addColorStop(0, "rgba(34,211,238,0.5)");
  gradient.addColorStop(1, "rgba(34,211,238,0)");
  
  return new Chart(ctx, {
    type: "line",
    data: {
      labels: Array(HISTORY_SIZE).fill(""),
      datasets: [{
        label: label,
        data: Array(HISTORY_SIZE).fill(0),
        borderColor: "#22d3ee",
        backgroundColor: gradient,
        fill: true,
        tension: 0.4,
        pointRadius: 0,
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      animation: { duration: 400 },
      plugins: { legend: { display: false }},
      scales: {
        x: {
          grid: { color: "rgba(255,255,255,0.1)" },
          ticks: { display: false }
        },
        y: {
          grid: { color: "rgba(255,255,255,0.1)" },
          ticks: { color: "#9ca3af", padding: 10, callback: v => v.toFixed(2) }
        }
      }
    }
  });
}

/**
 * @brief Updates a chart with new data.
 * @param {Chart} chart - The Chart.js instance to update.
 * @param {string[]} labels - The new array of labels.
 * @param {number[]} data - The new array of data points.
 */
function updateChart(chart, labels, data) {
  if (!chart) return;
  chart.data.labels = labels;
  chart.data.datasets[0].data = data;
  chart.update("quiet"); // Use "quiet" to prevent animation on every update
}

/**
 * @brief Sends a command to the ESP32 via an HTTP GET request.
 * @param {string} cmd - The command to send (e.g., "start", "reset", "speed:1000").
 */
function sendCommand(cmd) {
  fetch(`http://192.168.4.1/cmd?set=${cmd}`);
}

// --- EVENT HANDLERS & INITIALIZATION ---
document.addEventListener("DOMContentLoaded", function () {
  // Create charts
  charts.cpi = createChart(dom.cpiChartCanvas, "CPI");
  charts.misprediction = createChart(dom.mispredictionChartCanvas, "Misprediction Rate");

  // CPU mode toggle buttons
  document.getElementById("show-pipelined").onclick = function () {
    currentCpuMode = "pipelined";
    this.classList.add("active");
    document.getElementById("show-single").classList.remove("active");
    if (latestData) updateUI(latestData);
  };
  document.getElementById("show-single").onclick = function () {
    currentCpuMode = "single";
    this.classList.add("active");
    document.getElementById("show-pipelined").classList.remove("active");
    if (latestData) updateUI(latestData);
  };

  // Start polling for data every second
  setInterval(pollESP, 1000);

  // Control button event listeners
  dom.startPauseBtn.addEventListener("click", function () {
    simStatus = simStatus === "paused" ? "running" : "paused";
    sendCommand(simStatus === "running" ? "start" : "pause");
    dom.startPauseBtn.textContent = simStatus === "running" ? "Pause" : "Start / Resume";
    dom.startPauseBtn.className = "btn " + (simStatus === "running" ? "btn-yellow" : "btn-green");
  });

  dom.resetBtn.addEventListener("click", function () {
    sendCommand("reset");
    simStatus = "paused";
    dom.startPauseBtn.textContent = "Start / Resume";
    dom.startPauseBtn.className = "btn btn-green";
  });

  // Speed slider event listener
  dom.speedSlider.addEventListener("change", function () {
    const speed = dom.speedSlider.value;
    dom.speedValue.textContent = speed + "ms";
    sendCommand(`speed:${speed}`);
  });

  // Scenario button event listeners
  dom.scenariosPanel.addEventListener("click", function (e) {
    if (e.target.tagName !== "BUTTON") return;
    const scenario = e.target.dataset.scenario;
    sendCommand(`scenario:${scenario}`);
    // Update active button style
    if (dom.scenariosPanel.querySelector(".active")) {
        dom.scenariosPanel.querySelector(".active").classList.remove("active");
    }
    e.target.classList.add("active");
  });

  // Set initial UI state
  updateStatusUI("default", "Disconnected");
});
