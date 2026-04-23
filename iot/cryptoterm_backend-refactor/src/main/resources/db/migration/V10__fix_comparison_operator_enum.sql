-- Fix comparison_operator ENUM to use Java enum names instead of symbols
-- This allows Hibernate @Enumerated(EnumType.STRING) to work correctly

-- Drop the old enum type (need to remove column first)
ALTER TABLE condition ALTER COLUMN comparison_operator TYPE TEXT;
DROP TYPE IF EXISTS comparison_operator;

-- Create new enum with Java enum names
CREATE TYPE comparison_operator AS ENUM ('eq', 'gt', 'lt', 'gte', 'lte');

-- Convert existing data from symbols to enum names
UPDATE condition SET comparison_operator = 
    CASE comparison_operator
        WHEN '==' THEN 'eq'
        WHEN '>' THEN 'gt'
        WHEN '<' THEN 'lt'
        WHEN '>=' THEN 'gte'
        WHEN '<=' THEN 'lte'
        ELSE comparison_operator
    END;

-- Change column type back to enum
ALTER TABLE condition 
    ALTER COLUMN comparison_operator TYPE comparison_operator 
    USING comparison_operator::comparison_operator;
