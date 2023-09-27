import static java.lang.Math.*;

public class Main {
    public static void main(String[] args) {
        int[] c = new int[9];
        float[] x = new float[20];
        double[][] res = new double[9][20];
        int a = 18;


        for (int i = 0; i < c.length; i++) {
            c[i] = a;
            a = a - 2;
        }

        for (int i = 0; i < x.length; i++) {
            x[i] = (float) (random() * (16.f) - 4.f);
        }

        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].length; j++) {
                switch (c[i]) {
                    case 8:
                        res[i][j] = cos(cos(sin(x[j])));
                        break;
                    case 6, 10, 14, 18:
                        res[i][j] = exp(pow(E, (asin(1 / (pow(E, abs(x[j])))))));
                        break;
                    default:
                        res[i][j] = cbrt(cbrt(pow((pow((((double) 1 / 3 + x[j]) / x[j]), 2) / 3) / 4, 2)));
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 20; j++) {
                System.out.printf("%.2f\t", res[i][j]);
            }
            System.out.println();
        }
    }
}
