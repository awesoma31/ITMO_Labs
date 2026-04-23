// src/components/ConsoleLogs.jsx

import React, { useState, useEffect, useCallback } from 'react'; 
import { api } from '../api';

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

const LogLine = ({ log, index }) => {
    const { time, level, message } = log; 
    let color = 'white';
    
    if (level === 'ERROR' || level === 'FATAL') color = '#ff6b6b'; 
    else if (level === 'WARN') color = '#ffd43b'; 
    else if (level === 'INFO') color = '#82ca9d'; 
    else if (level === 'DEBUG') color = '#7c6dd8'; 

    const date = new Date(time);
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    const displayTime = hours + ':' + minutes + ':' + seconds;
    const lineNumber = String(index + 1).padStart(4, '0');
    
    return (
        <div style={{ 
            display: 'grid', 
            gridTemplateColumns: '50px 110px 100px 1fr', 
            gap: '12px',
            color: color, 
            fontFamily: 'monospace', 
            whiteSpace: 'pre-wrap', 
            lineHeight: '1.6',
            padding: '8px 0',
            borderBottom: '1px solid #2a2a2a'
        }}>
            <span style={{ color: '#666', textAlign: 'right' }}>{lineNumber}</span>
            <span style={{ color: '#999' }}>{displayTime}</span>
            <span style={{ fontWeight: 'bold' }}>{level}</span>
            <span>{message}</span>
        </div>
    );
};


export default function ConsoleLogs({ token, deviceId }) {
    const [range, setRange] = useState('15min');
    const [logs, setLogs] = useState([]);
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

            const data = await api.getMinerLogs(token, deviceId, from, to); 
            setLogs(data || []); 
        } catch (err) {
            setError(err.message || 'Failed to fetch logs.');
        } finally {
            setLoading(false);
        }
    }, [deviceId, range, token, isCustomRange, customFrom, customTo]);

    useEffect(() => {
        fetchData();
    }, [fetchData]); 
    
    
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
                    <button 
                        onClick={fetchData} 
                        disabled={loading}
                        className="range-button"
                    >
                        {loading ? '⏳' : '🔄 Refresh'}
                    </button>
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
            
            <div className="console-logs" style={{ maxHeight: '500px', marginTop: '24px' }}>
                <div style={{ 
                    display: 'grid', 
                    gridTemplateColumns: '50px 110px 100px 1fr', 
                    gap: '12px', 
                    color: '#999', 
                    fontFamily: 'monospace', 
                    fontWeight: 'bold', 
                    marginBottom: '12px',
                    borderBottom: '2px solid #333',
                    paddingBottom: '8px'
                }}>
                    <span>#</span>
                    <span>TIME</span>
                    <span>LEVEL</span>
                    <span>MESSAGE</span>
                </div>

                {loading && <div className="loading-message">Loading logs...</div>}
                {error && <div className="error-message">Error: {error}</div>}

                {!loading && logs.length > 0 ? (
                    logs.map((logObject, index) => (
                        <LogLine key={index} log={logObject} index={index} />
                    ))
                ) : (
                    !loading && !error && <div className="info-message">No logs found for selected period.</div>
                )}
            </div>
        </div>
    );
}
