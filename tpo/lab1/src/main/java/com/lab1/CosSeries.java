package com.lab1;

public class CosSeries {
    
    public static double cos(double x, int terms) {
        if (terms < 1) throw new IllegalArgumentException("Количество членов ряда должно быть положительным");
        
        // Нормализуем x в диапазон [-π, π] для лучшей сходимости
        x = normalizeAngle(x);
        
        double result = 0.0;
        double xSquared = x * x;
        double term = 1.0;
        
        for (int n = 0; n < terms; n++) {
            result += term;
            term *= -xSquared / ((2 * n + 1) * (2 * n + 2));
        }
        
        return result;
    }
    
    public static double cosWithPrecision(double x, double epsilon) {
        if (epsilon <= 0) throw new IllegalArgumentException("Точность должна быть положительной");
        
        // Нормализуем x
        x = normalizeAngle(x);
        
        double result = 0.0;
        double term = 1.0;
        int n = 0;
        
        while (Math.abs(term) > epsilon) {
            result += term;
            n++;
            term *= -x * x / ((2 * n - 1) * (2 * n));
        }
        
        return result;
    }
    
    private static double normalizeAngle(double angle) {
        // Приводим угол к диапазону [-π, π]
        double twoPi = 2 * Math.PI;
        double normalized = angle % twoPi;
        if (normalized > Math.PI) {
            normalized -= twoPi;
        } else if (normalized < -Math.PI) {
            normalized += twoPi;
        }
        return normalized;
    }
}