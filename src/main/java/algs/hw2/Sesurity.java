package algs.hw2;

import java.util.Scanner;

public class Sesurity {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();

        final int[][] a = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = scanner.nextInt();
            }
        }

        final int[][] f = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                f[i][j] = Math.max(i - 1 >= 0 ? f[i - 1][j] : 0,
                        j - 1 >= 0 ? f[i][j - 1] : 0)
                        + a[i][j];
            }
        }

        System.out.println(f[n-1][n-1]);
    }
}
