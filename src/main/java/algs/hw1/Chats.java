package algs.hw1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Chats {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        final ArrayList<String> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(scanner.next());
        }
        final HashSet<String> output = new HashSet<>();
        for (int i = list.size() - 1; i >= 0 ; i--) {
            final String str = list.get(i);
            if (!output.contains(str)) {
                System.out.println(str);
                output.add(str);
            }
        }
    }
}
