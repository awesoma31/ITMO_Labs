-- V4__metric_type_integer.sql

-- 1. Добавляем колонку miner_id, если её еще нет
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='other_metric' AND column_name='miner_id') THEN
ALTER TABLE other_metric ADD COLUMN miner_id UUID;
END IF;
END $$;

-- 2. Добавляем внешний ключ (предварительно удалив старый, чтобы не было дублей)
ALTER TABLE other_metric DROP CONSTRAINT IF EXISTS other_metric_miner_id_fkey;
ALTER TABLE other_metric
    ADD CONSTRAINT other_metric_miner_id_fkey
        FOREIGN KEY (miner_id)
            REFERENCES miner(id);

-- 3. Безопасное обновление PK в metric_type
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name='metric_type_pkey') THEN
        -- Если нужно что-то менять в PK, но обычно он уже есть после V3
        NULL;
END IF;
END $$;

-- 4. Установка NOT NULL на miner_id
-- ВАЖНО: Если в таблице есть данные, эта команда упадет, пока вы не заполните miner_id.
-- Если данные ЕСТЬ, сначала выполните: UPDATE other_metric SET miner_id = 'какой-то-uuid' WHERE miner_id IS NULL;
-- Пока закомментируем или оставим, если данных еще нет.
ALTER TABLE other_metric ALTER COLUMN miner_id SET NOT NULL;