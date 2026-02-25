package com.lab1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

public class CosSeriesTest {
    
    @ParameterizedTest
    @CsvSource({
        "0.0, 5, 1.0",
        "0.0, 1, 1.0",
        "1.0, 10, 0.5403023058681397",
        "1.57079632679, 10, 0.0",
        "3.14159265359, 10, -1.0",
        "6.28318530718, 10, 1.0"
    })
    void testCosSeriesFixedTerms(double x, int terms, double expected) {
        double result = CosSeries.cos(x, terms);
        assertEquals(expected, result, 1e-8, 
            String.format("cos(%f) with %d terms failed", x, terms));
    }
    
    @ParameterizedTest
    @CsvSource({
        "0.0, 1e-10, 1.0",
        "0.5, 1e-10, 0.8775825618903728",
        "1.0, 1e-10, 0.5403023058681398",
        "1.57079632679, 1e-10, 6.123233995736766E-17",
        "3.14159265359, 1e-10, -1.0"
    })
    void testCosWithPrecision(double x, double epsilon, double expected) {
        double result = CosSeries.cosWithPrecision(x, epsilon);
        assertEquals(expected, result, epsilon * 10,
            String.format("cos(%f) with precision %e failed", x, epsilon));
    }
    
    @Test
    void testEdgeCases() {
        // Маленькое значение
        assertEquals(1.0, CosSeries.cos(1e-10, 10), 1e-8);
        
        // Отрицательное значение
        double result = CosSeries.cos(-1.0, 20);
        double expected = Math.cos(-1.0);
        assertEquals(expected, result, 1e-8, "cos(-1.0) failed");
        
        // Для больших значений используем приведение аргумента
        // или увеличиваем количество членов ряда
        double largeX = 100.0;
        
        // Вариант A: Используем свойство периодичности косинуса
        double normalizedX = largeX % (2 * Math.PI);
        result = CosSeries.cos(normalizedX, 50);
        expected = Math.cos(largeX);
        assertEquals(expected, result, 1e-6, 
            String.format("cos(%f) after normalization failed", largeX));
        
        // Вариант B: Увеличиваем количество членов ряда
        result = CosSeries.cos(largeX, 150); // нужно больше членов
        assertEquals(expected, result, 1e-4, 
            String.format("cos(%f) with 150 terms failed", largeX));
    }
    
    @Test
    void testConvergence() {
        double x = 2.0;
        double exact = Math.cos(x);
        
        // Проверяем сходимость для разных количеств членов
        double prevError = Double.MAX_VALUE;
        for (int terms = 1; terms <= 15; terms++) {
            double result = CosSeries.cos(x, terms);
            double error = Math.abs(result - exact);
            
            // Ошибка должна уменьшаться
            if (terms > 3) { // Пропускаем первые члены, где ошибка может расти
                assertTrue(error <= prevError + 1e-10, 
                    String.format("Error increased at terms=%d: %e -> %e", 
                        terms, prevError, error));
            }
            prevError = error;
        }
    }
    
    @Test
    void testNormalization() {
        // Тест периодичности косинуса
        double[] testValues = {0, Math.PI/2, Math.PI, 2*Math.PI, 10*Math.PI, 100.0};
        
        for (double x : testValues) {
            double expected = Math.cos(x);
            double normalized = x % (2 * Math.PI);
            double result = CosSeries.cos(normalized, 30);
            
            assertEquals(expected, result, 1e-6,
                String.format("cos(%f) after normalization failed", x));
        }
    }
}