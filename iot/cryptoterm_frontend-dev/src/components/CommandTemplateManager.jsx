import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { auth } from '../auth';
import CommandTemplateCreator from './CommandTemplateCreator';

/**
 * Компонент управления шаблонами команд ASIC
 * Позволяет просматривать, создавать, редактировать и удалять шаблоны
 */
export default function CommandTemplateManager() {
  const [templates, setTemplates] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showCreator, setShowCreator] = useState(false);
  const [selectedTemplate, setSelectedTemplate] = useState(null);
  const [normalizing, setNormalizing] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);

  // Проверка прав администратора
  useEffect(() => {
    setIsAdmin(auth.isAdmin());
  }, []);

  // Загрузка списка шаблонов
  const loadTemplates = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const token = auth.getToken();
      if (!token) {
        setError('Необходимо войти в систему');
        return;
      }

      const data = await api.getAllCommandTemplates();
      setTemplates(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Загрузка при монтировании
  useEffect(() => {
    loadTemplates();
  }, []);

  // Удаление шаблона
  const handleDelete = async (templateName) => {
    if (!window.confirm(`Удалить шаблон "${templateName}"?`)) {
      return;
    }

    try {
      await api.deleteCommandTemplate(templateName);
      await loadTemplates();
    } catch (err) {
      alert('Ошибка удаления: ' + err.message);
    }
  };

  // Просмотр деталей шаблона
  const handleViewDetails = async (templateName) => {
    try {
      const template = await api.getCommandTemplate(templateName);
      setSelectedTemplate(template);
    } catch (err) {
      alert('Ошибка загрузки: ' + err.message);
    }
  };

  // Нормализация всех шаблонов (только для администратора)
  const handleNormalizeAll = async () => {
    if (!window.confirm('Нормализовать все шаблоны в БД?\n\nЭто приведет miner_model и miner_vendor к нижнему регистру для всех существующих шаблонов.')) {
      return;
    }

    setNormalizing(true);
    setError(null);

    try {
      const result = await api.normalizeAllCommandTemplates();
      alert(`✓ Нормализация завершена!\n\nОбновлено шаблонов: ${result.updatedCount}`);
      await loadTemplates(); // Перезагрузить список
    } catch (err) {
      alert('Ошибка нормализации: ' + err.message);
      setError(err.message);
    } finally {
      setNormalizing(false);
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '1400px', margin: '0 auto' }}>
      {/* Заголовок */}
      <div style={{ marginBottom: '20px' }}>
        <h1 style={{ marginBottom: '10px' }}>🔧 Управление шаблонами команд</h1>
        <p style={{ color: '#999', fontSize: '14px' }}>
          Создавайте и управляйте переиспользуемыми шаблонами команд для различных моделей майнеров
        </p>
      </div>

      {/* Кнопки действий */}
      <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', flexWrap: 'wrap' }}>
        <button
          onClick={() => setShowCreator(!showCreator)}
          className="start-btn"
          style={{ padding: '12px 24px' }}
        >
          {showCreator ? '📋 Показать список' : '➕ Создать новый шаблон'}
        </button>

        {!showCreator && (
          <>
            <button
              onClick={loadTemplates}
              disabled={loading}
              className="secondary"
              style={{ padding: '12px 24px' }}
            >
              {loading ? '⟳ Загрузка...' : '🔄 Обновить'}
            </button>

            {isAdmin && (
              <button
                onClick={handleNormalizeAll}
                disabled={normalizing || loading}
                style={{
                  padding: '12px 24px',
                  background: normalizing ? 'rgba(255, 193, 7, 0.2)' : 'rgba(40, 167, 69, 0.2)',
                  border: normalizing ? '1px solid rgba(255, 193, 7, 0.5)' : '1px solid rgba(40, 167, 69, 0.5)',
                  color: normalizing ? '#ffc107' : '#28a745',
                  borderRadius: '8px',
                  cursor: normalizing ? 'wait' : 'pointer',
                  fontWeight: '600'
                }}
              >
                {normalizing ? '⏳ Нормализация...' : '🔧 Нормализовать все шаблоны'}
              </button>
            )}
          </>
        )}
      </div>

      {/* Форма создания шаблона */}
      {showCreator && (
        <CommandTemplateCreator />
      )}

      {/* Список шаблонов */}
      {!showCreator && (
        <div className="card">
          <h3 style={{ marginBottom: '15px' }}>Существующие шаблоны ({templates.length})</h3>

          {error && (
            <div style={{
              padding: '15px',
              borderRadius: '8px',
              background: 'rgba(220, 53, 69, 0.1)',
              border: '1px solid rgba(220, 53, 69, 0.3)',
              color: '#dc3545',
              marginBottom: '15px'
            }}>
              <strong>✗ Ошибка:</strong> {error}
            </div>
          )}

          {loading ? (
            <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
              ⏳ Загрузка шаблонов...
            </div>
          ) : templates.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
              <p>Шаблонов пока нет</p>
              <button
                onClick={() => setShowCreator(true)}
                className="start-btn"
                style={{ marginTop: '15px' }}
              >
                ➕ Создать первый шаблон
              </button>
            </div>
          ) : (
            <div style={{ display: 'grid', gap: '10px' }}>
              {templates.map(template => (
                <div
                  key={template.name}
                  style={{
                    background: '#1a1a1a',
                    border: '1px solid #333',
                    borderRadius: '8px',
                    padding: '15px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                  }}
                >
                  <div style={{ flex: 1 }}>
                    <div style={{ 
                      fontWeight: 'bold', 
                      fontSize: '16px', 
                      marginBottom: '5px',
                      color: '#5865F2'
                    }}>
                      {template.name}
                    </div>
                    <div style={{ fontSize: '13px', color: '#999', marginBottom: '8px' }}>
                      {template.description}
                    </div>
                    <div style={{ 
                      display: 'flex', 
                      gap: '15px', 
                      fontSize: '12px', 
                      color: '#666'
                    }}>
                      <span>📱 {template.minerModel}</span>
                      <span>🏭 {template.minerVendor}</span>
                      <span>💾 {template.firmware || 'N/A'}</span>
                      <span>📋 Шагов: {template.steps?.length || 0}</span>
                    </div>
                  </div>

                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button
                      onClick={() => handleViewDetails(template.name)}
                      className="secondary"
                      style={{ fontSize: '12px', padding: '8px 16px' }}
                    >
                      👁 Детали
                    </button>
                    <button
                      onClick={() => handleDelete(template.name)}
                      style={{
                        fontSize: '12px',
                        padding: '8px 16px',
                        background: 'rgba(220, 53, 69, 0.2)',
                        border: '1px solid rgba(220, 53, 69, 0.5)',
                        color: '#dc3545',
                        borderRadius: '8px',
                        cursor: 'pointer'
                      }}
                    >
                      🗑 Удалить
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Модальное окно с деталями шаблона */}
      {selectedTemplate && (
        <div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            background: 'rgba(0, 0, 0, 0.8)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
            padding: '20px'
          }}
          onClick={() => setSelectedTemplate(null)}
        >
          <div
            className="card"
            style={{
              maxWidth: '800px',
              maxHeight: '90vh',
              overflow: 'auto',
              margin: 0
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <div style={{ 
              display: 'flex', 
              justifyContent: 'space-between', 
              alignItems: 'center',
              marginBottom: '15px'
            }}>
              <h3 style={{ margin: 0 }}>Детали шаблона: {selectedTemplate.name}</h3>
              <button
                onClick={() => setSelectedTemplate(null)}
                style={{
                  background: 'transparent',
                  border: 'none',
                  color: '#999',
                  fontSize: '24px',
                  cursor: 'pointer',
                  padding: '0',
                  lineHeight: '1'
                }}
              >
                ×
              </button>
            </div>

            <div style={{ marginBottom: '15px' }}>
              <strong>Описание:</strong>
              <p style={{ color: '#999', marginTop: '5px' }}>
                {selectedTemplate.description || 'Нет описания'}
              </p>
            </div>

            <div style={{ 
              display: 'grid', 
              gridTemplateColumns: '1fr 1fr', 
              gap: '10px',
              marginBottom: '15px',
              padding: '10px',
              background: '#1a1a1a',
              borderRadius: '8px'
            }}>
              <div>
                <strong>Модель:</strong> {selectedTemplate.minerModel}
              </div>
              <div>
                <strong>Производитель:</strong> {selectedTemplate.minerVendor}
              </div>
              <div>
                <strong>Прошивка:</strong> {selectedTemplate.firmware || 'N/A'}
              </div>
              <div>
                <strong>Шагов:</strong> {selectedTemplate.steps?.length || 0}
              </div>
            </div>

            <h4 style={{ marginTop: '20px', marginBottom: '10px' }}>
              Шаблон команды (Template JSON):
            </h4>
            <div style={{
              padding: '10px',
              background: 'rgba(88, 101, 242, 0.1)',
              border: '1px solid rgba(88, 101, 242, 0.3)',
              borderRadius: '8px',
              marginBottom: '10px',
              fontSize: '12px',
              color: '#999'
            }}>
              ℹ️ Это шаблон команды. При выполнении он автоматически преобразуется в исполняемую команду с добавлением:
              <ul style={{ marginTop: '8px', marginBottom: '0', paddingLeft: '20px' }}>
                <li>deviceId - ID устройства Raspberry Pi</li>
                <li>cmdId - уникальный ID команды</li>
                <li>asic.ip, asic.id - определяются автоматически устройством</li>
                <li>command: "asic_http_proxy"</li>
              </ul>
            </div>
            <pre style={{
              background: '#0a0a0a',
              border: '1px solid #333',
              borderRadius: '8px',
              padding: '15px',
              fontSize: '12px',
              overflow: 'auto',
              maxHeight: '400px',
              color: '#00ff00',
              fontFamily: 'monospace'
            }}>
              {JSON.stringify(selectedTemplate, null, 2)}
            </pre>
          </div>
        </div>
      )}
    </div>
  );
}
