document.addEventListener('DOMContentLoaded', () => {

    // --- CONFIGURATION ---
    // IMPORTANT: Do not expose this key in a public repository.
    // Use environment variables or a server-side proxy in a real application.
    const SUPABASE_URL = "https://qxxzgqdoifkpfpdleavo.supabase.co";
    const SUPABASE_API_KEY = "REDACTED_MED_KEY";
    const REFRESH_INTERVAL_MS = 5000; // 5 seconds

    // --- DOM ELEMENT REFERENCES ---
    const currentTempEl = document.getElementById('current-temp');
    const currentHrEl = document.getElementById('current-hr');
    const hrIconWrapperEl = document.getElementById('hr-icon-wrapper');
    const readingLogEl = document.getElementById('reading-log');
    const statusBarEl = document.getElementById('status-bar');
    const refreshBtn = document.getElementById('refresh-btn');
    const themeToggleBtn = document.getElementById('theme-toggle');
    const chartCanvas = document.getElementById('hr-chart').getContext('2d');

    // --- STATE MANAGEMENT ---
    let chartInstance = null;
    let lastDataTimestamp = null;
    let fetchIntervalId = null;

    // --- API FETCHER ---
    const fetchData = async () => {
        updateStatus('loading', 'Fetching latest data...');
        
        // Fetch the last 20 readings for the log, ordered by creation time
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

            // Process and update the UI with the new data
            updateUI(data);

        } catch (error) {
            console.error('Failed to fetch data:', error);
            updateStatus('error', `Error: ${error.message}`);
        }
    };

    // --- UI UPDATERS ---
    const updateUI = (data) => {
        const latestReading = data[0];

        // Check if data is new before updating everything to prevent screen flicker
        if (latestReading.created_at !== lastDataTimestamp) {
            lastDataTimestamp = latestReading.created_at;

            // 1. Update Monitor Cards
            currentTempEl.textContent = latestReading.temperature.toFixed(1);
            currentHrEl.textContent = latestReading.heartRate;
            
            // Trigger pulse animation for new heart rate
            hrIconWrapperEl.classList.add('pulse-animation');
            hrIconWrapperEl.addEventListener('animationend', () => {
                hrIconWrapperEl.classList.remove('pulse-animation');
            }, { once: true });

            // 2. Update Chart (with the latest 15 samples)
            updateChart(data.slice(0, 15));

            // 3. Update Reading Log
            updateLog(data);
            
            updateStatus('success', `Updated at ${new Date().toLocaleTimeString()}`);
        } else {
            updateStatus('success', `No new data. Last check at ${new Date().toLocaleTimeString()}`);
        }
    };

    const updateChart = (data) => {
        // Data comes in reverse chronological order, so we reverse it for the chart
        const chartData = data.slice().reverse(); 
        const labels = chartData.map(d => new Date(d.created_at).toLocaleTimeString());
        const heartRates = chartData.map(d => d.heartRate);

        if (chartInstance) {
            chartInstance.data.labels = labels;
            chartInstance.data.datasets[0].data = heartRates;
            chartInstance.update();
        } else {
            initializeChart(labels, heartRates);
        }
    };

    const updateLog = (data) => {
        readingLogEl.innerHTML = ''; // Clear previous entries
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

    const updateStatus = (type, message) => {
        statusBarEl.textContent = message;
        statusBarEl.className = `status-bar ${type}`; // Applies .loading, .success, or .error class
    };

    // --- CHART INITIALIZATION ---
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
                    y: {
                        beginAtZero: false,
                        grid: { color: gridColor },
                        ticks: { color: textColor }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { color: textColor }
                    }
                },
                plugins: {
                    legend: { display: false }
                }
            }
        });
    };

    // --- THEME MANAGEMENT ---
    const applyTheme = (theme) => {
        if (theme === 'dark') {
            document.body.classList.add('dark-mode');
        } else {
            document.body.classList.remove('dark-mode');
        }
        updateChartTheme();
    };

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

    themeToggleBtn.addEventListener('click', () => {
        const isDark = document.body.classList.toggle('dark-mode');
        const newTheme = isDark ? 'dark' : 'light';
        localStorage.setItem('theme', newTheme);
        updateChartTheme();
    });

    // --- EVENT LISTENERS & INITIALIZATION ---
    refreshBtn.addEventListener('click', () => {
        // Clear existing interval to prevent race conditions and restart it
        clearInterval(fetchIntervalId);
        fetchData();
        fetchIntervalId = setInterval(fetchData, REFRESH_INTERVAL_MS);
    });

    // Check for saved theme preference
    const savedTheme = localStorage.getItem('theme') || 'light';
    applyTheme(savedTheme);

    // Initial data fetch and start auto-refresh
    fetchData();
    fetchIntervalId = setInterval(fetchData, REFRESH_INTERVAL_MS);
});