// src/components/Dashboard.jsx
import React, { useState, useEffect } from 'react';
import { auth } from '../auth';
import { api } from '../api';
import HashRateChart from './HashRateChart';
import SensorChart from './SensorChart';
import MinersTable from './MinersTable';
import MinerCard from './MinerCard';
import ProfitCalculator from './ProfitCalculator';
import ConsoleLogs from './ConsoleLogs';
import CommandTemplateManager from './CommandTemplateManager';
import '../dashboard.css';

export default function Dashboard() {
  const [username, setUsername] = useState('User');
  const [userId, setUserId] = useState(null);
  const [selectedView, setSelectedView] = useState('hardware'); // 'hardware', 'table', 'templates'
  const [devices, setDevices] = useState([]);
  const [selectedDevice, setSelectedDevice] = useState(null);
  const [chartMetric, setChartMetric] = useState('hashrate');
  const [sensorMetric, setSensorMetric] = useState(null);
  const [sensors, setSensors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Получаем userId из токена
    const userIdFromToken = auth.getUserId();
    if (userIdFromToken) {
      setUserId(userIdFromToken);
      
      // Извлекаем username из токена
      const usernameFromToken = auth.getUsernameFromToken();
      if (usernameFromToken && usernameFromToken !== 'anonymous') {
        setUsername(usernameFromToken);
      } else {
        // Если нет username, показываем email или первые 8 символов userId
        const emailFromToken = auth.getEmailFromToken();
        if (emailFromToken) {
          setUsername(emailFromToken.split('@')[0]);
        } else {
          setUsername(userIdFromToken.substring(0, 8));
        }
      }
    }

    // Загружаем устройства и майнеры
    if (userIdFromToken) {
      loadDevices(userIdFromToken);
    }
  }, []);

  const loadDevices = async (uid) => {
    try {
      setLoading(true);
      setError(null);
      
      const token = auth.getToken();
      if (!token) {
        console.error('No token available');
        setError('Не авторизован');
        setLoading(false);
        return;
      }
      
      // Загружаем устройства с майнерами
      const data = await api.getUserDevices(token, uid);
      
      if (data && data.devices) {
        setDevices(data.devices);
      }
      
      setLoading(false);
    } catch (error) {
      console.error('Failed to load devices:', error);
      setError(error.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    if (devices.length > 0) {
      const token = auth.getToken();
      if (!token) return;
      
      Promise.all(
        devices.map(d => api.getOtherMetricSensors(token, d.id).catch(() => []))
      ).then(results => {
        const allSensors = results.flat();
        const unique = [];
        const seen = new Set();
        allSensors.forEach(s => {
          const key = `${s.metricName}::${s.sensorKey}`;
          if (!seen.has(key)) {
            seen.add(key);
            unique.push(s);
          }
        });
        setSensors(unique);
      });
    }
  }, [devices]);

  const handleLogout = () => {
    auth.removeToken();
    window.location.href = '/admin/login';
  };

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>Hey, {username}!</h1>
        <button className="profile-btn" onClick={handleLogout}>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M12 12C14.21 12 16 10.21 16 8C16 5.79 14.21 4 12 4C9.79 4 8 5.79 8 8C8 10.21 9.79 12 12 12ZM12 14C9.33 14 4 15.34 4 18V20H20V18C20 15.34 14.67 14 12 14Z" fill="currentColor"/>
          </svg>
        </button>
      </header>

      {userId && !sensorMetric && <HashRateChart userId={userId} metricType={chartMetric} />}
      {userId && sensorMetric && (
        <SensorChart 
          userId={userId}
          devices={devices}
          metricName={sensorMetric.metricName}
          sensorKey={sensorMetric.sensorKey}
          displayName={sensorMetric.displayName || sensorMetric.metricName}
          unit={sensorMetric.unit}
        />
      )}

      {/* Metric toggle buttons */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        gap: '8px', 
        marginTop: '-20px',
        marginBottom: '20px',
        flexWrap: 'wrap'
      }}>
        {[
          { key: 'hashrate', label: 'Hash Rate' },
          { key: 'temperature', label: 'Temperature' },
          { key: 'power', label: 'Power' },
        ].map(m => (
          <button
            key={m.key}
            onClick={() => { setChartMetric(m.key); setSensorMetric(null); }}
            style={{
              padding: '8px 20px',
              borderRadius: '8px',
              border: 'none',
              background: chartMetric === m.key && !sensorMetric ? '#5865f2' : '#2a2a2a',
              color: '#fff',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: chartMetric === m.key && !sensorMetric ? 'bold' : 'normal',
              transition: 'all 0.2s'
            }}
          >
            {m.label}
          </button>
        ))}
        {sensors.length > 0 && sensors.map(s => (
          <button
            key={`${s.metricName}::${s.sensorKey}`}
            onClick={() => setSensorMetric(s)}
            style={{
              padding: '8px 20px',
              borderRadius: '8px',
              border: sensorMetric?.sensorKey === s.sensorKey && sensorMetric?.metricName === s.metricName 
                ? '1px solid #5865f2' : '1px solid #333',
              background: sensorMetric?.sensorKey === s.sensorKey && sensorMetric?.metricName === s.metricName 
                ? '#5865f2' : '#1e1e1e',
              color: '#fff',
              cursor: 'pointer',
              fontSize: '13px',
              fontWeight: sensorMetric?.sensorKey === s.sensorKey && sensorMetric?.metricName === s.metricName 
                ? 'bold' : 'normal',
              transition: 'all 0.2s'
            }}
          >
            {s.displayName || s.metricName} — {s.sensorKey.length > 12 ? s.sensorKey.substring(0, 12) + '...' : s.sensorKey} ({s.unit})
          </button>
        ))}
      </div>

      {/* Tabs для переключения видов */}
      <div className="view-tabs">
        <button 
          className={selectedView === 'hardware' ? 'active' : ''}
          onClick={() => setSelectedView('hardware')}
        >
          {devices.length} hardware{devices.length !== 1 ? 's' : ''}
        </button>
        <button 
          className={selectedView === 'table' ? 'active' : ''}
          onClick={() => setSelectedView('table')}
        >
          Mining
        </button>
        <button 
          className={selectedView === 'templates' ? 'active' : ''}
          onClick={() => setSelectedView('templates')}
        >
          🔧 Команды
        </button>
      </div>

      {/* Секция майнеров */}
      <section className="miners-section">
        <h3>
          {selectedView === 'templates' ? 'Шаблоны команд' : 'Hardwares'}
        </h3>
        
        {loading && selectedView !== 'templates' && <p>Загрузка...</p>}
        {error && selectedView !== 'templates' && <p style={{ color: 'red' }}>Ошибка: {error}</p>}
        
        {selectedView === 'templates' ? (
          <CommandTemplateManager />
        ) : !loading && !error && (
          <>
            {selectedView === 'table' ? (
              <MinersTable devices={devices} />
            ) : (
              <div className="miners-list">
                {devices.map(device => (
                  <MinerCard 
                    key={device.id}
                    device={device}
                    isExpanded={selectedDevice === device.id}
                    onToggle={() => setSelectedDevice(
                      selectedDevice === device.id ? null : device.id
                    )}
                    onUpdate={() => loadDevices(userId)}
                    onSelectMiners={(minerIds) => {
                      console.log('Selected miners:', minerIds);
                      // Можно добавить дополнительную логику
                    }}
                  />
                ))}
              </div>
            )}
          </>
        )}
      </section>

      {selectedView === 'hardware' && userId && (
        <ProfitCalculator userId={userId} />
      )}
      
      {/* Developer Logs Panel */}
      {devices.length > 0 && (
        <section className="logs-section" style={{ marginTop: '40px' }}>
          <h3>Device Logs (Developer Panel)</h3>
          <div className="logs-tabs" style={{ display: 'flex', gap: '8px', marginBottom: '16px', flexWrap: 'wrap' }}>
            {devices.map(device => (
              <button
                key={device.id}
                className={`tab-btn ${selectedDevice === device.id ? 'active' : ''}`}
                onClick={() => setSelectedDevice(device.id)}
                style={{
                  padding: '8px 16px',
                  borderRadius: '8px',
                  background: selectedDevice === device.id ? '#5865f2' : '#2a2a2a',
                  border: 'none',
                  color: '#fff',
                  cursor: 'pointer',
                  fontSize: '14px'
                }}
              >
                {device.name || `Device ${device.id.substring(0, 8)}`}
              </button>
            ))}
          </div>
          
          {selectedDevice && (
            <ConsoleLogs 
              token={auth.getToken()} 
              deviceId={selectedDevice} 
            />
          )}
        </section>
      )}
    </div>
  );
}
