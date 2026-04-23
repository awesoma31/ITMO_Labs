// src/components/MinersList.jsx

import { useState } from "react";
import MinerDetails from "./MinerDetails";

export default function MinersList({ token, deviceId, minerIds }) {
    const [selectedMinerId, setSelectedMinerId] = useState(null);
    const miners = Array.isArray(minerIds) ? minerIds : [];

    if (selectedMinerId) {
        return (
            <MinerDetails 
                token={token}
                deviceId={deviceId}
                minerId={selectedMinerId}
                onBack={() => setSelectedMinerId(null)}
            />
        );
    }

    return (
        <div className="miners-list">
            <h3>⚙️ Miners for Device: {deviceId}</h3>
            {miners.length === 0 ? (
                <div className="info-message">No miners found for this device.</div>
            ) : (
                <ul>
                    {miners.map(minerId => (
                        <li 
                            key={minerId} 
                            onClick={() => setSelectedMinerId(minerId)}
                        >
                            <div>
                                <strong>P99</strong>
                                <div style={{ fontSize: '12px', color: '#999', marginTop: '4px' }}>
                                    Miner ID: {minerId}
                                </div>
                            </div>
                            <span style={{ fontSize: '18px' }}>→</span>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}
