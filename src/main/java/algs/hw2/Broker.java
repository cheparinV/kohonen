package algs.hw2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Scanner;
import java.util.TreeMap;

public class Broker {

    private static final String B = "B";

    private static final String S = "S";

    public static void main(String[] args) {
        final TreeMap<Integer, Integer> bMap = new TreeMap<>();
        final TreeMap<Integer, Integer> sMap = new TreeMap<>();

        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        final int s = scanner.nextInt();

        for (int i = 0; i < n; i++) {
            final String type = scanner.next();
            final int key = scanner.nextInt();
            final int count = scanner.nextInt();
            if (B.equals(type)) {
                bMap.put(key, bMap.getOrDefault(key, 0) + count);
            } else {
                sMap.put(key, sMap.getOrDefault(key, 0) + count);
            }
        }
        final Iterator<Integer> iterator = sMap.keySet().iterator();
        final ArrayList<Integer> keys = new ArrayList<>();
        for (int i = 0; i < s && iterator.hasNext(); i++) {
            keys.add(iterator.next());
        }
        for (int i = keys.size() - 1; i >= 0; i--) {
            final Integer key = keys.get(i);
            System.out.println("S" + " " + key + " " + sMap.get(key));
        }

        final NavigableSet<Integer> set = bMap.descendingKeySet();
        final Iterator<Integer> bIterator = set.iterator();
        for (int i = 0; i < s && bIterator.hasNext(); i++) {
            final Integer key = bIterator.next();
            System.out.println(B + " " + key + " " + bMap.get(key));
        }
    }
}
