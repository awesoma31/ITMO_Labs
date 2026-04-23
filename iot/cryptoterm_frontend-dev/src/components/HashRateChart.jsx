// src/components/HashRateChart.jsx
import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, ResponsiveContainer, Tooltip, Legend } from 'recharts';
import { auth } from '../auth';
import { api } from '../api';

const COLORS = ['#5865f2', '#f7931a', '#4CAF50', '#FF5722', '#9C27B0', '#00BCD4', '#FFEB3B'];

export default function HashRateChart({ userId, metricType = 'hashrate' }) {
  const [timeRange, setTimeRange] = useState('Day');
  const [chartData, setChartData] = useState([]);
  const [devices, setDevices] = useState([]);
  const [visibleDevices, setVisibleDevices] = useState({});
  const [currentDate, setCurrentDate] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const metricConfig = {
    hashrate: {
      title: 'Hash rate',
      unit: 'TH/s',
      color: '#5865f2'
    },
    temperature: {
      title: 'Temperature',
      unit: '°C',
      color: '#f7931a'
    },
    power: {
      title: 'Power consumption',
      unit: 'W',
      color: '#4CAF50'
    }
  };
  
  const config = metricConfig[metricType] || metricConfig.hashrate;

  useEffect(() => {
    // Устанавливаем текущую дату
    const now = new Date();
    const formatted = now.toLocaleDateString('en-GB', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    }).replace(/\//g, '.');
    setCurrentDate(formatted);

    // Загружаем устройства
    if (userId) {
      loadDevices();
    }
  }, [userId]);

  useEffect(() => {
    // Загружаем данные при изменении timeRange, devices или metricType
    if (devices.length > 0) {
      loadMetricData(timeRange);
    }
  }, [timeRange, devices, metricType]);

  const getTimeRangeParams = (range) => {
    const to = new Date();
    let from = new Date();
    let bucket = '5 minutes';

    switch (range) {
      case 'Day':
        from.setDate(to.getDate() - 1);
        bucket = '10 minutes';
        break;
      case 'Week':
        from.setDate(to.getDate() - 7);
        bucket = '1 hour';
        break;
      case 'Month':
        from.setMonth(to.getMonth() - 1);
        bucket = '6 hours';
        break;
      case 'Year':
        from.setFullYear(to.getFullYear() - 1);
        bucket = '1 day';
        break;
      default:
        from.setDate(to.getDate() - 1);
    }

    return { from, to, bucket };
  };

  const loadDevices = async () => {
    try {
      const token = auth.getToken();
      if (!token) return;
      
      const data = await api.getUserDevices(token, userId);
      if (data && data.devices) {
        setDevices(data.devices);
        
        // Инициализируем все устройства как видимые
        const visible = {};
        data.devices.forEach(device => {
          visible[device.id] = true;
        });
        setVisibleDevices(visible);
      }
    } catch (error) {
      console.error('Failed to load devices:', error);
    }
  };

  const loadMetricData = async (range) => {
    try {
      setLoading(true);
      setError(null);
      
      const token = auth.getToken();
      if (!token) {
        console.error('No token available');
        return;
      }

      const { from, to, bucket } = getTimeRangeParams(range);
      
      // Загружаем данные для каждого устройства в зависимости от типа метрики
      const deviceDataPromises = devices.map(async (device) => {
        try {
          let data;
          if (metricType === 'temperature') {
            data = await api.getTemperatureByDevice(token, device.id, from, to, bucket);
          } else if (metricType === 'power') {
            data = await api.getPowerConsumptionByDevice(token, device.id, from, to, bucket);
          } else {
            data = await api.getHashRateByDevice(token, device.id, from, to, bucket);
          }
          
          return {
            deviceId: device.id,
            deviceName: device.name || `Device ${device.id.substring(0, 8)}`,
            data: data
          };
        } catch (error) {
          console.error(`Failed to load data for device ${device.id}:`, error);
          return {
            deviceId: device.id,
            deviceName: device.name || `Device ${device.id.substring(0, 8)}`,
            data: []
          };
        }
      });

      const devicesData = await Promise.all(deviceDataPromises);
      
      // Объединяем данные по timestamp
      const dataByTimestamp = {};
      
      devicesData.forEach(({ deviceId, deviceName, data }) => {
        data.forEach(point => {
          // Бэкенд может возвращать либо avgValue, либо value
          const value = point.avgValue || point.value || 0;
          const timestamp = new Date(point.timestamp || point.time).getTime();
          
          if (!dataByTimestamp[timestamp]) {
            const date = new Date(timestamp);
            let label;
            
            if (range === 'Day') {
              const hours = String(date.getHours()).padStart(2, '0');
              const minutes = String(date.getMinutes()).padStart(2, '0');
              label = hours + ':' + minutes;
            } else if (range === 'Week') {
              const days = ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'];
              label = days[date.getDay()];
            } else if (range === 'Month') {
              label = date.getDate().toString();
            } else {
              label = date.toLocaleDateString('en-US', { month: 'short' });
            }
            
            dataByTimestamp[timestamp] = {
              name: label,
              timestamp: timestamp
            };
          }
          
          // Конвертируем значения в зависимости от типа метрики
          let displayValue = value;
          if (metricType === 'hashrate' && value > 10000) {
            // Конвертация GH/s в TH/s
            displayValue = value / 1000;
          }
          
          dataByTimestamp[timestamp][deviceId] = parseFloat(displayValue.toFixed(2));
        });
      });

      // Преобразуем в массив и сортируем по времени
      const formattedData = Object.values(dataByTimestamp).sort((a, b) => a.timestamp - b.timestamp);

      setChartData(formattedData);
      setLoading(false);
    } catch (error) {
      console.error('Failed to load metric data:', error);
      setError(error.message);
      setLoading(false);
      setChartData([]);
    }
  };

  const handleLegendClick = (deviceId) => {
    setVisibleDevices(prev => ({
      ...prev,
      [deviceId]: !prev[deviceId]
    }));
  };

  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      return (
        <div style={{
          backgroundColor: '#1a1a1a',
          padding: '8px 12px',
          border: '1px solid #333',
          borderRadius: '4px'
        }}>
          <p style={{ margin: '0 0 4px 0', color: '#fff', fontSize: '12px' }}>
            {payload[0].payload.name}
          </p>
          {payload.map((entry, index) => (
            <p key={index} style={{ margin: 0, color: entry.color, fontSize: '12px', fontWeight: 'bold' }}>
              {entry.name}: {entry.value.toFixed(2)} {config.unit}
            </p>
          ))}
        </div>
      );
    }
    return null;
  };

  const CustomLegend = ({ payload }) => {
    return (
      <div style={{
        display: 'flex',
        flexWrap: 'wrap',
        gap: '12px',
        justifyContent: 'center',
        marginTop: '12px'
      }}>
        {payload.map((entry, index) => (
          <div
            key={`legend-${index}`}
            onClick={() => handleLegendClick(entry.dataKey)}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              cursor: 'pointer',
              opacity: visibleDevices[entry.dataKey] ? 1 : 0.3,
              transition: 'opacity 0.2s'
            }}
          >
            <div style={{
              width: '12px',
              height: '12px',
              backgroundColor: entry.color,
              borderRadius: '2px'
            }} />
            <span style={{ fontSize: '12px', color: '#fff' }}>
              {entry.value}
            </span>
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className="hash-rate-chart">
      <div className="chart-header">
        <h3>{config.title} ›</h3>
        <span className="chart-date">{currentDate}</span>
        <button className="expand-btn">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M3 8L8 3M8 3V8M8 3H3M17 12L12 17M12 17V12M12 17H17" 
                  stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          </svg>
        </button>
      </div>

      <div className="chart-container">
        {loading && <p style={{ textAlign: 'center', color: '#666' }}>Загрузка...</p>}
        {error && <p style={{ textAlign: 'center', color: '#f44' }}>Ошибка: {error}</p>}
        
        {!loading && !error && chartData.length > 0 && (
          <ResponsiveContainer width="100%" height={250}>
            <LineChart data={chartData} margin={{ top: 5, right: 10, left: -20, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#2a2a2a" />
              <XAxis 
                dataKey="name" 
                stroke="#666" 
                tick={{ fill: '#666', fontSize: 12 }}
              />
              <YAxis 
                stroke="#666" 
                tick={{ fill: '#666', fontSize: 12 }}
                domain={[0, 'auto']}
                label={{ value: config.unit, angle: -90, position: 'insideLeft', fill: '#666', fontSize: 12 }}
              />
              <Tooltip content={<CustomTooltip />} />
              <Legend content={<CustomLegend />} />
              {devices.map((device, index) => (
                visibleDevices[device.id] && (
                  <Line 
                    key={device.id}
                    type="monotone" 
                    dataKey={device.id}
                    name={device.name || `Device ${device.id.substring(0, 8)}`}
                    stroke={COLORS[index % COLORS.length]}
                    strokeWidth={2}
                    dot={false}
                    connectNulls
                  />
                )
              ))}
            </LineChart>
          </ResponsiveContainer>
        )}
        
        {!loading && !error && chartData.length === 0 && (
          <p style={{ textAlign: 'center', color: '#666', padding: '40px 0' }}>
            No data available for the selected period
          </p>
        )}
      </div>

      <div className="time-range-filters">
        {['Day', 'Week', 'Month', 'Year'].map(range => (
          <button
            key={range}
            className={timeRange === range ? 'active' : ''}
            onClick={() => setTimeRange(range)}
          >
            {range}
          </button>
        ))}
      </div>
    </div>
  );
}
