import React, { useState } from 'react';
import { api } from '../api';
import { auth } from '../auth';

/**
 * Компонент для создания шаблонов команд ASIC (только для администраторов)
 * Предзаполненный шаблон для смены режима мощности
 */
export default function CommandTemplateCreator() {
  const [templateName, setTemplateName] = useState('');
  const [watts, setWatts] = useState('3495');
  const [hashrate, setHashrate] = useState('132');
  const [minerModel, setMinerModel] = useState('Antminer S19 Pro Hydro');
  const [minerVendor, setMinerVendor] = useState('Bitmain');
  const [firmware, setFirmware] = useState('anthill');
  const [password, setPassword] = useState('admin');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [showJson, setShowJson] = useState(false);

  // Предопределённые режимы для Antminer S19 Pro Hydro
  const predefinedModes = [
    { watts: '3495', hashrate: '132', name: '3495W_132TH', description: 'ECO (Экономичный)' },
    { watts: '3635', hashrate: '136', name: '3635W_136TH', description: 'ECO+' },
    { watts: '3865', hashrate: '143', name: '3865W_143TH', description: 'Сбалансированный низкий' },
    { watts: '4200', hashrate: '149', name: '4200W_149TH', description: 'Сбалансированный' },
    { watts: '4400', hashrate: '160', name: '4400W_160TH', description: 'Средний' },
    { watts: '4855', hashrate: '171', name: '4855W_171TH', description: 'Производительный низкий' },
    { watts: '5150', hashrate: '177', name: '5150W_177TH', description: 'STANDARD (Стандартный)' },
    { watts: '5560', hashrate: '188', name: '5560W_188TH', description: 'Высокий' },
    { watts: '6000', hashrate: '199', name: '6000W_199TH', description: 'Очень высокий' },
    { watts: '6700', hashrate: '210', name: '6700W_210TH', description: 'Максимальный' },
    { watts: '7000', hashrate: '220', name: '7000W_220TH', description: 'OVERCLOCK (Экстремальный)' },
    { watts: '7500', hashrate: '240', name: '7500W_240TH', description: 'Турбо' },
    { watts: '7700', hashrate: '250', name: '7700W_250TH', description: 'Максимум' },
  ];

  // Генерация JSON шаблона команды
  const generateTemplate = () => {
    return {
      name: templateName || `${watts}W_${hashrate}TH`,
      description: `Режим мощности ${watts}W (~${hashrate} TH/s) для ${minerModel}`,
      minerModel: minerModel,
      minerVendor: minerVendor,
      firmware: firmware,
      steps: [
        {
          id: "unlock",
          request: {
            method: "POST",
            path: "/api/v1/unlock",
            headers: {
              "Content-Type": "application/json"
            },
            body: {
              pw: password
            },
            timeoutMs: 10000
          },
          extract: {
            token: "$.token"
          }
        },
        {
          id: `set_profile_${watts}`,
          request: {
            method: "POST",
            path: "/api/v1/settings",
            headers: {
              "Content-Type": "application/json",
              "Authorization": "Bearer ${token}"
            },
            body: {
              miner: {
                overclock: {
                  modded_psu: false,
                  preset: watts
                }
              }
            },
            timeoutMs: 15000
          }
        },
        {
          id: "restart_mining",
          request: {
            method: "POST",
            path: "/api/v1/mining/restart",
            headers: {
              "Content-Type": "application/json",
              "Authorization": "Bearer ${token}"
            },
            body: {},
            timeoutMs: 10000
          }
        }
      ],
      policy: {
        maxRetries: 2,
        retryDelayMs: 2000
      }
    };
  };

  // Применить предустановленный режим
  const applyPredefinedMode = (mode) => {
    setWatts(mode.watts);
    setHashrate(mode.hashrate);
    setTemplateName(mode.name);
  };

  // Создать шаблон
  const handleCreateTemplate = async () => {
    const token = auth.getToken();
    if (!token) {
      setError('Необходимо войти в систему');
      return;
    }

    if (!watts || !hashrate || !minerModel || !minerVendor) {
      setError('Заполните все обязательные поля');
      return;
    }

    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const template = generateTemplate();
      const response = await api.createAsicCommandTemplate(template);
      setSuccess(`Шаблон "${template.name}" успешно создан!`);
      
      // Сброс формы
      setTimeout(() => {
        setSuccess(null);
      }, 5000);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const template = generateTemplate();

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <div className="card">
        <h2 style={{ marginBottom: '10px' }}>🔧 Создание шаблона команды ASIC</h2>
        <p style={{ color: '#999', marginBottom: '20px', fontSize: '14px' }}>
          Создайте переиспользуемый шаблон команды для смены режима мощности. IP адрес и порт ASIC будут автоматически подставлены Raspberry Pi при выполнении.
        </p>

        {/* Предустановленные режимы */}
        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block', marginBottom: '10px', fontWeight: '500' }}>
            Быстрый выбор режима:
          </label>
          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
            gap: '10px'
          }}>
            {predefinedModes.map(mode => (
              <button
                key={mode.name}
                onClick={() => applyPredefinedMode(mode)}
                className="secondary"
                style={{
                  padding: '10px',
                  fontSize: '12px',
                  textAlign: 'left',
                  background: templateName === mode.name ? 'rgba(88, 101, 242, 0.2)' : 'transparent',
                  border: templateName === mode.name ? '2px solid #5865F2' : '1px solid #333'
                }}
              >
                <div style={{ fontWeight: 'bold', marginBottom: '3px' }}>{mode.name}</div>
                <div style={{ fontSize: '11px', opacity: 0.7 }}>{mode.description}</div>
              </button>
            ))}
          </div>
        </div>

        <div style={{ display: 'grid', gap: '15px' }}>
          {/* Основные параметры */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '15px' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
                Мощность (Watts) *
              </label>
              <input
                type="text"
                value={watts}
                onChange={(e) => setWatts(e.target.value)}
                disabled={loading}
                placeholder="3495"
                style={{
                  width: '100%',
                  padding: '10px',
                  borderRadius: '8px',
                  border: '1px solid #333',
                  background: '#1a1a1a',
                  color: '#fff'
                }}
              />
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
                Хешрейт (TH/s) *
              </label>
              <input
                type="text"
                value={hashrate}
                onChange={(e) => setHashrate(e.target.value)}
                disabled={loading}
                placeholder="132"
                style={{
                  width: '100%',
                  padding: '10px',
                  borderRadius: '8px',
                  border: '1px solid #333',
                  background: '#1a1a1a',
                  color: '#fff'
                }}
              />
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
                Имя шаблона
              </label>
              <input
                type="text"
                value={templateName}
                onChange={(e) => setTemplateName(e.target.value)}
                disabled={loading}
                placeholder={`${watts}W_${hashrate}TH`}
                style={{
                  width: '100%',
                  padding: '10px',
                  borderRadius: '8px',
                  border: '1px solid #333',
                  background: '#1a1a1a',
                  color: '#fff'
                }}
              />
              <div style={{ fontSize: '11px', color: '#999', marginTop: '3px' }}>
                По умолчанию: {watts}W_{hashrate}TH
              </div>
            </div>
          </div>

          {/* Информация о майнере */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '15px' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
                Модель майнера *
              </label>
              <input
                type="text"
                value={minerModel}
                onChange={(e) => setMinerModel(e.target.value)}
                disabled={loading}
                placeholder="Antminer S19 Pro Hydro"
                style={{
                  width: '100%',
                  padding: '10px',
                  borderRadius: '8px',
                  border: '1px solid #333',
                  background: '#1a1a1a',
                  color: '#fff'
                }}
              />
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
                Производитель *
              </label>
              <input
                type="text"
                value={minerVendor}
                onChange={(e) => setMinerVendor(e.target.value)}
                disabled={loading}
                placeholder="Bitmain"
                style={{
                  width: '100%',
                  padding: '10px',
                  borderRadius: '8px',
                  border: '1px solid #333',
                  background: '#1a1a1a',
                  color: '#fff'
                }}
              />
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
                Прошивка
              </label>
              <select
                value={firmware}
                onChange={(e) => setFirmware(e.target.value)}
                disabled={loading}
                style={{
                  width: '100%',
                  padding: '10px',
                  borderRadius: '8px',
                  border: '1px solid #333',
                  background: '#1a1a1a',
                  color: '#fff'
                }}
              >
                <option value="anthill">Anthill</option>
                <option value="vnish">Vnish</option>
                <option value="stock">Stock</option>
              </select>
            </div>
          </div>

          {/* Пароль ASIC */}
          <div>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
              Пароль ASIC (для разблокировки)
            </label>
            <input
              type="text"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={loading}
              placeholder="admin"
              style={{
                width: '100%',
                padding: '10px',
                borderRadius: '8px',
                border: '1px solid #333',
                background: '#1a1a1a',
                color: '#fff'
              }}
            />
            <div style={{ fontSize: '11px', color: '#999', marginTop: '3px' }}>
              Используется в шаге unlock для получения токена авторизации
            </div>
          </div>

          {/* Кнопки действий */}
          <div style={{ display: 'flex', gap: '10px' }}>
            <button
              onClick={handleCreateTemplate}
              disabled={loading}
              className="start-btn"
              style={{ flex: 1 }}
            >
              {loading ? '⏳ Создание...' : '✓ Создать шаблон'}
            </button>

            <button
              onClick={() => setShowJson(!showJson)}
              className="secondary"
              style={{ padding: '12px 20px' }}
            >
              {showJson ? '📋 Скрыть JSON' : '👁 Показать JSON'}
            </button>
          </div>

          {/* Сообщения */}
          {success && (
            <div style={{
              padding: '15px',
              borderRadius: '8px',
              background: 'rgba(40, 167, 69, 0.1)',
              border: '1px solid rgba(40, 167, 69, 0.3)',
              color: '#28a745'
            }}>
              <strong>✓ {success}</strong>
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

          {/* JSON предпросмотр */}
          {showJson && (
            <div>
              <h4 style={{ marginBottom: '10px' }}>Предпросмотр JSON:</h4>
              <div style={{ 
                marginBottom: '10px', 
                padding: '10px',
                background: '#1a1a1a',
                borderRadius: '8px',
                fontSize: '12px',
                color: '#ffc107'
              }}>
                <strong>📝 Это шаблон команды</strong> — переиспользуемая заготовка, которая хранится в базе данных.
                <div style={{ marginTop: '8px', fontSize: '11px', opacity: 0.9 }}>
                  При выполнении команды шаблон будет преобразован в полную команду с добавлением:
                  <ul style={{ margin: '5px 0 0 20px', padding: 0 }}>
                    <li><code>deviceId</code> — ID Raspberry Pi</li>
                    <li><code>cmdId</code> — уникальный ID команды</li>
                    <li><code>asic</code> — объект с данными майнера (IP, порт, ID)</li>
                  </ul>
                </div>
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
                {JSON.stringify(template, null, 2)}
              </pre>
              <div style={{ 
                marginTop: '15px', 
                padding: '15px',
                background: '#1a1a1a',
                borderRadius: '8px',
                fontSize: '12px',
                color: '#17a2b8',
                border: '1px solid #17a2b8'
              }}>
                <strong>🚀 Формат команды MQTT (отправляется на Raspberry Pi):</strong>
                <div style={{ 
                  marginTop: '8px', 
                  fontSize: '11px', 
                  opacity: 0.9,
                  marginBottom: '10px'
                }}>
                  Бэкенд автоматически преобразует шаблон в полную команду при выполнении:
                </div>
                <pre style={{ 
                  marginTop: '5px', 
                  color: '#17a2b8',
                  fontFamily: 'monospace',
                  whiteSpace: 'pre-wrap',
                  background: '#0a0a0a',
                  padding: '10px',
                  borderRadius: '5px',
                  fontSize: '11px',
                  overflow: 'auto'
                }}>
{`{
  "deviceId": "rp-uuid",              ← ID Raspberry Pi
  "command": "asic_http_proxy",       ← Тип команды
  "cmdId": "generated-uuid",          ← Уникальный ID команды
  "asic": {
    "ip": "\${address}",              ← Raspberry Pi подставит IP из своей конфигурации
    "firmware": "${firmware}",        ← Из шаблона
    "port": "\${port}",               ← Raspberry Pi подставит порт из своей конфигурации
    "scheme": "http",
    "id": "asic-uuid"                 ← ID майнера
  },
  "steps": [                          ← Из шаблона
    {
      "id": "unlock",
      "request": {
        "method": "POST",
        "path": "/api/v1/unlock",
        "headers": {"Content-Type": "application/json"},
        "body": {"pw": "${password}"},
        "timeoutMs": 10000
      },
      "extract": {"token": "$.token"}
    },
    ...
  ],
  "policy": {                         ← Из шаблона
    "maxRetries": 2,
    "retryDelayMs": 2000
  }
}`}
                </pre>
                <div style={{ 
                  marginTop: '10px', 
                  fontSize: '11px', 
                  opacity: 0.8,
                  borderTop: '1px solid rgba(23, 162, 184, 0.3)',
                  paddingTop: '10px'
                }}>
                  <strong>🔑 Плейсхолдеры (заменяются Raspberry Pi):</strong>
                  <div style={{ marginTop: '5px', fontFamily: 'monospace' }}>
                    • <code>$&#123;address&#125;</code> → реальный IP майнера из <code>structured_config.yaml</code><br/>
                    • <code>$&#123;port&#125;</code> → реальный порт майнера из <code>structured_config.yaml</code><br/>
                    • <code>$&#123;token&#125;</code> → JWT токен из предыдущего шага (extract)
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Информация о структуре */}
      <div className="card" style={{ marginTop: '20px' }}>
        <h3 style={{ marginBottom: '15px' }}>ℹ️ Информация о шаблоне</h3>
        <div style={{ color: '#999', fontSize: '14px', lineHeight: '1.6' }}>
          <p><strong>Что делает шаблон:</strong></p>
          <ol style={{ marginLeft: '20px' }}>
            <li>
              <strong>unlock</strong> - Авторизуется на ASIC через <code>/api/v1/unlock</code>, 
              получает токен и сохраняет его в переменную <code>$&#123;token&#125;</code>
            </li>
            <li>
              <strong>set_profile_&#123;watts&#125;</strong> - Устанавливает профиль мощности 
              через <code>/api/v1/settings</code>, используя токен из предыдущего шага
            </li>
            <li>
              <strong>restart_mining</strong> - Перезапускает майнинг через 
              <code>/api/v1/mining/restart</code> для применения изменений
            </li>
          </ol>
          <p style={{ marginTop: '15px' }}>
            <strong>Примечание:</strong> После создания шаблон будет доступен через API 
            <code>/api/v1/asic-commands/available</code> для выполнения на устройствах 
            с моделью <code>{minerModel}</code>.
          </p>
        </div>
      </div>
    </div>
  );
}
