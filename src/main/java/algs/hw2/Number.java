package algs.hw2;

import java.util.Scanner;

public class Number {

    public static void main(String[] args) {
        int[] a = new int[10];

        final Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        String reading = scanner.next();

        char[] reading1 = reading.toCharArray();

        int cur = 0;
        for (int i = 0; i < reading.length(); i++) {
            int digit = reading1[i] - '0';
            a[digit]++;
            cur += digit;
        }

        int k = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < a[i]; j++) {
                if (cur < n) {
                    k++;
                    cur += 9 - i;
                }
            }
        }

        System.out.println(k);
    }
}