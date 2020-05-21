package algs.hw2;

import java.util.Arrays;
import java.util.Scanner;

public class Sahir {

    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int[] left = new int[n];
        int[] right = new int[n];
        int max = -1;

        Arrays.fill(left, m + 1);

        for (int i = n - 1; i >= 0; i--) {
            String s = scanner.next();
            for (int j = 0; j < m + 2; j++)
                if (s.charAt(j) == '1') {
                    right[i] = j;
                    if (max == -1) {
                        max = i;
                    }
                }

            for (int j = m + 1; j >= 0; --j)
                if (s.charAt(j) == '1') {
                    left[i] = j;
                }
        }

        int ans = Integer.MAX_VALUE;

        final int pow = (1 << n - 1);
        for (int stairs = 0; stairs < pow; stairs++) {
            int cur = 0, room = 0, floor = 0;
            while (floor <= max) {
                if (room == 0) {
                    cur = cur + right[floor] - room;
                    room = right[floor];
                } else {
                    cur = cur + room - left[floor];
                    room = left[floor];
                }

                if (floor == max) {
                    break;
                }

                int next;
                final int i1 = 1 << floor;
                final int i = stairs & i1;
                if (i == 0) {
                    next = 0;
                } else {
                    next = m + 1;
                }
                cur = cur + Math.abs(next - room) + 1;

                room = next;
                floor++;
            }

            if (ans > cur) {
                ans = cur;
            }
        }

        System.out.println(ans);
    }
}