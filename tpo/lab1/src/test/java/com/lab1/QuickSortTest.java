package com.lab1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class QuickSortTest {
    
    static int[][] testArrays() {
        return new int[][]{
            {5, 2, 8, 1, 9, 3},
            {1},
            {},
            {1, 2, 3, 4, 5},
            {5, 4, 3, 2, 1},
            {3, 3, 3, 3, 3},
            {-5, -2, -8, -1, -9},
            {Integer.MAX_VALUE, 0, Integer.MIN_VALUE}
        };
    }
    
    @ParameterizedTest
    @MethodSource("testArrays")
    void testQuickSortCorrectness(int[] arr) {
        QuickSort sorter = new QuickSort();
        int[] original = arr.clone();
        int[] expected = original.clone();
        Arrays.sort(expected);
        
        sorter.sort(arr);
        
        assertArrayEquals(expected, arr, 
            "Failed for array: " + Arrays.toString(original));
    }
    
    @Test
    void testTracePoints() {
        QuickSort sorter = new QuickSort();
        int[] arr = {5, 2, 8, 1, 9, 3};
        
        sorter.sort(arr);
        List<String> trace = sorter.getTracePoints();
        
        // Более гибкие проверки
        assertNotNull(trace, "Trace should not be null");
        assertFalse(trace.isEmpty(), "Trace should not be empty");
        
        // Проверяем наличие ключевых точек
        boolean hasStart = trace.stream().anyMatch(s -> s.startsWith("QUICKSORT_START"));
        boolean hasEnd = trace.stream().anyMatch(s -> s.startsWith("QUICKSORT_END"));
        
        assertTrue(hasStart, "Should have QUICKSORT_START");
        assertTrue(hasEnd, "Should have QUICKSORT_END");
        
        // Проверяем, что массив отсортирован
        int[] expected = {1, 2, 3, 5, 8, 9};
        assertArrayEquals(expected, arr);
    }
    
    @Test
    void testEmptyArray() {
        QuickSort sorter = new QuickSort();
        int[] arr = {};
        
        sorter.sort(arr);
        List<String> trace = sorter.getTracePoints();
        
        assertTrue(trace.contains("EMPTY_ARRAY") || trace.isEmpty(), 
            "Empty array should be handled");
    }
    
    @Test
    void testSingleElement() {
        QuickSort sorter = new QuickSort();
        int[] arr = {42};
        
        sorter.sort(arr);
        
        assertEquals(42, arr[0], "Single element should remain unchanged");
    }
    
    @Test
    void testAllEqual() {
        QuickSort sorter = new QuickSort();
        int[] arr = {5, 5, 5, 5, 5};
        int[] expected = arr.clone();
        
        sorter.sort(arr);
        
        assertArrayEquals(expected, arr, "All equal elements should remain in same order");
    }
}