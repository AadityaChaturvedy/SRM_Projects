const HISTORYSIZE = 30;
let simStatus = "paused";
let charts = { cpi: null, misprediction: null };
let timeLabels = Array(HISTORYSIZE).fill("");
let latestData = null;

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

let currentCpuMode = "pipelined";

// Helper for status
function updateStatusUI(status, message) {
  dom.statusText.textContent = message;
  dom.statusText.className = "status-text " + status;
  if (status === "connected") {
    dom.mainContent.classList.remove("disabled");
  } else {
    dom.mainContent.classList.add("disabled");
  }
}

// Periodically poll the ESP32 for JSON metrics
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

// UI Update Logic
function updateUI(data) {
  let cpuMetrics;
  if (currentCpuMode === "single" && data.SingleCycleCPU) {
    cpuMetrics = data.SingleCycleCPU;
  } else {
    cpuMetrics = data.PipelinedCPU;
  }

  dom.cpiValue.textContent = cpuMetrics.cpi.toFixed(3);
  dom.mispredictionsValue.textContent = cpuMetrics.branchMispredictions.toLocaleString();
  dom.instructionsValue.textContent = cpuMetrics.totalInstructions.toLocaleString();
  dom.mispredictionRateValue.textContent = (cpuMetrics.mispredictionRate * 100).toFixed(2);

  // Shift time labels
  timeLabels = [...timeLabels.slice(1), new Date().toLocaleTimeString()];
  if (currentCpuMode === "single") {
    updateChart(charts.cpi, timeLabels, data.history.singleCpi);
    updateChart(charts.misprediction, timeLabels, data.history.singleMispredictionRate);
  } else {
    updateChart(charts.cpi, timeLabels, data.history.cpi);
    updateChart(charts.misprediction, timeLabels, data.history.mispredictionRate);
  }

  renderCpuComparisonTable(data);
}

function renderCpuComparisonTable(data) {
  if (!data.PipelinedCPU || !data.SingleCycleCPU) return;
  const pipelined = data.PipelinedCPU, singleCycle = data.SingleCycleCPU, speedup = data.speedupFactor;
  // If you use totalCycles/maxClockFreqMHz add them here
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

// Charting logic
function createChart(canvas, label) {
  const ctx = canvas.getContext("2d");
  const gradient = ctx.createLinearGradient(0, 0, 0, 320);
  gradient.addColorStop(0, "rgba(34,211,238,0.5)");
  gradient.addColorStop(1, "rgba(34,211,238,0)");
  return new Chart(ctx, {
    type: "line",
    data: {
      labels: Array(HISTORYSIZE).fill(""),
      datasets: [{
        label: label,
        data: Array(HISTORYSIZE).fill(0),
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

function updateChart(chart, labels, data) {
  if (!chart) return;
  chart.data.labels = labels;
  chart.data.datasets[0].data = data;
  chart.update("quiet");
}

// Command sending via HTTP GET (simStatus logic for start/pause/reset)
function sendCommand(cmd) {
  fetch(`http://192.168.4.1/cmd?set=${cmd}`);
}

// UI event handlers and initialization
document.addEventListener("DOMContentLoaded", function () {
  charts.cpi = createChart(dom.cpiChartCanvas, "CPI");
  charts.misprediction = createChart(dom.mispredictionChartCanvas, "Misprediction Rate");

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

  setInterval(pollESP, 1000);

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

  dom.speedSlider.addEventListener("change", function () {
    const speed = dom.speedSlider.value;
    dom.speedValue.textContent = speed + "ms";
    sendCommand(`speed:${speed}`);
  });

  dom.scenariosPanel.addEventListener("click", function (e) {
    if (e.target.tagName !== "BUTTON") return;
    const scenario = e.target.dataset.scenario;
    sendCommand(`scenario:${scenario}`);
    dom.scenariosPanel.querySelector(".active").classList.remove("active");
    e.target.classList.add("active");
  });

  updateStatusUI("default", "Disconnected");
});