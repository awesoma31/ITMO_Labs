// src/components/ProfitCalculator.jsx
import React, { useState, useEffect } from 'react';
import { auth } from '../auth';
import { api } from '../api';

export default function ProfitCalculator({ userId }) {
  const [pricePerKwh, setPricePerKwh] = useState('');
  const [timeRange, setTimeRange] = useState('Day');
  const [profitData, setProfitData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (userId) {
      loadProfitData();
    }
  }, [timeRange, userId]);

  const getTimeRangeParams = (range) => {
    const to = new Date();
    const from = new Date();

    switch (range) {
      case 'Hour':
        from.setTime(to.getTime() - 60 * 60 * 1000); // 1 hour in ms
        break;
      case 'Day':
        from.setTime(to.getTime() - 24 * 60 * 60 * 1000); // 24 hours in ms
        break;
      case 'Week':
        from.setTime(to.getTime() - 7 * 24 * 60 * 60 * 1000); // 7 days in ms
        break;
      default:
        from.setTime(to.getTime() - 24 * 60 * 60 * 1000);
    }

    console.log('Time range:', range, 'from:', from.toISOString(), 'to:', to.toISOString());
    return { from, to };
  };

  const loadProfitData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const token = auth.getToken();
      if (!token) {
        console.error('No token available');
        return;
      }

      const { from, to } = getTimeRangeParams(timeRange);
      
      const data = await api.getMiningProfit(token, userId, from, to);
      setProfitData(data);
      
      setLoading(false);
    } catch (error) {
      console.error('Failed to load profit data:', error);
      setError(error.message);
      setLoading(false);
    }
  };

  const calculateProfit = () => {
    if (!profitData) return { 
      btcMined: 0,
      revenueUsd: 0, 
      revenueRub: 0,
      withExpenses: 0,
      withExpensesRub: 0,
      btcPriceUsd: 0,
      btcPriceRub: 0
    };

    const price = parseFloat(pricePerKwh) || 0;
    
    // Данные из API
    const btcMined = profitData.btcMined || 0;
    const revenueUsd = profitData.revenueUsd || 0;
    const powerConsumptionW = profitData.avgPowerConsumptionW || 0;
    const workedHours = profitData.workedHours || 0;
    const btcPriceUsd = profitData.btcPriceUsd || 0;
    const usdRubRate = profitData.usdRubRate || 90;
    
    // Расчет стоимости электричества
    const powerConsumptionKw = powerConsumptionW / 1000; // W to kW
    const electricityCost = powerConsumptionKw * price * workedHours;
    
    // Прибыль
    const profitUsd = revenueUsd - electricityCost;
    const revenueRub = revenueUsd * usdRubRate;
    const profitRub = profitUsd * usdRubRate;
    const btcPriceRub = btcPriceUsd * usdRubRate;
    
    return {
      btcMined,
      revenueUsd,
      revenueRub,
      withExpenses: profitUsd,
      withExpensesRub: profitRub,
      btcPriceUsd,
      btcPriceRub
    };
  };

  const profit = calculateProfit();

  const formatNumber = (num) => {
    return num.toLocaleString('en-US');
  };

  return (
    <div className="profit-calculator">
      <div className="calculator-header">
        <h3>Bitcoin Mining Calculator</h3>
        <button className="info-btn" title="Mining profit calculator based on your actual metrics">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="10" stroke="#5865f2" strokeWidth="2"/>
            <text x="12" y="16" textAnchor="middle" fill="#5865f2" fontSize="14" fontWeight="bold">?</text>
          </svg>
        </button>
      </div>

      <div className="calculator-form">
        <div className="form-group">
          <div className="crypto-select">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="#f7931a">
              <path d="M23.638 14.904c-1.602 6.43-8.113 10.34-14.542 8.736C2.67 22.05-1.244 15.525.362 9.105 1.962 2.67 8.475-1.243 14.9.358c6.43 1.605 10.342 8.115 8.738 14.548v-.002zm-6.35-4.613c.24-1.59-.974-2.45-2.64-3.03l.54-2.153-1.315-.33-.525 2.107c-.345-.087-.705-.167-1.064-.25l.526-2.127-1.32-.33-.54 2.165c-.285-.067-.565-.132-.84-.2l-1.815-.45-.35 1.407s.975.225.955.236c.535.136.63.486.615.766l-1.477 5.92c-.075.166-.24.406-.614.314.015.02-.96-.24-.96-.24l-.66 1.51 1.71.426.93.242-.54 2.19 1.32.327.54-2.17c.36.1.705.19 1.05.273l-.51 2.154 1.32.33.545-2.19c2.24.427 3.93.257 4.64-1.774.57-1.637-.03-2.58-1.217-3.196.854-.193 1.5-.76 1.68-1.93h.01zm-3.01 4.22c-.404 1.64-3.157.75-4.05.53l.72-2.9c.896.23 3.757.67 3.33 2.37zm.41-4.24c-.37 1.49-2.662.735-3.405.55l.654-2.64c.744.18 3.137.524 2.75 2.084v.006z"/>
            </svg>
            <span style={{ marginLeft: '8px', color: '#fff', fontWeight: '500' }}>Bitcoin (BTC)</span>
          </div>
        </div>

        <div className="form-group">
          <input 
            type="number" 
            placeholder="Electricity price per kW/h (RUB), e.g. 5.50"
            value={pricePerKwh}
            onChange={(e) => setPricePerKwh(e.target.value)}
            step="0.01"
            min="0"
          />
        </div>
        
        {pricePerKwh && parseFloat(pricePerKwh) > 0 && profitData && profitData.avgPowerConsumptionW > 0 && (
          <div style={{ 
            padding: '8px 12px', 
            backgroundColor: 'rgba(255,255,255,0.05)', 
            borderRadius: '8px',
            marginBottom: '12px',
            fontSize: '12px',
            color: '#888' 
          }}>
            💡 Electricity cost: ~{((profitData.avgPowerConsumptionW / 1000) * parseFloat(pricePerKwh) * profitData.workedHours).toFixed(2)} ₽
            {' '}(~${(((profitData.avgPowerConsumptionW / 1000) * parseFloat(pricePerKwh) * profitData.workedHours) / (profitData.usdRubRate || 90)).toFixed(2)})
          </div>
        )}

        <div className="time-range-select">
          {['Hour', 'Day', 'Week'].map(range => (
            <button
              key={range}
              className={timeRange === range ? 'active' : ''}
              onClick={() => setTimeRange(range)}
            >
              {range}
            </button>
          ))}
        </div>

        {loading && <p style={{ textAlign: 'center', color: '#666' }}>Loading...</p>}
        {error && <p style={{ textAlign: 'center', color: '#f44', fontSize: '12px' }}>Error: {error}</p>}

        <div className="profit-results">
          <div className="result-row">
            <span>BTC Mined</span>
            <span className="result-value profit-positive">
              {profit.btcMined.toFixed(8)} BTC
            </span>
          </div>

          <div className="result-row">
            <span>BTC/USD Rate</span>
            <span className="result-value">
              <svg width="12" height="12" viewBox="0 0 12 12" fill="#5865f2">
                <circle cx="6" cy="6" r="6"/>
              </svg>
              ${formatNumber(Math.round(profit.btcPriceUsd))}
            </span>
          </div>

          <div className="result-row">
            <span>BTC/RUB Rate</span>
            <span className="result-value">
              {formatNumber(Math.round(profit.btcPriceRub))} ₽
            </span>
          </div>

          <div className="result-row">
            <span>Revenue (USD)</span>
            <span className="result-value profit-positive">
              ${formatNumber(profit.revenueUsd.toFixed(2))}
            </span>
          </div>

          <div className="result-row">
            <span>Revenue (RUB)</span>
            <span className="result-value profit-positive">
              {formatNumber(profit.revenueRub.toFixed(2))} ₽
            </span>
          </div>

          <div className="result-row">
            <span>Profit with expenses (USD)</span>
            <span className={`result-value ${profit.withExpenses >= 0 ? 'profit-positive' : 'profit-negative'}`}>
              ${formatNumber(profit.withExpenses.toFixed(2))}
            </span>
          </div>

          <div className="result-row">
            <span>Profit with expenses (RUB)</span>
            <span className={`result-value ${profit.withExpensesRub >= 0 ? 'profit-positive' : 'profit-negative'}`}>
              {formatNumber(profit.withExpensesRub.toFixed(2))} ₽
            </span>
          </div>
          
          {profitData && (
            <>
              <div className="result-row">
                <span>Avg hashrate</span>
                <span className="result-value">
                  {profitData.avgHashrateThs ? profitData.avgHashrateThs.toFixed(2) : 0} TH/s
                </span>
              </div>
              <div className="result-row">
                <span>Avg power consumption</span>
                <span className="result-value">
                  {profitData.avgPowerConsumptionW ? (profitData.avgPowerConsumptionW / 1000).toFixed(2) : 0} kW
                </span>
              </div>
              <div className="result-row">
                <span>Worked hours</span>
                <span className="result-value">
                  {profitData.workedHours ? profitData.workedHours.toFixed(1) : 0} h
                </span>
              </div>
              <div className="result-row">
                <span>Network difficulty</span>
                <span className="result-value">
                  {profitData.difficulty ? (profitData.difficulty / 1e12).toFixed(2) + 'T' : 0}
                </span>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
