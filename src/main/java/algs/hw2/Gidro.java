package algs.hw2;

import java.util.Scanner;

public class Gidro {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        int[] grad = new int[n];
        int k = 0;
        int max = 0;

        for (int i = 0; i < n; i++) {
            grad[i] = scanner.nextInt();
            if (Math.abs(grad[i]) % 2 == 0 && grad[i] < 0) {
                k++;
            } else {
                if (k > max) {
                    max = k;
                }
                k = 0;
            }
        }
        if (k > max) {
            max = k;
        }

        System.out.println(max);

    }
}
