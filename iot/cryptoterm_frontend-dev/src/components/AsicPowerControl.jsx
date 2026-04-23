import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { auth } from '../auth';
import MiningCalendar from './MiningCalendar';

/**
 * Компонент для управления режимами мощности ASIC майнера
 * Интегрирован в MinerDetails
 */
export default function AsicPowerControl({ deviceId, minerId }) {
  const [selectedMode, setSelectedMode] = useState('ECO');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [showCalendar, setShowCalendar] = useState(false);
  const [showCommands, setShowCommands] = useState(false);
  
  // Состояния для команд
  const [availableCommands, setAvailableCommands] = useState([]);
  const [selectedCommand, setSelectedCommand] = useState(null);
  const [loadingCommands, setLoadingCommands] = useState(false);
  const [minerInfo, setMinerInfo] = useState(null);

  // Доступные режимы работы
  const powerModes = {
    'ECO': { description: 'Экономичный', icon: '💚' },
    'STANDARD': { description: 'Стандартный', icon: '⚖️' },
    'OVERCLOCK': { description: 'Разгон', icon: '🔥' }
  };

  // Загрузка информации о майнере при монтировании
  useEffect(() => {
    if (minerId) {
      loadMinerInfo();
    }
  }, [minerId]);

  // Загрузка команд при переключении на вкладку команд
  useEffect(() => {
    if (showCommands && minerInfo) {
      loadAvailableCommands();
    }
  }, [showCommands, minerInfo]);

  const loadMinerInfo = async () => {
    try {
      // Получаем информацию о майнере из devices
      const userId = auth.getUserId();
      if (!userId) return;

      const token = auth.getToken();
      const response = await api.getUserDevices(token, userId);
      const allDevices = response.devices || [];
      
      for (const device of allDevices) {
        const miner = device.miners?.find(m => m.id === minerId);
        if (miner) {
          setMinerInfo(miner);
          break;
        }
      }
    } catch (err) {
      console.error('Failed to load miner info:', err);
    }
  };

  const loadAvailableCommands = async () => {
    if (!minerInfo) return;

    setLoadingCommands(true);
    setError(null);

    try {
      // Не передаем vendor если он "default" или пустой
      const vendor = minerInfo.vendor && minerInfo.vendor !== 'default' 
        ? minerInfo.vendor 
        : null;
      
      const commands = await api.getAvailableCommands(
        minerInfo.model,
        vendor
      );
      setAvailableCommands(commands);
    } catch (err) {
      console.error('Failed to load commands:', err);
      setError('Не удалось загрузить команды: ' + err.message);
      setAvailableCommands([]);
    } finally {
      setLoadingCommands(false);
    }
  };

  const handleExecuteCommand = async () => {
    if (!deviceId || !minerId || !selectedCommand) {
      setError('Не выбрана команда');
      return;
    }

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await api.executeAsicCommand(
        deviceId,
        minerId,
        selectedCommand.commandId
      );
      
      setResult(response);
      setError(null);
    } catch (err) {
      console.error('Failed to execute command:', err);
      setError(err.message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  // Смена режима работы майнера
  const handleChangePowerMode = async () => {
    // Проверяем что пользователь залогинен
    const token = auth.getToken();
    if (!token) {
      setError('Необходимо войти в систему для управления ASIC');
      return;
    }

    if (!deviceId || !minerId) {
      setError('Не указан ID устройства или ID майнера');
      return;
    }

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await api.changeMinerModeNew(
        deviceId,    // rpId
        minerId,     // asicId
        selectedMode // mode: ECO, STANDARD, OVERCLOCK
      );
      
      setResult(response);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Форматирование времени
  const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return day + '.' + month + '.' + year + ', ' + hours + ':' + minutes + ':' + seconds;
  };

  return (
    <div style={{ padding: '20px' }}>
      {/* Кнопки переключения режимов */}
      <div style={{ 
        display: 'flex', 
        gap: '10px', 
        marginBottom: '20px',
        padding: '10px',
        background: '#1a1a1a',
        borderRadius: '8px',
        border: '1px solid #333'
      }}>
        <button
          onClick={() => {
            setShowCalendar(false);
            setShowCommands(false);
          }}
          style={{
            flex: 1,
            padding: '10px 20px',
            borderRadius: '8px',
            border: !showCalendar && !showCommands ? '2px solid #5865F2' : '1px solid #333',
            background: !showCalendar && !showCommands ? 'rgba(88, 101, 242, 0.2)' : 'transparent',
            color: !showCalendar && !showCommands ? '#5865F2' : '#999',
            fontSize: '14px',
            fontWeight: '600',
            cursor: 'pointer',
            transition: 'all 0.2s'
          }}
        >
          ⚡ Ручное управление
        </button>
        <button
          onClick={() => {
            setShowCalendar(false);
            setShowCommands(true);
          }}
          style={{
            flex: 1,
            padding: '10px 20px',
            borderRadius: '8px',
            border: showCommands ? '2px solid #5865F2' : '1px solid #333',
            background: showCommands ? 'rgba(88, 101, 242, 0.2)' : 'transparent',
            color: showCommands ? '#5865F2' : '#999',
            fontSize: '14px',
            fontWeight: '600',
            cursor: 'pointer',
            transition: 'all 0.2s'
          }}
        >
          🎛️ Выполнить команду
        </button>
        <button
          onClick={() => {
            setShowCalendar(true);
            setShowCommands(false);
          }}
          style={{
            flex: 1,
            padding: '10px 20px',
            borderRadius: '8px',
            border: showCalendar ? '2px solid #5865F2' : '1px solid #333',
            background: showCalendar ? 'rgba(88, 101, 242, 0.2)' : 'transparent',
            color: showCalendar ? '#5865F2' : '#999',
            fontSize: '14px',
            fontWeight: '600',
            cursor: 'pointer',
            transition: 'all 0.2s'
          }}
        >
          📅 Календарь майнинга
        </button>
      </div>

      {/* Календарь майнинга */}
      {showCalendar && (
        <MiningCalendar 
          minerId={minerId}
        />
      )}

      {/* Выполнение команд */}
      {showCommands && (
        <div className="card" style={{ marginBottom: '20px' }}>
          <h3 style={{ marginBottom: '20px' }}>🎛️ Выполнение команд</h3>
          
          {minerInfo && (
            <div style={{ 
              marginBottom: '15px', 
              padding: '10px', 
              background: '#0a0a0a',
              borderRadius: '5px',
              fontSize: '12px',
              color: '#999'
            }}>
              <strong>Модель:</strong> {minerInfo.vendor} {minerInfo.model}
            </div>
          )}

          {loadingCommands && (
            <div style={{ textAlign: 'center', padding: '20px', color: '#999' }}>
              ⏳ Загрузка доступных команд...
            </div>
          )}

          {!loadingCommands && availableCommands.length === 0 && minerInfo && (
            <div style={{ 
              textAlign: 'center', 
              padding: '20px', 
              color: '#999',
              background: '#1a1a1a',
              borderRadius: '8px',
              border: '1px solid #333'
            }}>
              ℹ️ Нет доступных команд для модели {minerInfo.model}
            </div>
          )}

          {!loadingCommands && availableCommands.length > 0 && (
            <>
              <div style={{ marginBottom: '15px' }}>
                <label style={{ display: 'block', marginBottom: '10px', fontWeight: '500' }}>
                  Выберите команду:
                </label>
                <div style={{ display: 'grid', gap: '10px' }}>
                  {availableCommands.map(cmd => (
                    <button
                      key={cmd.commandId}
                      onClick={() => setSelectedCommand(cmd)}
                      disabled={loading}
                      style={{
                        padding: '15px',
                        borderRadius: '8px',
                        border: selectedCommand?.commandId === cmd.commandId 
                          ? '2px solid #5865F2' 
                          : '1px solid #333',
                        background: selectedCommand?.commandId === cmd.commandId 
                          ? 'rgba(88, 101, 242, 0.2)' 
                          : '#1a1a1a',
                        color: selectedCommand?.commandId === cmd.commandId 
                          ? '#5865F2' 
                          : '#fff',
                        cursor: 'pointer',
                        textAlign: 'left',
                        transition: 'all 0.2s',
                        fontSize: '14px'
                      }}
                    >
                      <div style={{ fontWeight: '600', marginBottom: '5px' }}>
                        {cmd.commandName}
                      </div>
                      <div style={{ fontSize: '12px', opacity: 0.7 }}>
                        ID: <code style={{ 
                          background: 'rgba(0,0,0,0.3)', 
                          padding: '2px 6px', 
                          borderRadius: '4px' 
                        }}>
                          {cmd.commandId}
                        </code>
                      </div>
                    </button>
                  ))}
                </div>
              </div>

              {selectedCommand && (
                <button
                  onClick={handleExecuteCommand}
                  disabled={loading}
                  className="start-btn"
                  style={{
                    width: '100%',
                    padding: '15px',
                    marginTop: '10px',
                    fontSize: '16px'
                  }}
                >
                  {loading ? '⏳ Выполнение команды...' : '▶️ Выполнить команду'}
                </button>
              )}
            </>
          )}

          {result && (
            <div style={{
              padding: '15px',
              marginTop: '15px',
              borderRadius: '8px',
              background: 'rgba(40, 167, 69, 0.1)',
              border: '1px solid rgba(40, 167, 69, 0.3)',
              color: '#28a745'
            }}>
              <strong>✓ Команда успешно отправлена!</strong>
              <div style={{ marginTop: '10px', fontSize: '14px', color: '#fff' }}>
                <div style={{ marginBottom: '5px' }}>
                  <strong>ID команды:</strong> 
                  <code style={{ 
                    marginLeft: '10px',
                    background: 'rgba(0,0,0,0.3)', 
                    padding: '4px 8px', 
                    borderRadius: '4px',
                    fontSize: '12px'
                  }}>
                    {result.cmdId}
                  </code>
                </div>
                <div style={{ marginBottom: '5px' }}>
                  <strong>Статус:</strong> 
                  <span style={{ 
                    marginLeft: '10px',
                    padding: '2px 8px',
                    borderRadius: '4px',
                    background: 'rgba(88, 101, 242, 0.2)',
                    color: '#5865F2',
                    fontSize: '12px',
                    fontWeight: '600'
                  }}>
                    {result.status}
                  </span>
                </div>
              </div>
            </div>
          )}

          {error && (
            <div style={{
              padding: '15px',
              marginTop: '15px',
              borderRadius: '8px',
              background: 'rgba(220, 53, 69, 0.1)',
              border: '1px solid rgba(220, 53, 69, 0.3)',
              color: '#dc3545'
            }}>
              <strong>✗ Ошибка:</strong> {error}
            </div>
          )}
        </div>
      )}

      {/* Ручное управление */}
      {!showCalendar && !showCommands && (
        <>
      <div className="card" style={{ marginBottom: '20px' }}>
        <h3 style={{ marginBottom: '20px' }}>⚡ Управление режимом мощности ASIC</h3>
        
        <div style={{ display: 'grid', gap: '15px' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
              Режим работы:
            </label>
            <div style={{ 
              display: 'grid', 
              gridTemplateColumns: 'repeat(3, 1fr)',
              gap: '10px'
            }}>
              {Object.entries(powerModes).map(([mode, config]) => (
                <button
                  key={mode}
                  onClick={() => setSelectedMode(mode)}
                  disabled={loading}
                  style={{
                    padding: '15px',
                    borderRadius: '8px',
                    border: selectedMode === mode ? '2px solid #5865F2' : '1px solid #333',
                    background: selectedMode === mode ? 'rgba(88, 101, 242, 0.2)' : '#1a1a1a',
                    color: selectedMode === mode ? '#5865F2' : '#999',
                    cursor: 'pointer',
                    transition: 'all 0.2s',
                    fontSize: '14px',
                    fontWeight: selectedMode === mode ? '600' : '400',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    gap: '5px'
                  }}
                >
                  <span style={{ fontSize: '24px' }}>{config.icon}</span>
                  <span>{mode}</span>
                  <span style={{ fontSize: '11px', opacity: 0.7 }}>{config.description}</span>
                </button>
              ))}
            </div>
          </div>

          <button 
            onClick={handleChangePowerMode}
            disabled={loading || !deviceId || !minerId}
            className="start-btn"
            style={{ 
              width: '100%',
              padding: '12px',
              marginTop: '10px'
            }}
          >
            {loading ? '⏳ Отправка команды...' : '⚡ Применить режим мощности'}
          </button>

          {result && (
            <div style={{ 
              padding: '15px', 
              borderRadius: '8px', 
              background: 'rgba(40, 167, 69, 0.1)',
              border: '1px solid rgba(40, 167, 69, 0.3)',
              color: '#28a745'
            }}>
              <strong>✓ Команда успешно создана!</strong>
              <div style={{ marginTop: '8px', fontSize: '14px' }}>
                ID команды: <code style={{ background: 'rgba(0,0,0,0.3)', padding: '2px 6px', borderRadius: '4px' }}>{result.cmdId}</code>
              </div>
              <div style={{ fontSize: '14px' }}>Статус: <strong>{result.status}</strong></div>
            </div>
          )}

          {error && (
            <div style={{ 
              padding: '15px', 
              borderRadius: '8px', 
              background: 'rgba(220, 53, 69, 0.1)',
              border: '1px solid rgba(220, 53, 69, 0.3)',
              color: '#dc3545'
            }}>
              <strong>✗ Ошибка:</strong> {error}
            </div>
          )}
        </div>
      </div>
        </>
      )}
    </div>
  );
}
