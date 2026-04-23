// src/components/MinerDetails.jsx

import React, { useState } from 'react';
import { api } from '../api';
import MetricsView from './MetricsView';
import DynamicMetricsView from './DynamicMetricsView';
import ConsoleLogs from './ConsoleLogs';
import AsicPowerControl from './AsicPowerControl';

const MinerDetails = ({ token, deviceId, minerId, onBack }) => {
    const [status, setStatus] = useState('');
    const [activeTab, setActiveTab] = useState('metrics');

    const handleRestart = async () => {
        const confirmRestart = window.confirm(`Are you sure you want to restart miner ${minerId}?`);
        if (!confirmRestart) return;

        setStatus('Restarting...');
        try {
            await api.restartMiner(token, minerId);
            setStatus(`Miner ${minerId} restarted successfully!`);
            setTimeout(() => setStatus(''), 5000);
        } catch (error) {
            setStatus(`Restart failed: ${error.message}`);
        }
    };

    const handlePauseMining = async () => {
        const confirmPause = window.confirm(`Are you sure you want to pause mining on miner ${minerId}?`);
        if (!confirmPause) return;

        setStatus('Pausing mining...');
        try {
            await api.pauseMining(token, minerId);
            setStatus(`Mining paused on miner ${minerId} successfully!`);
            setTimeout(() => setStatus(''), 5000);
        } catch (error) {
            setStatus(`Pause mining failed: ${error.message}`);
        }
    };

    const handleContinueMining = async () => {
        const confirmContinue = window.confirm(`Are you sure you want to continue mining on miner ${minerId}?`);
        if (!confirmContinue) return;

        setStatus('Continuing mining...');
        try {
            await api.continueMining(token, minerId);
            setStatus(`Mining continued on miner ${minerId} successfully!`);
            setTimeout(() => setStatus(''), 5000);
        } catch (error) {
            setStatus(`Continue mining failed: ${error.message}`);
        }
    };

    return (
        <div className="card" style={{ marginTop: '24px' }}>
            <div className="card-header">
                <div>
                    <button onClick={onBack} className="secondary" style={{ marginBottom: '8px' }}>
                        ← Back to Miners
                    </button>
                    <h2 style={{ fontSize: '28px', marginTop: '8px' }}>
                        Hey, Matvei! 👋
                    </h2>
                    <p style={{ color: '#999', marginTop: '8px' }}>
                        Device: {deviceId} | Miner: {minerId}
                    </p>
                </div>
                <div style={{ display: 'flex', gap: '12px', alignSelf: 'flex-start' }}>
                    <button 
                        onClick={handleRestart} 
                        disabled={status.includes('...')}
                        style={{ 
                            display: 'flex', 
                            alignItems: 'center', 
                            gap: '8px',
                            minWidth: '160px',
                            justifyContent: 'center'
                        }}
                        title="Restart miner"
                    >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M4 12C4 7.58172 7.58172 4 12 4C14.5264 4 16.7792 5.17108 18.2454 7M20 12C20 16.4183 16.4183 20 12 20C9.47362 20 7.22075 18.8289 5.75463 17M18.5 3V7H14.5M5.5 21V17H9.5" 
                                  stroke="currentColor" 
                                  strokeWidth="2" 
                                  strokeLinecap="round" 
                                  strokeLinejoin="round"/>
                        </svg>
                        Restart
                    </button>
                    <button 
                        onClick={handlePauseMining} 
                        disabled={status.includes('...')}
                        style={{ 
                            display: 'flex', 
                            alignItems: 'center', 
                            gap: '8px',
                            minWidth: '160px',
                            justifyContent: 'center'
                        }}
                        title="Pause mining"
                    >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M6 4H10V20H6V4ZM14 4H18V20H14V4Z" 
                                  fill="currentColor"/>
                        </svg>
                        Pause
                    </button>
                    <button 
                        onClick={handleContinueMining} 
                        disabled={status.includes('...')}
                        style={{ 
                            display: 'flex', 
                            alignItems: 'center', 
                            gap: '8px',
                            minWidth: '160px',
                            justifyContent: 'center'
                        }}
                        title="Continue mining"
                    >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M8 5V19L19 12L8 5Z" 
                                  fill="currentColor"/>
                        </svg>
                        Continue
                    </button>
                </div>
            </div>
            
            {status && (
                <div className={status.includes('failed') ? 'error-message' : 'info-message'}>
                    {status}
                </div>
            )}

            <div className="tab-navigation">
                <button 
                    className={`tab-button ${activeTab === 'metrics' ? 'active' : ''}`}
                    onClick={() => setActiveTab('metrics')}
                >
                    📈 Hash Rate & Temperature
                </button>
                <button 
                    className={`tab-button ${activeTab === 'dynamic' ? 'active' : ''}`}
                    onClick={() => setActiveTab('dynamic')}
                >
                    📊 Other Metrics
                </button>
                <button 
                    className={`tab-button ${activeTab === 'control' ? 'active' : ''}`}
                    onClick={() => setActiveTab('control')}
                >
                    ⚡ ASIC Control
                </button>
                <button 
                    className={`tab-button ${activeTab === 'logs' ? 'active' : ''}`}
                    onClick={() => setActiveTab('logs')}
                >
                    📜 Console Logs
                </button>
            </div>

            {activeTab === 'metrics' && (
                <MetricsView token={token} deviceId={deviceId} />
            )}

            {activeTab === 'dynamic' && (
                <DynamicMetricsView token={token} deviceId={deviceId} />
            )}

            {activeTab === 'control' && (
                <AsicPowerControl deviceId={deviceId} minerId={minerId} />
            )}

            {activeTab === 'logs' && (
                <ConsoleLogs token={token} deviceId={deviceId} />
            )}
        </div>
    );
};

export default MinerDetails;
