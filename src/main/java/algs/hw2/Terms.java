package algs.hw2;

import java.util.Scanner;

public class Terms {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        final int m = scanner.nextInt();
        int sum = 0;
        for (int i = 1; i <= m; i++) {
            sum += func(n, i, n);
        }
        System.out.println(sum);
    }

    public static long func(int n, int m, int k) {
        if (0 < m && m <= n && 0 < k && k <= n) {
            return func(n, m, k -1) + func(n-k, m-1, k);
        }
        if (k > n) {
            return func(n, m, n);
        }
        if (n == 0 && m == 0) {
            return 1;
        }
        return 0;
    }
}
