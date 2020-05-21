package algs.hw2;

import java.util.Scanner;

public class Fence {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int k = scanner.nextInt();
        int[] h = new int[n];

        for (int i = 0; i < n; i++) {
            h[i] = scanner.nextInt();
        }

        int min = 0;
        int current = 0;
        int index = 0;

        for (int i = 0; i < k; i++) {
            min += h[i];
        }

        current = min;

        for (int i = k; i < n; i++) {
            current += h[i];
            current -= h[i - k];

            if (current < min) {
                min = current;
                index = i - k + 1;
            }
        }

        System.out.println(index + 1);

    }

}
