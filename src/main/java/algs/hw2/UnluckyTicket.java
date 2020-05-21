package algs.hw2;

import java.util.Arrays;
import java.util.Scanner;

public class UnluckyTicket {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = n * 2;
        int[] a = new int[n];
        int[] b = new int[n];

        String reading = scanner.next();

        char[] reading1 = reading.toCharArray();

        for (int i = 0; i < n; i++) {
            a[i] = Character.getNumericValue(reading1[i]);
        }

        for (int i = n; i < 2 * n; i++) {
            b[i - n] = Character.getNumericValue(reading1[i]);
        }

        Arrays.sort(a);
        Arrays.sort(b);
        int k1 = 0;
        int k2 = 0;
        for (int i = 0; i < n; i++) {
            if (a[i] > b[i]) {
                k1++;
            }
            if (a[i] < b[i]) {
                k2++;
            }
        }

        if (k1 == n || k2 == n) {
            System.out.println("YES");
        } else {
            System.out.println("NO");
        }
    }
}
