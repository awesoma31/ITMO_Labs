package com.lab1;

import java.util.ArrayList;
import java.util.List;

public class QuickSort {
    private List<String> tracePoints = new ArrayList<>();
    
    public List<String> getTracePoints() {
        return tracePoints;
    }
    
    public void clearTrace() {
        tracePoints.clear();
    }
    
    public void sort(int[] arr) {
        if (arr == null || arr.length == 0) {
            tracePoints.add("EMPTY_ARRAY");
            return;
        }
        quickSort(arr, 0, arr.length - 1);
    }
    
    private void quickSort(int[] arr, int low, int high) {
        tracePoints.add(String.format("QUICKSORT_START: low=%d, high=%d", low, high));
        
        if (low < high) {
            tracePoints.add("PARTITION_START");
            int pi = partition(arr, low, high);
            tracePoints.add(String.format("PARTITION_END: pivot_index=%d, pivot_value=%d", pi, arr[pi]));
            
            tracePoints.add("RECURSIVE_LEFT_START");
            quickSort(arr, low, pi - 1);
            tracePoints.add("RECURSIVE_LEFT_END");
            
            tracePoints.add("RECURSIVE_RIGHT_START");
            quickSort(arr, pi + 1, high);
            tracePoints.add("RECURSIVE_RIGHT_END");
        } else {
            tracePoints.add("BASE_CASE");
        }
        
        tracePoints.add(String.format("QUICKSORT_END: low=%d, high=%d", low, high));
    }
    
    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        tracePoints.add(String.format("PIVOT_SELECTED: %d", pivot));
        
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            tracePoints.add(String.format("COMPARE: arr[%d]=%d with pivot=%d", j, arr[j], pivot));
            
            if (arr[j] <= pivot) {
                i++;
                tracePoints.add(String.format("SWAP: arr[%d]=%d with arr[%d]=%d", i, arr[i], j, arr[j]));
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        
        tracePoints.add(String.format("FINAL_SWAP: arr[%d]=%d with arr[%d]=%d", i + 1, arr[i + 1], high, arr[high]));
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        
        tracePoints.add(String.format("PARTITION_COMPLETE: pivot_position=%d", i + 1));
        return i + 1;
    }
}