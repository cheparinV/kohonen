package algs.hw2;

import java.util.ArrayList;
import java.util.Scanner;

public class Line {

    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);
        String reading1 = scanner.next();
        String reading2 = scanner.next();

        char[] s = reading1.toCharArray();
        char[] t = reading2.toCharArray();
        char[] p = reading1.toCharArray();

        int n = s.length;
        int different = n;

        ArrayList<Integer> index = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (Character.getNumericValue(s[i]) == Character.getNumericValue(t[i])) {
                different--;
            } else {
                index.add(i);
            }
        }

        if (different == 0) {
            for (int i = 0; i < n; i++)
                System.out.print(s[i]);
        }

        if (different % 2 != 0) {
            System.out.println("impossible");
        }

        if (different % 2 == 0 && different != 0) {
            for (int i = 0; i < index.size(); i = i + 2) {
                if (t[index.get(i)] == '0') {
                    p[index.get(i)] = '1';
                } else {
                    p[index.get(i)] = '0';
                }

                if (s[index.get(i + 1)] == '0') {
                    p[index.get(i + 1)] = '1';
                } else {
                    p[index.get(i + 1)] = '0';
                }
            }

            for (int i = 0; i < n; i++)
                System.out.print(p[i]);

        }
    }
}