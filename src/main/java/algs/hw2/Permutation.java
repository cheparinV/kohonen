package algs.hw2;

import java.util.Scanner;

public class Permutation {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        int[] a = new int[n];
        int error = 0;
        for (int i = 0; i < n; i++) {
            final int val = scanner.nextInt();
            if (val <= n) {
                a[val - 1]++;
            } else {
                error++;
            }
        }

        for (int i = 0; i < a.length; i++) {
            if (a[i] > 1) {
                error += a[i] - 1;
            }
        }
        System.out.println(error);
    }
}
