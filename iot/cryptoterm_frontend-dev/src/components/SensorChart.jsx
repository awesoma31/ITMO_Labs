import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, ResponsiveContainer, Tooltip, Legend } from 'recharts';
import { auth } from '../auth';
import { api } from '../api';

const COLORS = ['#a28aff', '#51d9c4', '#ff8ab8', '#ffaa51', '#5865f2', '#82ca9d', '#ffc658'];

export default function SensorChart({ userId, devices, metricName, sensorKey, displayName, unit }) {
  const [timeRange, setTimeRange] = useState('Day');
  const [chartData, setChartData] = useState([]);
  const [visibleDevices, setVisibleDevices] = useState({});
  const [currentDate, setCurrentDate] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const now = new Date();
    const formatted = now.toLocaleDateString('en-GB', {
      day: '2-digit', month: '2-digit', year: 'numeric'
    }).replace(/\//g, '.');
    setCurrentDate(formatted);
  }, []);

  useEffect(() => {
    if (devices.length > 0) {
      const visible = {};
      devices.forEach(d => { visible[d.id] = true; });
      setVisibleDevices(visible);
    }
  }, [devices]);

  useEffect(() => {
    if (devices.length > 0) {
      loadData(timeRange);
    }
  }, [timeRange, devices, metricName, sensorKey]);

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

  const loadData = async (range) => {
    try {
      setLoading(true);
      setError(null);

      const token = auth.getToken();
      if (!token) return;

      const { from, to, bucket } = getTimeRangeParams(range);

      const deviceDataPromises = devices.map(async (device) => {
        try {
          const rawData = await api.getOtherMetricByDevice(token, metricName, device.id, from, to, bucket);
          const filtered = (rawData || []).filter(p => p.sensorKey === sensorKey);
          return {
            deviceId: device.id,
            deviceName: device.name || `Device ${device.id.substring(0, 8)}`,
            data: filtered
          };
        } catch (err) {
          console.error(`Failed to load other metric for device ${device.id}:`, err);
          return { deviceId: device.id, deviceName: device.name || `Device ${device.id.substring(0, 8)}`, data: [] };
        }
      });

      const devicesData = await Promise.all(deviceDataPromises);

      const dataByTimestamp = {};

      devicesData.forEach(({ deviceId, data }) => {
        data.forEach(point => {
          const value = point.avg || point.avgValue || point.value || 0;
          const timestamp = new Date(point.timestamp || point.time).getTime();

          if (!dataByTimestamp[timestamp]) {
            const date = new Date(timestamp);
            let label;

            if (range === 'Day') {
              label = String(date.getHours()).padStart(2, '0') + ':' + String(date.getMinutes()).padStart(2, '0');
            } else if (range === 'Week') {
              const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
              label = days[date.getDay()];
            } else if (range === 'Month') {
              label = date.getDate().toString();
            } else {
              label = date.toLocaleDateString('en-US', { month: 'short' });
            }

            dataByTimestamp[timestamp] = { name: label, timestamp };
          }

          dataByTimestamp[timestamp][deviceId] = parseFloat(Number(value).toFixed(2));
        });
      });

      const formattedData = Object.values(dataByTimestamp).sort((a, b) => a.timestamp - b.timestamp);
      setChartData(formattedData);
      setLoading(false);
    } catch (err) {
      console.error('Failed to load sensor data:', err);
      setError(err.message);
      setLoading(false);
      setChartData([]);
    }
  };

  const handleLegendClick = (deviceId) => {
    setVisibleDevices(prev => ({ ...prev, [deviceId]: !prev[deviceId] }));
  };

  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      return (
        <div style={{
          backgroundColor: '#1a1a1a', padding: '8px 12px',
          border: '1px solid #333', borderRadius: '4px'
        }}>
          <p style={{ margin: '0 0 4px 0', color: '#fff', fontSize: '12px' }}>
            {payload[0].payload.name}
          </p>
          {payload.map((entry, index) => (
            <p key={index} style={{ margin: 0, color: entry.color, fontSize: '12px', fontWeight: 'bold' }}>
              {entry.name}: {entry.value} {unit}
            </p>
          ))}
        </div>
      );
    }
    return null;
  };

  const CustomLegend = ({ payload }) => (
    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '12px', justifyContent: 'center', marginTop: '12px' }}>
      {payload.map((entry, index) => (
        <div
          key={`legend-${index}`}
          onClick={() => handleLegendClick(entry.dataKey)}
          style={{
            display: 'flex', alignItems: 'center', gap: '6px',
            cursor: 'pointer', opacity: visibleDevices[entry.dataKey] ? 1 : 0.3,
            transition: 'opacity 0.2s'
          }}
        >
          <div style={{ width: '12px', height: '12px', backgroundColor: entry.color, borderRadius: '2px' }} />
          <span style={{ fontSize: '12px', color: '#fff' }}>{entry.value}</span>
        </div>
      ))}
    </div>
  );

  return (
    <div className="hash-rate-chart">
      <div className="chart-header">
        <h3>{displayName} ({sensorKey.length > 16 ? sensorKey.substring(0, 16) + '...' : sensorKey}) ›</h3>
        <span className="chart-date">{currentDate}</span>
      </div>

      <div className="chart-container">
        {loading && <p style={{ textAlign: 'center', color: '#666' }}>Loading...</p>}
        {error && <p style={{ textAlign: 'center', color: '#f44' }}>Error: {error}</p>}

        {!loading && !error && chartData.length > 0 && (
          <ResponsiveContainer width="100%" height={250}>
            <LineChart data={chartData} margin={{ top: 5, right: 10, left: -20, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#2a2a2a" />
              <XAxis dataKey="name" stroke="#666" tick={{ fill: '#666', fontSize: 12 }} />
              <YAxis
                stroke="#666"
                tick={{ fill: '#666', fontSize: 12 }}
                domain={[0, 'auto']}
                label={{ value: unit, angle: -90, position: 'insideLeft', fill: '#666', fontSize: 12 }}
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
        {['Day', 'Week', 'Month', 'Year'].map(r => (
          <button
            key={r}
            className={timeRange === r ? 'active' : ''}
            onClick={() => setTimeRange(r)}
          >
            {r}
          </button>
        ))}
      </div>
    </div>
  );
}
