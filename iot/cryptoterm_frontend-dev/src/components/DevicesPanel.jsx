import { useEffect, useState } from "react";
import { api } from "../api";
import { auth } from "../auth"; 
import MinersList from "./MinersList";

export default function DevicesPanel() { 
    const [devices, setDevices] = useState([]);
    const [selectedDevice, setSelectedDevice] = useState(null); 
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const token = auth.getToken();
    const userId = auth.getUserId();

    useEffect(() => {
        if (token && userId) {
            setLoading(true);
            setError(null);
            api.getDevices(token, userId)
                .then(data => {
                    setDevices(data.devices || []);
                    setLoading(false);
                })
                .catch(err => {
                    setError(err.message || 'Failed to load devices');
                    setLoading(false);
                });
        }
    }, [token, userId]);

    const handleSelectDevice = (device) => {
        setSelectedDevice(device); 
    };

    if (loading) {
        return <div className="loading-message">Loading devices...</div>;
    }

    if (error) {
        return <div className="error-message">Error: {error}</div>;
    }

    return (
        <div className="devices">
            <h2>🖥️ Hardware Devices</h2>
            {devices.length === 0 ? (
                <div className="info-message">No devices found. Add a device to get started.</div>
            ) : (
                <ul>
                    {devices.map((d) => (
                        <li 
                            key={d.deviceId} 
                            onClick={() => handleSelectDevice(d)} 
                            className={selectedDevice?.deviceId === d.deviceId ? 'selected' : ''}
                        >
                            <div>
                                <strong>{d.deviceId}</strong>
                                <div style={{ fontSize: '14px', color: '#999', marginTop: '8px' }}>
                                    {d.miners.length} hardware
                                </div>
                            </div>
                        </li>
                    ))}
                </ul>
            )}

            {selectedDevice && (
                <MinersList 
                    token={token} 
                    minerIds={selectedDevice.miners} 
                    deviceId={selectedDevice.deviceId}
                />
            )}
        </div>
    );
}
