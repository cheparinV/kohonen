package algs.hw2;

import java.util.HashMap;
import java.util.Scanner;

public class Reposts {

    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        final HashMap<String, Integer> map = new HashMap<>();

        map.put("polycarp", 1);
        int k=1;

        for (int i = 0; i < n; i++) {
            final String a = scanner.next().toLowerCase();
            scanner.next();
            final String b = scanner.next().toLowerCase();
            int length = map.compute(
                    a, (m, v) -> map.get(b) + (v == null ? 1 : v)
            );
            k = Math.max(k, length);
        }

        System.out.println(k);

    }
}
