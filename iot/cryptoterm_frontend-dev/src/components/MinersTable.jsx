// src/components/MinersTable.jsx
import React, { useState, useEffect } from 'react';
import { auth } from '../auth';
import { api } from '../api';

export default function MinersTable({ devices }) {
  const [metricsData, setMetricsData] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (devices && devices.length > 0) {
      loadMetricsForDevices();
    }
  }, [devices]);

  const loadMetricsForDevices = async () => {
    setLoading(true);
    const token = auth.getToken();
    if (!token) return;

    const to = new Date();
    const from = new Date(to.getTime() - 5 * 60 * 1000); // Последние 5 минут
    
    const metricsPromises = devices.map(async (device) => {
      try {
        // Загружаем хэшрейт и температуру для каждого устройства
        const [hashRate, temperature] = await Promise.all([
          api.getHashRateByDevice(token, device.id, from, to, '1 minute').catch(() => []),
          api.getTemperatureByDevice(token, device.id, from, to, '1 minute').catch(() => [])
        ]);

        // Берем последнее значение
        const latestHashRate = hashRate.length > 0 ? hashRate[hashRate.length - 1].avgValue : null;
        const latestTemp = temperature.length > 0 ? temperature[temperature.length - 1].avgValue : null;

        return {
          deviceId: device.id,
          hashRate: latestHashRate,
          temperature: latestTemp
        };
      } catch (error) {
        console.error(`Failed to load metrics for device ${device.id}:`, error);
        return {
          deviceId: device.id,
          hashRate: null,
          temperature: null
        };
      }
    });

    const results = await Promise.all(metricsPromises);
    
    // Преобразуем в объект для быстрого доступа
    const metricsMap = {};
    results.forEach(result => {
      metricsMap[result.deviceId] = result;
    });
    
    setMetricsData(metricsMap);
    setLoading(false);
  };

  const getStatusColor = (temperature) => {
    if (!temperature) return 'status-unknown';
    if (temperature < 70) return 'status-active';
    if (temperature < 85) return 'status-warning';
    return 'status-error';
  };

  return (
    <div className="miners-table-container">
      {loading && <p style={{ textAlign: 'center', color: '#666' }}>Загрузка метрик...</p>}
      
      <table className="miners-table">
        <thead>
          <tr>
            <th>Device / Miner</th>
            <th>Model</th>
            <th>Vendor</th>
            <th>Mode</th>
            <th>TH/s</th>
            <th>t°C</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {devices.map(device => {
            const metrics = metricsData[device.id] || {};
            const deviceMiners = device.miners || [];
            
            return (
              <React.Fragment key={device.id}>
                <tr className="device-row">
                  <td colSpan="7" style={{ backgroundColor: '#1a1a1a', fontWeight: 'bold', padding: '8px' }}>
                    {device.name || `Device ${device.id.substring(0, 8)}`}
                  </td>
                </tr>
                {deviceMiners.map(miner => (
                  <tr key={miner.id}>
                    <td>
                      <div className="miner-name-cell">
                        <input type="checkbox" />
                        <span style={{ marginLeft: '20px' }}>└ {miner.label || 'Unknown Miner'}</span>
                      </div>
                    </td>
                    <td>{miner.model || 'N/A'}</td>
                    <td>{miner.vendor || 'N/A'}</td>
                    <td>
                      <span style={{ 
                        padding: '2px 8px', 
                        borderRadius: '4px', 
                        backgroundColor: miner.mode === 'OVERCLOCK' ? '#f44' : miner.mode === 'ECO' ? '#4f4' : '#5865f2',
                        fontSize: '11px',
                        fontWeight: 'bold'
                      }}>
                        {miner.mode || 'STANDARD'}
                      </span>
                    </td>
                    <td>{metrics.hashRate ? metrics.hashRate.toFixed(2) : '-'}</td>
                    <td>{metrics.temperature ? metrics.temperature.toFixed(1) : '-'}</td>
                    <td>
                      <div className={`status-indicator ${getStatusColor(metrics.temperature)}`} />
                    </td>
                  </tr>
                ))}
                {deviceMiners.length === 0 && (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', color: '#666', fontStyle: 'italic' }}>
                      No miners found for this device
                    </td>
                  </tr>
                )}
              </React.Fragment>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
