// src/components/MetricsView.jsx

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

export default function MetricsView({ token, deviceId }) {
    const [range, setRange] = useState('15min');
    const [metrics, setMetrics] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    
    const [customFrom, setCustomFrom] = useState('');
    const [customTo, setCustomTo] = useState('');
    const [isCustomRange, setIsCustomRange] = useState(false);

    const fetchData = useCallback(async () => {
        if (!deviceId) return;

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

            const data = await api.getMinerMetrics(token, deviceId, from, to); 
            setMetrics(data);
        } catch (err) {
            setError(err.message || 'Failed to fetch metrics.');
        } finally {
            setLoading(false);
        }
    }, [deviceId, range, token, isCustomRange, customFrom, customTo]);

    // Автообновление каждые 10 минут (600 000 мс)
    useAutoRefresh(fetchData, 600000, [fetchData]); 

    
    const formatChartData = (metricsData) => {
        if (!metricsData || metricsData.length === 0) return [];
        
        return metricsData.map(item => {
            const date = new Date(item.time);
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            return {
                name: hours + ':' + minutes,
                temperature: item.avgTemp,
                hashRate: item.avgHashRate,
        }));
    };
    
    const MetricChart = ({ data, dataKey, name, color, yAxisId, unit }) => (
        <div style={{ marginBottom: '30px' }}>
            <h4 style={{ color: color, marginBottom: '10px' }}>{name}</h4>
            <ResponsiveContainer width="100%" height={250}>
                <LineChart data={data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#333" />
                    <XAxis dataKey="name" stroke="#999" />
                    <YAxis yAxisId={yAxisId} unit={unit} stroke={color} />
                    <Tooltip 
                        contentStyle={{ 
                            backgroundColor: '#1a1a1a', 
                            border: '1px solid #333',
                            borderRadius: '8px' 
                        }}
                    />
                    <Legend />
                    <Line 
                        yAxisId={yAxisId} 
                        type="monotone" 
                        dataKey={dataKey} 
                        name={name} 
                        stroke={color} 
                        activeDot={{ r: 8 }} 
                        strokeWidth={2}
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
    
    const chartData = formatChartData(metrics);

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
    }
    
    const handleApplyCustomRange = () => {
        if (customFrom && customTo) {
            setIsCustomRange(true);
        }
    }

    return (
        <div className="metrics-view">
            <div className="metrics-header">
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
            
            {loading && <p className="loading-message">Loading metrics...</p>}
            {error && <p className="error-message">Error: {error}</p>}
            
            {chartData.length > 0 ? (
                <div className="metrics-charts">
                    <MetricChart 
                        data={chartData} 
                        dataKey="temperature" 
                        name="Average Temperature" 
                        color="#8884d8" 
                        yAxisId="temp" 
                        unit="°C"
                    />
                    
                    <MetricChart 
                        data={chartData} 
                        dataKey="hashRate" 
                        name="Average Hash Rate" 
                        color="#82ca9d" 
                        yAxisId="hash" 
                        unit="MH/s"
                    />
                </div>
            ) : (
                !loading && <p className="info-message">Select a time range to view metrics (or no data received).</p>
            )}
        </div>
    );
}
