// src/utils/caseConverter.js

/**
 * Конвертирует строку из snake_case в camelCase
 */
export function snakeToCamel(str) {
  return str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
}

/**
 * Конвертирует строку из camelCase в snake_case
 */
export function camelToSnake(str) {
  return str.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`);
}

/**
 * Рекурсивно конвертирует все ключи объекта из snake_case в camelCase
 */
export function keysToCamel(obj) {
  if (Array.isArray(obj)) {
    return obj.map(v => keysToCamel(v));
  } else if (obj !== null && obj !== undefined && obj.constructor === Object) {
    return Object.keys(obj).reduce((result, key) => {
      const camelKey = snakeToCamel(key);
      result[camelKey] = keysToCamel(obj[key]);
      return result;
    }, {});
  }
  return obj;
}

/**
 * Рекурсивно конвертирует все ключи объекта из camelCase в snake_case
 * @param {any} obj - Объект для конвертации
 * @param {boolean} skipHeaders - Если true, не конвертирует ключи внутри объектов 'headers'
 */
export function keysToSnake(obj, skipHeaders = false) {
  if (Array.isArray(obj)) {
    return obj.map(v => keysToSnake(v, skipHeaders));
  } else if (obj !== null && obj !== undefined && obj.constructor === Object) {
    return Object.keys(obj).reduce((result, key) => {
      const snakeKey = camelToSnake(key);
      // Если ключ - "headers" и skipHeaders = true, сохраняем оригинальные ключи внутри
      if (key === 'headers' && skipHeaders) {
        result[snakeKey] = obj[key]; // Не рекурсируем, сохраняем как есть
      } else {
        result[snakeKey] = keysToSnake(obj[key], skipHeaders);
      }
      return result;
    }, {});
  }
  return obj;
}
