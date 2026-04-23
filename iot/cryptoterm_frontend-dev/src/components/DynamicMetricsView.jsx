// src/components/DynamicMetricsView.jsx

import React, { useState, useEffect, useCallback } from 'react';
import { api } from '../api';
import useAutoRefresh from '../useAutoRefresh';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts';

const getTimeRange = (rangeKey) => {
    const now = new Date();
    let durationMs = 5 * 60 * 1000; // Default: 5 minutes

    if (rangeKey === '15min') durationMs = 15 * 60 * 1000;
    else if (rangeKey === '1h') durationMs = 60 * 60 * 1000;
    else if (rangeKey === '6h') durationMs = 6 * 60 * 60 * 1000;
    else if (rangeKey === '1d') durationMs = 24 * 60 * 60 * 1000;

    const from = new Date(now.getTime() - durationMs);
    return { from, to: now };
};

const METRIC_COLORS = [
    '#8884d8', // blue
    '#82ca9d', // green
    '#ffc658', // yellow
    '#ff7c7c', // red
    '#a28aff', // purple
    '#ff8ab8', // pink
    '#51d9c4', // cyan
    '#ffaa51', // orange
];

export default function DynamicMetricsView({ token, deviceId }) {
    const [range, setRange] = useState('15min');
    const [metricTypes, setMetricTypes] = useState([]);
    const [metricsData, setMetricsData] = useState({});
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [selectedMetrics, setSelectedMetrics] = useState([]);

    const [customFrom, setCustomFrom] = useState('');
    const [customTo, setCustomTo] = useState('');
    const [isCustomRange, setIsCustomRange] = useState(false);

    // Загрузка типов метрик при монтировании
    useEffect(() => {
        const loadMetricTypes = async () => {
            try {
                const types = await api.getMetricTypes(token);
                setMetricTypes(types || []);
                // По умолчанию выбираем первые 2 метрики (если есть)
                if (types && types.length > 0) {
                    setSelectedMetrics(types.slice(0, Math.min(2, types.length)).map(t => t.name));
                }
            } catch (err) {
                console.error('Failed to load metric types:', err);
                setError('Failed to load metric types');
            }
        };
        
        if (token) {
            loadMetricTypes();
        }
    }, [token]);

    const fetchData = useCallback(async () => {
        if (!deviceId || selectedMetrics.length === 0) return;

        setLoading(true);
        setError(null);
        try {
            let from, to;

            if (isCustomRange && customFrom && customTo) {
                from = new Date(customFrom);
                to = new Date(customTo);
            } else {
                const rangeDates = getTimeRange(range);
                from = rangeDates.from;
                to = rangeDates.to;
            }

            // Загружаем данные для каждой выбранной метрики
            const promises = selectedMetrics.map(metricName =>
                api.getOtherMetrics(token, metricName, deviceId, from, to)
                    .then(data => ({ metricName, data }))
                    .catch(err => {
                        console.error(`Failed to fetch ${metricName}:`, err);
                        return { metricName, data: [] };
                    })
            );

            const results = await Promise.all(promises);
            
            // Преобразуем результаты в объект
            const newMetricsData = {};
            results.forEach(({ metricName, data }) => {
                newMetricsData[metricName] = data;
            });
            
            setMetricsData(newMetricsData);
        } catch (err) {
            setError(err.message || 'Failed to fetch metrics.');
        } finally {
            setLoading(false);
        }
    }, [deviceId, range, token, isCustomRange, customFrom, customTo, selectedMetrics]);

    // Автообновление каждые 10 минут (600 000 мс)
    useAutoRefresh(fetchData, 600000, [fetchData]);

    const formatChartData = (metricName) => {
        const data = metricsData[metricName];
        if (!data || data.length === 0) return [];

        return data.map(item => {
            const date = new Date(item.time);
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            return {
                name: hours + ':' + minutes,
                value: item.avg || item.value || 0,
            };
        });
    };

    const getMetricUnit = (metricName) => {
        const metricType = metricTypes.find(t => t.name === metricName);
        return metricType?.unit || '';
    };

    const MetricChart = ({ metricName, color }) => {
        const data = formatChartData(metricName);
        const unit = getMetricUnit(metricName);

        return (
            <div style={{ marginBottom: '30px' }}>
                <h4 style={{ color: color, marginBottom: '10px' }}>{metricName}</h4>
                <ResponsiveContainer width="100%" height={250}>
                    <LineChart data={data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#333" />
                        <XAxis dataKey="name" stroke="#999" />
                        <YAxis unit={unit ? ` ${unit}` : ''} stroke={color} />
                        <Tooltip 
                            contentStyle={{ 
                                backgroundColor: '#1a1a1a', 
                                border: '1px solid #333',
                                borderRadius: '8px' 
                            }}
                        />
                        <Legend />
                        <Line
                            type="monotone"
                            dataKey="value"
                            name={metricName}
                            stroke={color}
                            activeDot={{ r: 8 }}
                            strokeWidth={2}
                        />
                    </LineChart>
                </ResponsiveContainer>
            </div>
        );
    };

    const rangeOptions = [
        { key: '5min', label: '5 min' },
        { key: '15min', label: '15 min' },
        { key: '1h', label: '1 hour' },
        { key: '6h', label: '6 hours' },
        { key: '1d', label: '1 day' },
    ];

    const handleRangeSelect = (key) => {
        setRange(key);
        setIsCustomRange(false);
    };

    const handleApplyCustomRange = () => {
        if (customFrom && customTo) {
            setIsCustomRange(true);
        }
    };

    const toggleMetric = (metricName) => {
        setSelectedMetrics(prev => {
            if (prev.includes(metricName)) {
                return prev.filter(m => m !== metricName);
            } else {
                return [...prev, metricName];
            }
        });
    };

    return (
        <div className="metrics-view">
            <div className="metrics-header">
                <h3>📊 Dynamic Metrics</h3>
                
                {/* Выбор метрик */}
                <div className="metric-selector">
                    <h4>Select Metrics:</h4>
                    <div className="metric-checkboxes">
                        {metricTypes.map((metricType, index) => (
                            <label key={metricType.name} className="metric-checkbox">
                                <input
                                    type="checkbox"
                                    checked={selectedMetrics.includes(metricType.name)}
                                    onChange={() => toggleMetric(metricType.name)}
                                />
                                <span style={{ color: METRIC_COLORS[index % METRIC_COLORS.length] }}>
                                    {metricType.name} ({metricType.unit})
                                </span>
                            </label>
                        ))}
                    </div>
                </div>

                {/* Выбор временного диапазона */}
                <div className="time-range-selector">
                    <h4>Time Range:</h4>
                    <div className="range-buttons">
                        {rangeOptions.map(option => (
                            <button
                                key={option.key}
                                onClick={() => handleRangeSelect(option.key)}
                                className={`range-button ${range === option.key && !isCustomRange ? 'active' : ''}`}
                            >
                                {option.label}
                            </button>
                        ))}
                    </div>
                </div>

                {/* Кастомный диапазон */}
                <div className="custom-range">
                    <h4>Custom Range:</h4>
                    <div className="custom-inputs">
                        <label>
                            From:
                            <input
                                type="datetime-local"
                                value={customFrom}
                                onChange={e => setCustomFrom(e.target.value)}
                                step="1"
                            />
                        </label>
                        <label>
                            To:
                            <input
                                type="datetime-local"
                                value={customTo}
                                onChange={e => setCustomTo(e.target.value)}
                                step="1"
                            />
                        </label>
                        <button
                            onClick={handleApplyCustomRange}
                            disabled={!customFrom || !customTo || loading}
                            className="apply-button"
                        >
                            Apply
                        </button>
                    </div>
                </div>
            </div>

            {loading && <p className="loading-message">Loading metrics...</p>}
            {error && <p className="error-message">Error: {error}</p>}

            {selectedMetrics.length === 0 && !loading && (
                <p className="info-message">Please select at least one metric to display.</p>
            )}

            <div className="metrics-charts">
                {selectedMetrics.map((metricName, index) => (
                    <MetricChart
                        key={metricName}
                        metricName={metricName}
                        color={METRIC_COLORS[index % METRIC_COLORS.length]}
                    />
                ))}
            </div>
        </div>
    );
}

