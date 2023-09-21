import java.sql.SQLOutput;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int[] c = new int[9];
        float[] x = new float[20];
        double[][] res = new double[9][20];
        Random rnd = new Random();
        float min = -4f;
        float max = 12f;
        int a = 18;
        int[] checklist = {6, 10, 14, 18};


        // Заполнение списка с
        for (int i = 0; i < 9; i++) {
            c[i] = a;
            a = a - 2;
        }

        // Заполнение списка x
        for (int i = 0; i < x.length; i++) {
            x[i] = rnd.nextFloat(min, max);
        }

        // Заполнение результирующего спискa
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 20; j++) {
                //int[] checklist = {6, 10, 14, 18};
                switch (i) {
                    case 8:
                        res[i][j] = Math.cos(Math.cos(Math.sin(x[j])));
                        break;
                    case 6, 10, 14, 18:
                        res[i][j] = Math.exp(Math.pow(Math.E, (Math.asin((1 / (Math.pow(Math.E, Math.abs(x[j]))))))));
                        break;
                    default:
                        res[i][j] = Math.cbrt(Math.cbrt(Math.pow((Math.pow(((double) 1/3 + x[j]), 2) / 3) / 4, 2)));
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 20; j ++) {
                System.out.printf("%.2f\t", res[i][j]);
            }
            System.out.println();
        }


    }
}
