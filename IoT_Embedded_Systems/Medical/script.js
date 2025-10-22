/**
 * @file script.js
 * @description This script provides the frontend logic for the Medico Health Monitor dashboard.
 * It fetches real-time health data (temperature and heart rate) from a Supabase backend,
 * displays the latest readings, visualizes historical data on a chart, and lists recent readings in a log.
 * @author [Your Name/Project Group]
 * @date 2025-10-22
 */

document.addEventListener('DOMContentLoaded', () => {

    // --- CONFIGURATION ---
    // IMPORTANT: In a production environment, these keys should not be hardcoded.
    // They should be stored securely, for example, as environment variables on a server.
    const SUPABASE_URL = "https://qxxzgqdoifkpfpdleavo.supabase.co";
    const SUPABASE_API_KEY = "REDACTED_MED_KEY";
    const REFRESH_INTERVAL_MS = 5000; // Fetch new data every 5 seconds

    // --- DOM ELEMENT REFERENCES ---
    const currentTempEl = document.getElementById('current-temp');
    const currentHrEl = document.getElementById('current-hr');
    const hrIconWrapperEl = document.getElementById('hr-icon-wrapper');
    const readingLogEl = document.getElementById('reading-log');
    const statusBarEl = document.getElementById('status-bar');
    const refreshBtn = document.getElementById('refresh-btn');
    const themeToggleBtn = document.getElementById('theme-toggle');
    const alertBtn = document.getElementById('alert-btn');
    const chartCanvas = document.getElementById('hr-chart').getContext('2d');

    // --- STATE MANAGEMENT ---
    let chartInstance = null;
    let lastDataTimestamp = null; // To check if the fetched data is new
    let fetchIntervalId = null; // To manage the auto-refresh interval

    // --- CONSTANTS ---
    const SAFE_HEART_RATE_MIN = 60;
    const SAFE_HEART_RATE_MAX = 100;

    /**
     * @brief Triggers a browser alert for unstable heart rate.
     */
    const triggerUnstableHeartRateAlert = () => {
        alert('Warning: Unstable and unhealthy heart rate detected!');
    };

    /**
     * @brief Fetches the latest 20 health data readings from the Supabase backend.
     */
    const fetchData = async () => {
        updateStatus('loading', 'Fetching latest data...');
        
        const endpoint = `${SUPABASE_URL}/rest/v1/medicos?order=created_at.desc&limit=20&select=temperature,heartRate,created_at`;

        try {
            const response = await fetch(endpoint, {
                headers: {
                    'apikey': SUPABASE_API_KEY,
                    'Authorization': `Bearer ${SUPABASE_API_KEY}`
                }
            });

            if (!response.ok) {
                throw new Error(`API Error: ${response.status} ${response.statusText}`);
            }

            const data = await response.json();

            if (!data || data.length === 0) {
                updateStatus('error', 'No data received from the device.');
                return;
            }

            updateUI(data);

        } catch (error) {
            console.error('Failed to fetch data:', error);
            updateStatus('error', `Error: ${error.message}`);
        }
    };

    /**
     * @brief Updates all UI components with the new data.
     * @param {Array} data - An array of reading objects from the API.
     */
    const updateUI = (data) => {
        const latestReading = data[0];

        // Only update if the timestamp of the latest reading is new
        if (latestReading.created_at !== lastDataTimestamp) {
            lastDataTimestamp = latestReading.created_at;

            // 1. Update the main monitor cards
            currentTempEl.textContent = latestReading.temperature.toFixed(1);
            currentHrEl.textContent = latestReading.heartRate;

            // 2. Check for abnormal heart rate and trigger an alert if necessary
            if (latestReading.heartRate < SAFE_HEART_RATE_MIN || latestReading.heartRate > SAFE_HEART_RATE_MAX) {
                triggerUnstableHeartRateAlert();
            }
            
            // 3. Trigger a visual pulse animation for the heart icon
            hrIconWrapperEl.classList.add('pulse-animation');
            hrIconWrapperEl.addEventListener('animationend', () => {
                hrIconWrapperEl.classList.remove('pulse-animation');
            }, { once: true });

            // 4. Update the historical data chart
            updateChart(data.slice(0, 15)); // Use the latest 15 readings for the chart

            // 5. Update the log of recent readings
            updateLog(data);
            
            updateStatus('success', `Updated at ${new Date().toLocaleTimeString()}`);
        } else {
            updateStatus('success', `No new data. Last check at ${new Date().toLocaleTimeString()}`);
        }
    };

    /**
     * @brief Updates the heart rate history chart with new data.
     * @param {Array} data - An array of the most recent readings.
     */
    const updateChart = (data) => {
        // The data from the API is in reverse chronological order, so we reverse it for the chart
        const chartData = data.slice().reverse(); 
        const labels = chartData.map(d => new Date(d.created_at).toLocaleTimeString());
        const heartRates = chartData.map(d => d.heartRate);

        if (chartInstance) {
            // If the chart already exists, update its data and labels
            chartInstance.data.labels = labels;
            chartInstance.data.datasets[0].data = heartRates;
            chartInstance.update();
        } else {
            // Otherwise, initialize a new chart
            initializeChart(labels, heartRates);
        }
    };

    /**
     * @brief Updates the log of recent readings.
     * @param {Array} data - An array of the 20 most recent readings.
     */
    const updateLog = (data) => {
        readingLogEl.innerHTML = ''; // Clear previous log entries
        data.forEach(reading => {
            const li = document.createElement('li');
            li.className = 'log-item';

            const timestamp = new Date(reading.created_at).toLocaleString();
            const temp = `${reading.temperature.toFixed(1)} °C`;
            const hr = `${reading.heartRate} BPM`;

            li.innerHTML = `
                <span class="timestamp">${timestamp}</span>
                <span class="reading-value">${temp}</span>
                <span class="reading-value">${hr}</span>
            `;
            readingLogEl.appendChild(li);
        });
    };

    /**
     * @brief Updates the status bar with a message and a status type.
     * @param {'loading' | 'success' | 'error'} type - The type of status.
     * @param {string} message - The message to display.
     */
    const updateStatus = (type, message) => {
        statusBarEl.textContent = message;
        statusBarEl.className = `status-bar ${type}`;
    };

    /**
     * @brief Initializes the Chart.js instance with custom styling.
     * @param {Array} initialLabels - The initial set of labels for the x-axis.
     * @param {Array} initialData - The initial set of data points for the y-axis.
     */
    const initializeChart = (initialLabels = [], initialData = []) => {
        const isDarkMode = document.body.classList.contains('dark-mode');
        const gridColor = isDarkMode ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.1)';
        const textColor = isDarkMode ? '#e0e0e0' : '#333333';

        chartInstance = new Chart(chartCanvas, {
            type: 'line',
            data: {
                labels: initialLabels,
                datasets: [{
                    label: 'Heart Rate',
                    data: initialData,
                    borderColor: '#ef4444',
                    backgroundColor: 'rgba(239, 68, 68, 0.1)',
                    fill: true,
                    tension: 0.4,
                    borderWidth: 2,
                    pointBackgroundColor: '#ef4444',
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: false, grid: { color: gridColor }, ticks: { color: textColor } },
                    x: { grid: { display: false }, ticks: { color: textColor } }
                },
                plugins: { legend: { display: false } }
            }
        });
    };

    /**
     * @brief Applies the selected theme (light or dark) to the page.
     * @param {'light' | 'dark'} theme - The theme to apply.
     */
    const applyTheme = (theme) => {
        document.body.classList.toggle('dark-mode', theme === 'dark');
        updateChartTheme();
    };

    /**
     * @brief Updates the chart's theme to match the page's theme.
     */
    const updateChartTheme = () => {
        if (!chartInstance) return;
        const isDarkMode = document.body.classList.contains('dark-mode');
        const gridColor = isDarkMode ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.1)';
        const textColor = isDarkMode ? '#e0e0e0' : '#333333';
        
        chartInstance.options.scales.y.grid.color = gridColor;
        chartInstance.options.scales.y.ticks.color = textColor;
        chartInstance.options.scales.x.ticks.color = textColor;
        chartInstance.update();
    };

    // --- EVENT LISTENERS & INITIALIZATION ---

    // Theme toggle button
    themeToggleBtn.addEventListener('click', () => {
        const isDark = document.body.classList.toggle('dark-mode');
        const newTheme = isDark ? 'dark' : 'light';
        localStorage.setItem('theme', newTheme);
        updateChartTheme();
    });

    // Manual alert button
    alertBtn.addEventListener('click', triggerUnstableHeartRateAlert);

    // Manual refresh button
    refreshBtn.addEventListener('click', () => {
        clearInterval(fetchIntervalId);
        fetchData();
        fetchIntervalId = setInterval(fetchData, REFRESH_INTERVAL_MS);
    });

    // Load saved theme from local storage or default to light
    const savedTheme = localStorage.getItem('theme') || 'light';
    applyTheme(savedTheme);

    // Initial data fetch and start of the auto-refresh interval
    fetchData();
    fetchIntervalId = setInterval(fetchData, REFRESH_INTERVAL_MS);
});
