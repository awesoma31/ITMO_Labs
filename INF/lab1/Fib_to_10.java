import java.util.Scanner;

public class Fib_to_10 {
    public static int fibonacciToDecimal(String fibonacci) {
        // Initialize variables
        int current = 1;
        int next = 2;
        int decimal = 0;
        // Iterate through Fibonacci base digits from right to left
        for (int i = fibonacci.length() - 1; i >= 0; i--) {
            char digit = fibonacci.charAt(i);
            if (digit == '1') {
                decimal += current;
            }
            int temp = current;
            current = next;
            next = temp + next; // Update Fibonacci values
        }
        return decimal;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Input Fibonacci base number as a string
        System.out.print("Введите число в СС Фибоначчи: ");
        String fibonacciBase = scanner.nextLine();
        int decimalResult = fibonacciToDecimal(fibonacciBase);
        System.out.println("Результат перевода в 10-ю СС: " + decimalResult);
    }
}