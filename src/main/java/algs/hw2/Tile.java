package algs.hw2;

import java.util.Scanner;

public class Tile {

    static long[][][] cache;

    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        cache = new long[n + 2][101][101];
        int aVal = scanner.nextInt();
        int bVal = scanner.nextInt();
        final long func = func(n + 1, aVal, bVal);
        System.out.println(func);
    }

    public static long func(int pos, int a, int b) {
        final long fromCache = cache[pos][a][b];
        if (fromCache > 0) {
            return fromCache;
        }
        if (pos == 1) {
            //cache[pos][a][b] = 1;
            return 1;
        }
        long sum = 0;
        if (a > 0) {
            sum += func(pos - 1, a - 1, b);
        }
        if (b > 0 && (pos - 3) >= 1) {
            sum += func(pos - 3, a, b - 1);
        }
        cache[pos][a][b] = sum % 1000000009;
        //System.out.println("pos = " + pos + ", a = " + a + ", b = " + b + ", value = " + sum);
        return sum % 1000000009;
    }
}
