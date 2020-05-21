package algs.hw2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Saw {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        int b = scanner.nextInt();
        int counter = 0;

        final List<Integer> costs = new ArrayList<>();
        int prev = scanner.nextInt();
        if (prev % 2 == 0) {
            counter++;
        } else {
            counter--;
        }
        for (int i = 1; i < n; i++) {
            final int val = scanner.nextInt();
            if (counter == 0) {
                costs.add(Math.abs(val - prev));
            }
            if (val % 2 == 0) {
                counter++;
            } else {
                counter--;
            }
            prev = val;
        }

        costs.sort(Integer::compareTo);
        int i = 0;
        while (b >= 0 && i < costs.size()) {
            b -= costs.get(i);
            i++;
        }
        if (b < 0) {
            i--;
        }
        System.out.println(i);
    }
}
