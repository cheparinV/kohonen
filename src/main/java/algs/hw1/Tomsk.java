package algs.hw1;

import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;

public class Tomsk {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        final int m = 1000_000 - scanner.nextInt();

        final TreeMap<Double, Integer> values = new TreeMap<>();
        int sum = 0;
        for (int i = 0; i < n; i++) {
            final int a = scanner.nextInt();
            final int b = scanner.nextInt();
            final int k = scanner.nextInt();
            sum += k;
            final double key = Math.sqrt(a * a + b * b);
            values.put(key, values.getOrDefault(key, 0) + k);
        }
        if (sum < m) {
            System.out.println(-1);
        } else {
            int all = 0;
            Iterator<Double> iterator = values.keySet().iterator();
            int newSum = 0;
            Double current = 0.0;
            while (iterator.hasNext() && newSum < m) {
                current = iterator.next();
                newSum += values.get(current);
            }
            System.out.format("%.7f\n", current);
        }
    }
}
