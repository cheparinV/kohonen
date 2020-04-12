package algs.hw1;

import java.util.Scanner;

public class Songs {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        final int m = scanner.nextInt();
        final int[] data = new int[n];
        data[0] = scanner.nextInt() * scanner.nextInt();
        for (int i = 1; i < n; i++) {
            data[i] = scanner.nextInt() * scanner.nextInt() + data[i - 1];
        }
        int prev = 0;
        int[] moments = new int[m];
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < m; i++) {
            moments[i] = scanner.nextInt();
            prev = binarySearch(data, prev, data.length, moments[i]);
            output.append(prev + 1)
                  .append("\n");
        }
        System.out.println(output.toString());
    }

    private static int binarySearch(int[] a, int fromIndex, int toIndex, int key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];
            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return low;
    }

    private static int binarySearchSecond(int[] a, int fromIndex, int toIndex, int key) {
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
