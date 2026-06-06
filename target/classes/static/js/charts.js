// Enhanced Chart.js functionality for Daily Habit Tracker

// Chart configuration and utilities
const ChartConfig = {
    colors: {
        primary: '#0d6efd',
        success: '#198754',
        warning: '#ffc107',
        danger: '#dc3545',
        info: '#0dcaf0',
        purple: '#6f42c1',
        orange: '#fd7e14',
        teal: '#20c997'
    },
    
    gradients: {
        primary: ['rgba(13, 110, 253, 0.8)', 'rgba(13, 110, 253, 0.4)'],
        success: ['rgba(25, 135, 84, 0.8)', 'rgba(25, 135, 84, 0.4)'],
        warning: ['rgba(255, 193, 7, 0.8)', 'rgba(255, 193, 7, 0.4)'],
        danger: ['rgba(220, 53, 69, 0.8)', 'rgba(220, 53, 69, 0.4)'],
        info: ['rgba(13, 202, 240, 0.8)', 'rgba(13, 202, 240, 0.4)'],
        purple: ['rgba(111, 66, 193, 0.8)', 'rgba(111, 66, 193, 0.4)'],
        orange: ['rgba(253, 126, 20, 0.8)', 'rgba(253, 126, 20, 0.4)'],
        teal: ['rgba(32, 201, 151, 0.8)', 'rgba(32, 201, 151, 0.4)']
    }
};

// Create gradient background for charts
function createGradient(ctx, colorKey) {
    const gradient = ctx.createLinearGradient(0, 0, 0, 400);
    const colors = ChartConfig.gradients[colorKey] || ChartConfig.gradients.primary;
    gradient.addColorStop(0, colors[0]);
    gradient.addColorStop(1, colors[1]);
    return gradient;
}

// Enhanced streak chart with animations and better styling
function createStreakChart(habitNames, habitStreaks) {
    const ctx = document.getElementById('streakChart');
    if (!ctx) return;
    
    const chartCtx = ctx.getContext('2d');
    
    // Create dynamic colors based on streak values
    const backgroundColors = habitStreaks.map((streak, index) => {
        const colorKeys = Object.keys(ChartConfig.gradients);
        const colorKey = colorKeys[index % colorKeys.length];
        return createGradient(chartCtx, colorKey);
    });
    
    const borderColors = habitStreaks.map((streak, index) => {
        const colorKeys = Object.keys(ChartConfig.colors);
        const colorKey = colorKeys[index % colorKeys.length];
        return ChartConfig.colors[colorKey];
    });
    
    new Chart(chartCtx, {
        type: 'bar',
        data: {
            labels: habitNames,
            datasets: [{
                label: 'Current Streak (days)',
                data: habitStreaks,
                backgroundColor: backgroundColors,
                borderColor: borderColors,
                borderWidth: 2,
                borderRadius: 8,
                borderSkipped: false,
                hoverBackgroundColor: borderColors,
                hoverBorderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: {
                duration: 1500,
                easing: 'easeOutBounce'
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    borderColor: 'rgba(255, 255, 255, 0.2)',
                    borderWidth: 1,
                    cornerRadius: 8,
                    displayColors: false,
                    callbacks: {
                        title: function(context) {
                            return context[0].label;
                        },
                        label: function(context) {
                            const streak = context.parsed.y;
                            return `${streak} day${streak !== 1 ? 's' : ''} streak`;
                        },
                        afterLabel: function(context) {
                            const streak = context.parsed.y;
                            if (streak === 0) return 'Start your streak today!';
                            if (streak < 7) return 'Keep it up!';
                            if (streak < 30) return 'Great progress!';
                            return 'Amazing dedication!';
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        color: '#6c757d',
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.1)',
                        drawBorder: false
                    }
                },
                x: {
                    ticks: {
                        maxRotation: 45,
                        minRotation: 0,
                        color: '#6c757d',
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        display: false
                    }
                }
            },
            onHover: (event, activeElements) => {
                event.native.target.style.cursor = activeElements.length > 0 ? 'pointer' : 'default';
            }
        }
    });
}

// Progress chart for individual habits (can be used in future enhancements)
function createProgressChart(checkInData, habitName) {
    const ctx = document.getElementById('progressChart');
    if (!ctx) return;
    
    const chartCtx = ctx.getContext('2d');
    
    new Chart(chartCtx, {
        type: 'line',
        data: {
            labels: checkInData.labels,
            datasets: [{
                label: `${habitName} Progress`,
                data: checkInData.values,
                borderColor: ChartConfig.colors.primary,
                backgroundColor: createGradient(chartCtx, 'primary'),
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: ChartConfig.colors.primary,
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 6,
                pointHoverRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: {
                duration: 1000,
                easing: 'easeOutQuart'
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    borderColor: 'rgba(255, 255, 255, 0.2)',
                    borderWidth: 1,
                    cornerRadius: 8
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 1,
                    ticks: {
                        stepSize: 1,
                        callback: function(value) {
                            return value === 1 ? 'Done' : 'Not Done';
                        },
                        color: '#6c757d'
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.1)',
                        drawBorder: false
                    }
                },
                x: {
                    ticks: {
                        color: '#6c757d'
                    },
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
}

// Weekly overview chart (can be used in future enhancements)
function createWeeklyOverviewChart(weeklyData) {
    const ctx = document.getElementById('weeklyChart');
    if (!ctx) return;
    
    const chartCtx = ctx.getContext('2d');
    
    new Chart(chartCtx, {
        type: 'doughnut',
        data: {
            labels: ['Completed', 'Missed'],
            datasets: [{
                data: [weeklyData.completed, weeklyData.missed],
                backgroundColor: [
                    ChartConfig.colors.success,
                    ChartConfig.colors.danger
                ],
                borderWidth: 0,
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: {
                duration: 1000,
                easing: 'easeOutQuart'
            },
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        usePointStyle: true,
                        font: {
                            size: 14
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    borderColor: 'rgba(255, 255, 255, 0.2)',
                    borderWidth: 1,
                    cornerRadius: 8,
                    callbacks: {
                        label: function(context) {
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = Math.round((context.parsed / total) * 100);
                            return `${context.label}: ${context.parsed} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

// Chart animation utilities
function animateChart(chart) {
    chart.update('active');
}

function updateChartData(chart, newData) {
    chart.data.datasets[0].data = newData;
    chart.update('active');
}

// Export functions for global use
window.ChartUtils = {
    createStreakChart,
    createProgressChart,
    createWeeklyOverviewChart,
    animateChart,
    updateChartData,
    ChartConfig
};

