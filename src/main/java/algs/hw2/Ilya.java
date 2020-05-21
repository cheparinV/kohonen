package algs.hw2;

import java.util.Scanner;

public class Ilya {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        String reading = scanner.next();
        char[] reading1 = reading.toCharArray();
        int[] sum = new int[reading.length()];
        int n = scanner.nextInt();
        int k = 0;
        sum[0] = 0;
        for (int i = 1; i < reading.length(); i++) {
            if (reading1[i] == reading1[i - 1]) {
                k++;
            }
            sum[i] = k;
        }

        for (int i = 0; i < n; i++) {
            int left = scanner.nextInt();
            int right = scanner.nextInt();
            int answer = sum[right - 1] - sum[left - 1];
            System.out.println(answer);
        }
    }
}