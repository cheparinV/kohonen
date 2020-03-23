package csssr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CSSSR {

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine();

        final Map<Character, List<String>> collect =
                Arrays.stream(input.split(" "))
                      .collect(Collectors.groupingBy(str -> str.charAt(0),
                              Collectors.collectingAndThen(Collectors.toList(),
                                      l -> {
                                          l.sort(CSSSR::comparator);
                                          return l;
                                      })));

        final String output = collect.keySet().stream().sorted(Character::compareTo)
                                     .filter(key -> collect.get(key).size() > 1)
                                     .map(key -> groupToString(key, collect.get(key)))
                                     .collect(Collectors.joining(", "));

        System.out.println("[" + output + "]");
    }

    private static int comparator(String o1, String o2) {
        final int compare = Integer.compare(o1.length(), o2.length());
        if (compare == 0) {
            return o1.compareTo(o2);
        }
        return -compare;
    }

    private static String groupToString(Character key, List<String> values) {
        return key + "=[" + values.stream().collect(Collectors.joining(", ")) + "]";
    }
}
