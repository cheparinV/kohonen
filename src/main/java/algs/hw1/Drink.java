package algs.hw1;

import java.util.Arrays;
import java.util.Scanner;

public class Drink {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        scanner.nextLine();
        final int[] data = Arrays.stream(scanner.nextLine().split(" "))
                                 .mapToInt(Integer::parseInt).toArray();
        final int m = scanner.nextInt();
        scanner.nextLine();
        final int array[] = new int[m];
        for (int i = 0; i < m; i++) {
            array[i] = scanner.nextInt();
        }
        search(n, data, m, array);

        //search(5, new int[] {3, 10, 8, 6, 11}, 4, new int[] {1, 10, 3, 11});
    }

    private static void search(int n, int[] data, int m, int[] array) {
        Arrays.sort(data);
        for (int i : array) {
            int x = binarySearch0(data, 0, data.length, i);
            System.out.print(x + " ");
        }
    }

    private static int binarySearch0(int[] a, int fromIndex, int toIndex, int key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];

            if (midVal <= key) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return low;
    }
}
