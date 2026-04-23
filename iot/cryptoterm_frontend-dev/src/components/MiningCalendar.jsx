import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { auth } from '../auth';

/**
 * Компонент календаря планирования майнинга
 * Позволяет пользователю планировать режим работы майнера по датам и времени
 */
export default function MiningCalendar({ minerId }) {
  console.log('🔵 [MiningCalendar] Component mounted/updated with:', { minerId });
  
  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedDates, setSelectedDates] = useState([]);
  const [selectedMode, setSelectedMode] = useState('ECO');
  const [selectedTime, setSelectedTime] = useState('00:00');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);
  const [currentTime, setCurrentTime] = useState(getCurrentTimeString());

  // Режимы работы
  const powerModes = {
    'ECO': { description: 'Экономичный', icon: '💚' },
    'STANDARD': { description: 'Стандартный', icon: '⚖️' },
    'OVERCLOCK': { description: 'Разгон', icon: '🔥' }
  };

  // Функция для безопасного форматирования времени
  function getCurrentTimeString() {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return hours + ':' + minutes;
  }

  // Обновляем текущее время каждую минуту
  useEffect(() => {
    console.log('🔵 [MiningCalendar] Setting up time update interval');
    const interval = setInterval(() => {
      const newTime = getCurrentTimeString();
      console.log('🔵 [MiningCalendar] Time updated to:', newTime);
      setCurrentTime(newTime);
    }, 60000); // каждую минуту
    
    return () => {
      console.log('🔵 [MiningCalendar] Cleaning up time update interval');
      clearInterval(interval);
    };
  }, []);

  // Логируем изменения состояния
  useEffect(() => {
    console.log('🔵 [MiningCalendar] State changed - selectedDates:', selectedDates.length, selectedDates);
  }, [selectedDates]);

  useEffect(() => {
    console.log('🔵 [MiningCalendar] State changed - selectedMode:', selectedMode);
  }, [selectedMode]);

  useEffect(() => {
    console.log('🔵 [MiningCalendar] State changed - selectedTime:', selectedTime);
  }, [selectedTime]);

  useEffect(() => {
    console.log('🔵 [MiningCalendar] State changed - loading:', loading);
  }, [loading]);

  useEffect(() => {
    console.log('🔵 [MiningCalendar] State changed - error:', error);
  }, [error]);

  useEffect(() => {
    console.log('🔵 [MiningCalendar] State changed - result:', result);
  }, [result]);

  // Получить дни месяца
  const getDaysInMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();

    const days = [];
    // Добавляем пустые дни для выравнивания начала месяца
    for (let i = 0; i < (startingDayOfWeek === 0 ? 6 : startingDayOfWeek - 1); i++) {
      days.push(null);
    }
    // Добавляем дни месяца
    for (let day = 1; day <= daysInMonth; day++) {
      days.push(new Date(year, month, day));
    }
    return days;
  };

  // Проверка, выбрана ли дата
  const isDateSelected = (date) => {
    if (!date) return false;
    return selectedDates.some(d => 
      d.getDate() === date.getDate() &&
      d.getMonth() === date.getMonth() &&
      d.getFullYear() === date.getFullYear()
    );
  };

  // Выбор даты
  const toggleDateSelection = (date) => {
    if (!date) return;
    
    console.log('🔵 [MiningCalendar] toggleDateSelection:', date);
    const isSelected = isDateSelected(date);
    console.log('🔵 [MiningCalendar] Date is currently selected:', isSelected);
    
    if (isSelected) {
      setSelectedDates(selectedDates.filter(d => 
        !(d.getDate() === date.getDate() &&
          d.getMonth() === date.getMonth() &&
          d.getFullYear() === date.getFullYear())
      ));
      console.log('🔵 [MiningCalendar] Date removed from selection');
    } else {
      setSelectedDates([...selectedDates, date]);
      console.log('🔵 [MiningCalendar] Date added to selection');
    }
  };

  // Навигация по месяцам
  const previousMonth = () => {
    const newDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - 1);
    console.log('🔵 [MiningCalendar] previousMonth:', newDate);
    setCurrentDate(newDate);
  };

  const nextMonth = () => {
    const newDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1);
    console.log('🔵 [MiningCalendar] nextMonth:', newDate);
    setCurrentDate(newDate);
  };

  // Планирование команды
  const handleSchedule = async () => {
    console.log('🔵 [MiningCalendar] handleSchedule started');
    console.log('🔵 [MiningCalendar] Selected dates:', selectedDates);
    console.log('🔵 [MiningCalendar] Selected mode:', selectedMode);
    console.log('🔵 [MiningCalendar] Selected time:', selectedTime);
    console.log('🔵 [MiningCalendar] MinerId:', minerId);
    
    if (selectedDates.length === 0) {
      console.log('⚠️ [MiningCalendar] No dates selected');
      setError('Выберите хотя бы одну дату');
      return;
    }

    const token = auth.getToken();
    console.log('🔵 [MiningCalendar] Token exists:', !!token);
    if (!token) {
      console.log('❌ [MiningCalendar] No token found');
      setError('Необходимо войти в систему');
      return;
    }

    if (!minerId) {
      console.log('❌ [MiningCalendar] Missing minerId');
      setError('Не указан ID майнера');
      return;
    }

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const results = [];

      // Создаем запланированную команду для каждой выбранной даты
      console.log('🔵 [MiningCalendar] Starting to schedule commands for', selectedDates.length, 'dates');
      let skippedCount = 0;
      for (const date of selectedDates) {
        console.log('🔵 [MiningCalendar] Processing date:', date);
        const [hours, minutes] = selectedTime.split(':');
        const scheduledDateTime = new Date(date);
        scheduledDateTime.setHours(parseInt(hours), parseInt(minutes), 0, 0);
        console.log('🔵 [MiningCalendar] Scheduled date time:', scheduledDateTime.toISOString());

        // Проверяем, что дата в будущем
        const now = new Date();
        if (scheduledDateTime <= now) {
          console.warn('⚠️ [MiningCalendar] Skipping past date:', scheduledDateTime);
          skippedCount++;
          continue;
        }

        // Используем новый упрощенный API для планирования смены режима
        console.log('🔵 [MiningCalendar] Scheduling mode change:', {
          token: '***',
          minerId,
          mode: selectedMode,
          scheduledAt: scheduledDateTime.toISOString()
        });
        const response = await api.scheduleChangeMinerMode(
          token,
          minerId,
          selectedMode,
          scheduledDateTime
        );
        console.log('🔵 [MiningCalendar] Schedule response:', response);
        results.push(response);
      }

      console.log('🔵 [MiningCalendar] Scheduling completed. Results:', results.length, 'Skipped:', skippedCount);

      if (results.length === 0 && skippedCount > 0) {
        const now = new Date();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const timeStr = hours + ':' + minutes;
        console.log('❌ [MiningCalendar] All dates are in the past');
        throw new Error('Все выбранные даты в прошлом. Выберите будущее время (сейчас ' + timeStr + ')');
      }

      const message = results.length > 0 
        ? '✓ Запланировано ' + results.length + ' ' + (results.length === 1 ? 'команда' : 'команд') + 
          (skippedCount > 0 ? ' (пропущено ' + skippedCount + ' прошедших дат)' : '')
        : 'Нет команд для планирования';
      
      console.log('✅ [MiningCalendar] Success:', message);
      setResult(message);
      setSelectedDates([]);
    } catch (err) {
      console.error('❌ [MiningCalendar] Error:', err);
      console.error('❌ [MiningCalendar] Error stack:', err.stack);
      setError(err.message);
    } finally {
      setLoading(false);
      console.log('🔵 [MiningCalendar] handleSchedule completed');
    }
  };

  // Форматирование даты
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return day + '.' + month + '.' + year + ' ' + hours + ':' + minutes;
  };

  const days = getDaysInMonth(currentDate);
  const monthYear = currentDate.getFullYear() + ' ' + 
    ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 
     'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'][currentDate.getMonth()];

  console.log('🔵 [MiningCalendar] Rendering - current month:', monthYear);
  console.log('🔵 [MiningCalendar] Rendering - days count:', days.length);
  console.log('🔵 [MiningCalendar] Rendering - selected dates count:', selectedDates.length);

  return (
    <div style={{ padding: '20px' }}>
      <div className="card" style={{ marginBottom: '20px', background: '#000', border: '1px solid #333' }}>
        <h3 style={{ marginBottom: '20px', color: '#ccc' }}>📅 Календарь майнинга</h3>
        
        {/* Календарь */}
        <div style={{ marginBottom: '20px' }}>
          <div style={{ 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center',
            marginBottom: '15px',
            padding: '10px',
            background: '#000',
            borderRadius: '8px'
          }}>
            <button 
              onClick={previousMonth}
              style={{
                background: 'transparent',
                border: 'none',
                color: '#fff',
                fontSize: '20px',
                cursor: 'pointer'
              }}
            >
              ‹
            </button>
            <span style={{ 
              fontSize: '16px', 
              fontWeight: '500',
              color: '#fff',
              textTransform: 'capitalize'
            }}>
              {monthYear}
            </span>
            <button 
              onClick={nextMonth}
              style={{
                background: 'transparent',
                border: 'none',
                color: '#fff',
                fontSize: '20px',
                cursor: 'pointer'
              }}
            >
              ›
            </button>
          </div>

          {/* Дни недели */}
          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(7, 1fr)', 
            gap: '5px',
            marginBottom: '10px',
            textAlign: 'center'
          }}>
            {['ПН', 'ВТ', 'СР', 'ЧТ', 'ПТ', 'СБ', 'ВС'].map(day => (
              <div key={day} style={{ 
                padding: '8px', 
                fontSize: '12px',
                color: '#999',
                fontWeight: '500'
              }}>
                {day}
              </div>
            ))}
          </div>

          {/* Дни месяца */}
          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(7, 1fr)', 
            gap: '5px'
          }}>
            {days.map((date, index) => {
              const isSelected = isDateSelected(date);
              const today = new Date();
              today.setHours(0, 0, 0, 0); // Сброс времени для сравнения только дат
              
              const isToday = date && 
                date.getDate() === new Date().getDate() &&
                date.getMonth() === new Date().getMonth() &&
                date.getFullYear() === new Date().getFullYear();
              
              // Проверяем только дату, без времени
              const dateOnly = date ? new Date(date) : null;
              if (dateOnly) {
                dateOnly.setHours(0, 0, 0, 0);
              }
              const isPastDate = dateOnly && dateOnly < today;

              return (
                <button
                  key={index}
                  onClick={() => toggleDateSelection(date)}
                  disabled={!date || isPastDate}
                  style={{
                    padding: '12px',
                    background: isSelected ? '#5865F2' : '#1a1a1a',
                    border: isToday ? '2px solid #5865F2' : '1px solid #333',
                    borderRadius: '8px',
                    color: date ? '#fff' : 'transparent',
                    cursor: date && !isPastDate ? 'pointer' : 'default',
                    opacity: isPastDate ? 0.3 : 1,
                    fontSize: '14px',
                    fontWeight: isSelected ? 'bold' : 'normal',
                    transition: 'all 0.2s'
                  }}
                >
                  {date ? date.getDate() : ''}
                </button>
              );
            })}
          </div>
        </div>

        {/* Выбор режима */}
        <div style={{ 
          background: '#5865F2',
          borderRadius: '12px',
          padding: '20px',
          marginBottom: '15px'
        }}>
          {/* Режимы */}
          <div style={{ 
            display: 'flex', 
            gap: '10px',
            marginBottom: '15px'
          }}>
            {Object.entries(powerModes).map(([mode, config]) => (
              <button
                key={mode}
                onClick={() => {
                  console.log('🔵 [MiningCalendar] Power mode clicked:', mode);
                  setSelectedMode(mode);
                }}
                style={{
                  flex: 1,
                  padding: '10px',
                  borderRadius: '8px',
                  border: 'none',
                  background: selectedMode === mode ? '#fff' : 'rgba(255, 255, 255, 0.2)',
                  color: selectedMode === mode ? '#5865F2' : '#fff',
                  fontSize: '12px',
                  fontWeight: '600',
                  cursor: 'pointer',
                  textTransform: 'uppercase',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: '5px'
                }}
              >
                <span style={{ fontSize: '20px' }}>{config.icon}</span>
                <span>{mode}</span>
                <span style={{ fontSize: '10px', opacity: 0.8 }}>{config.description}</span>
              </button>
            ))}
          </div>

          {/* Время */}
          <div style={{ marginTop: '15px' }}>
            <label style={{ 
              display: 'block', 
              marginBottom: '5px', 
              color: '#fff',
              fontSize: '12px',
              opacity: 0.7
            }}>
              Время запуска:
            </label>
            <input
              type="time"
              value={selectedTime}
              onChange={(e) => {
                console.log('🔵 [MiningCalendar] Time input changed:', e.target.value);
                setSelectedTime(e.target.value);
              }}
              disabled={loading}
              style={{
                width: '100%',
                padding: '10px',
                borderRadius: '8px',
                border: 'none',
                background: 'rgba(0, 0, 0, 0.3)',
                color: '#fff',
                fontSize: '16px'
              }}
            />
            <div style={{
              marginTop: '5px',
              fontSize: '11px',
              color: 'rgba(255, 255, 255, 0.5)'
            }}>
              💡 Сейчас: {currentTime} (можно планировать на сегодня, если время в будущем)
            </div>
          </div>
        </div>

        {/* Кнопки действий */}
        <div style={{ 
          display: 'grid',
          gridTemplateColumns: '1fr auto',
          gap: '10px'
        }}>
          <button
            onClick={handleSchedule}
            disabled={loading || selectedDates.length === 0}
            style={{
              padding: '12px',
              borderRadius: '8px',
              border: 'none',
              background: '#5865F2',
              color: '#fff',
              fontSize: '14px',
              fontWeight: '600',
              cursor: selectedDates.length > 0 ? 'pointer' : 'not-allowed',
              opacity: selectedDates.length > 0 ? 1 : 0.5,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '8px'
            }}
          >
            ▶ {loading ? 'Загрузка...' : 'Start'}
          </button>

          <button
            onClick={() => {
              console.log('🔵 [MiningCalendar] Clear button clicked');
              setSelectedDates([]);
            }}
            disabled={selectedDates.length === 0}
            style={{
              padding: '12px',
              borderRadius: '8px',
              border: 'none',
              background: 'rgba(220, 53, 69, 0.2)',
              color: '#dc3545',
              fontSize: '14px',
              cursor: selectedDates.length > 0 ? 'pointer' : 'not-allowed',
              opacity: selectedDates.length > 0 ? 1 : 0.5
            }}
          >
            🗑
          </button>
        </div>

        {/* Сообщения */}
        {result && (
          <div style={{ 
            marginTop: '15px',
            padding: '12px',
            borderRadius: '8px',
            background: 'rgba(40, 167, 69, 0.1)',
            border: '1px solid rgba(40, 167, 69, 0.3)',
            color: '#28a745',
            fontSize: '14px'
          }}>
            ✓ {result}
          </div>
        )}

        {error && (
          <div style={{ 
            marginTop: '15px',
            padding: '12px',
            borderRadius: '8px',
            background: 'rgba(220, 53, 69, 0.1)',
            border: '1px solid rgba(220, 53, 69, 0.3)',
            color: '#dc3545',
            fontSize: '14px'
          }}>
            ✗ {error}
          </div>
        )}
      </div>
    </div>
  );
}
