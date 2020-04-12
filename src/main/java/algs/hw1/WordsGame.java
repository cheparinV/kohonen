package algs.hw1;

import java.util.HashSet;
import java.util.Scanner;

public class WordsGame {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        final int m = scanner.nextInt();

        final HashSet<String> first = new HashSet<>();
        final HashSet<String> second = new HashSet<>();
        for (int i = 0; i < n; i++) {
            first.add(scanner.next());
        }
        int sum = 0;
        for (int i = 0; i < m; i++) {
            sum += first.contains(scanner.next()) ? 1 : 0;
        }
        if (n + sum % 2 > m) {
            System.out.println("YES");
        } else {
            System.out.println("NO");
        }
    }
}
