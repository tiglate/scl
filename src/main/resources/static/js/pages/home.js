function initCharts() {
    const ctxLine = document.getElementById('lineChart').getContext('2d');

    const lineGradient = ctxLine.createLinearGradient(0, 0, 0, 400);
    lineGradient.addColorStop(0, 'rgba(0, 26, 255, 0.2)');
    lineGradient.addColorStop(1, 'rgba(0, 26, 255, 0)');

    new Chart(ctxLine, {
        type: 'line',
        data: {
            labels: ['M', 'T', 'W', 'T', 'F', 'S', 'S'],
            datasets: [{
                data: [45, 52, 48, 70, 65, 80, 75],
                borderColor: '#001AFF',
                borderWidth: 3,
                pointRadius: 0,
                fill: true,
                backgroundColor: lineGradient,
                tension: 0.4
            }]
        },
        options: {
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { display: true },
                x: { grid: { display: true }, border: { display: false } }
            }
        }
    });

    new Chart(document.getElementById('barChart'), {
        type: 'bar',
        data: {
            labels: ['JPM', 'GS', 'SAN', 'CITI', 'BNP'],
            datasets: [{
                data: [95, 80, 65, 45, 30],
                backgroundColor: '#001AFF',
                borderRadius: 10,
                barThickness: 15
            }]
        },
        options: {
            indexAxis: 'y',
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { display: true },
                y: { grid: { display: true }, border: { display: true } }
            }
        }
    });

    const pieOptions = {
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false },
            tooltip: { enabled: true }
        },
        cutout: 0
    };

    const statusColors = {
        done: '#001AFF',
        pending: '#E5E5EA'
    };

    const createPie = (id, data) => {
        new Chart(document.getElementById(id), {
            type: 'pie',
            data: {
                labels: ['Completed', 'Pending'],
                datasets: [{
                    data: data,
                    backgroundColor: [statusColors.done, statusColors.pending],
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: pieOptions
        });
    };

    createPie('chartINS', [85, 15]); // 85% Done
    createPie('chartG10', [60, 40]); // 60% Done
    createPie('chartBRL', [92, 8]);  // 92% Done
    createPie('chartION', [45, 55]); // 45% Done (Bottleneck identified!)
}

document.addEventListener("htmx:load", (e) => {
    const home = document.getElementById('home-content');
    if (!home || home.dataset.loaded === 'true') return;
    initCharts();
    home.dataset.loaded = 'true';
});