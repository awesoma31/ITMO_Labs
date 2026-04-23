// src/components/MinerCard.jsx
import React, { useState, useEffect } from 'react';
import { auth } from '../auth';
import { api } from '../api';
import AsicPowerControl from './AsicPowerControl';

export default function MinerCard({ device, isExpanded, onToggle, onUpdate, onSelectMiners }) {
  const [selectedMode, setSelectedMode] = useState('STANDARD');
  const [selectedMiners, setSelectedMiners] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showAsicControl, setShowAsicControl] = useState(false);

  // Первый майнер устройства (для отображения)
  const primaryMiner = device.miners && device.miners.length > 0 ? device.miners[0] : null;
  
  // Синхронизируем selectedMode с текущим режимом майнера
  useEffect(() => {
    if (primaryMiner && primaryMiner.mode) {
      setSelectedMode(primaryMiner.mode);
    }
  }, [primaryMiner]);
  
  const handleCardClick = () => {
    // Выбираем всех майнеров этого устройства
    if (device.miners && device.miners.length > 0) {
      const minerIds = device.miners.map(m => m.id);
      setSelectedMiners(minerIds);
      
      if (onSelectMiners) {
        onSelectMiners(minerIds);
      }
    }
    
    // Открываем/закрываем карточку
    onToggle();
  };

  const handleSetMode = async (mode) => {
    if (!primaryMiner) {
      alert('No miner found for this device');
      return;
    }

    setLoading(true);
    setError(null);
    
    try {
      const token = auth.getToken();
      if (!token) {
        alert('No authentication token available');
        setLoading(false);
        return;
      }
      
      const result = await api.setMinerMode(token, primaryMiner.id, mode);
      
      alert(`Mode changed to ${mode} successfully!`);
      setSelectedMode(mode);
      
      if (onUpdate) {
        onUpdate();
      }
    } catch (error) {
      console.error('Failed to set mode:', error);
      setError(error.message);
      alert(`Failed to set mode: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleRestart = async () => {
    if (!primaryMiner) {
      alert('No miner found for this device');
      return;
    }

    if (!window.confirm('Are you sure you want to restart this miner?')) {
      return;
    }

    setLoading(true);
    setError(null);
    
    try {
      const token = auth.getToken();
      if (!token) {
        alert('No authentication token available');
        setLoading(false);
        return;
      }
      
      const result = await api.restartMiner(token, primaryMiner.id);
      
      alert('Restart command sent successfully!');
      
      if (onUpdate) {
        onUpdate();
      }
    } catch (error) {
      console.error('Failed to restart miner:', error);
      setError(error.message);
      alert(`Failed to restart miner: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handlePauseMining = async () => {
    if (!primaryMiner) {
      alert('No miner found for this device');
      return;
    }

    if (!window.confirm('Are you sure you want to pause mining?')) {
      return;
    }

    setLoading(true);
    setError(null);
    
    try {
      const token = auth.getToken();
      if (!token) {
        alert('No authentication token available');
        setLoading(false);
        return;
      }
      
      const result = await api.pauseMining(token, primaryMiner.id);
      
      alert('Pause mining command sent successfully!');
      
      if (onUpdate) {
        onUpdate();
      }
    } catch (error) {
      console.error('Failed to pause mining:', error);
      setError(error.message);
      alert(`Failed to pause mining: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleContinueMining = async () => {
    if (!primaryMiner) {
      alert('No miner found for this device');
      return;
    }

    if (!window.confirm('Are you sure you want to continue mining?')) {
      return;
    }

    setLoading(true);
    setError(null);
    
    try {
      const token = auth.getToken();
      if (!token) {
        alert('No authentication token available');
        setLoading(false);
        return;
      }
      
      const result = await api.continueMining(token, primaryMiner.id);
      
      alert('Continue mining command sent successfully!');
      
      if (onUpdate) {
        onUpdate();
      }
    } catch (error) {
      console.error('Failed to continue mining:', error);
      setError(error.message);
      alert(`Failed to continue mining: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="miner-card" style={{
      border: selectedMiners.length > 0 ? '2px solid #5865f2' : '1px solid #333',
      transition: 'border 0.2s'
    }}>
      <div className="miner-header" onClick={handleCardClick} style={{ cursor: 'pointer' }}>
        <span className="miner-name">
          {device.name || `Device ${device.id.substring(0, 8)}`}
          {primaryMiner && ` (${primaryMiner.model || 'Unknown'})`}
        </span>
        <span className="arrow">{isExpanded ? '∨' : '›'}</span>
      </div>

      {isExpanded && (
        <div className="miner-content">
          {error && <p style={{ color: '#f44', fontSize: '12px', marginBottom: '8px' }}>{error}</p>}
          
          <div className="profile-categories">
            {['ECO', 'STANDARD', 'OVERCLOCK'].map(mode => (
              <button
                key={mode}
                className={selectedMode === mode ? 'active' : ''}
                onClick={() => setSelectedMode(mode)}
              >
                {mode}
              </button>
            ))}
          </div>

          {primaryMiner && (
            <div className="profile-details">
              <div className="detail-item">
                <label>Label</label>
                <span>{primaryMiner.label || 'Unknown'}</span>
              </div>
              <div className="detail-item">
                <label>Model</label>
                <span>{primaryMiner.model || 'Unknown'}</span>
              </div>
              <div className="detail-item">
                <label>Vendor</label>
                <span>{primaryMiner.vendor || 'Unknown'}</span>
              </div>
              <div className="detail-item">
                <label>Current Mode</label>
                <span>{primaryMiner.mode || 'STANDARD'}</span>
              </div>
              {device.miners && device.miners.length > 1 && (
                <div className="detail-item">
                  <label>Miners</label>
                  <span>{device.miners.length} units</span>
                </div>
              )}
            </div>
          )}

          <div className="profile-actions">
            <button 
              className="start-btn" 
              onClick={() => handleSetMode(selectedMode)}
              disabled={loading}
            >
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <path d="M3 2L13 8L3 14V2Z" fill="currentColor"/>
              </svg>
              {loading ? 'Applying...' : 'Apply Mode'}
            </button>
            <button 
              className="icon-btn"
              onClick={handleRestart}
              disabled={loading}
              title="Restart miner"
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '6px',
                minWidth: '100px',
                height: '40px',
                padding: '0 12px'
              }}
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 12C4 7.58172 7.58172 4 12 4C14.5264 4 16.7792 5.17108 18.2454 7M20 12C20 16.4183 16.4183 20 12 20C9.47362 20 7.22075 18.8289 5.75463 17M18.5 3V7H14.5M5.5 21V17H9.5" 
                      stroke="currentColor" 
                      strokeWidth="2" 
                      strokeLinecap="round" 
                      strokeLinejoin="round"/>
              </svg>
              <span>Restart</span>
            </button>
            <button 
              className="icon-btn"
              onClick={handlePauseMining}
              disabled={loading}
              title="Pause mining"
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '6px',
                minWidth: '90px',
                height: '40px',
                padding: '0 12px'
              }}
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M6 4H10V20H6V4ZM14 4H18V20H14V4Z" 
                      fill="currentColor"/>
              </svg>
              <span>Pause</span>
            </button>
            <button 
              className="icon-btn"
              onClick={handleContinueMining}
              disabled={loading}
              title="Continue mining"
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '6px',
                minWidth: '110px',
                height: '40px',
                padding: '0 12px'
              }}
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M8 5V19L19 12L8 5Z" 
                      fill="currentColor"/>
              </svg>
              <span>Continue</span>
            </button>
            <button 
              className="icon-btn"
              onClick={() => setShowAsicControl(!showAsicControl)}
              title="ASIC Power Control"
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                width: '40px',
                height: '40px',
                background: showAsicControl ? '#5865f2' : 'transparent'
              }}
            >
              ⚡
            </button>
          </div>

          {showAsicControl && primaryMiner && (
            <div style={{ marginTop: '20px', borderTop: '1px solid #333', paddingTop: '20px' }}>
              <AsicPowerControl deviceId={device.id} minerId={primaryMiner.id} />
            </div>
          )}
        </div>
      )}
    </div>
  );
}
