package algs.hw1;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Scanner;

public class Lamps {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        final int m = scanner.nextInt();
        final int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = scanner.nextInt();
        }
        final NumberFormat format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(10);
        final double result = findMaxDiff(array, m) / 2;
        System.out.format("%.10f\n", result);
    }

    private static double findMaxDiff(int[] array, int m) {
        Arrays.sort(array);
        double max = 2 * Math.max(array[0], ((double) m) - array[array.length - 1]);
        for (int i = 0; i < array.length -1 ; i++) {
            final double value = ((double) array[i + 1]) - array[i];
            max = Math.max(max, value);
        }
        return max;
    }
}
