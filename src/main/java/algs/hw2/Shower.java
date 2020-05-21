package algs.hw2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Shower {

    static List<int[]> lists = new ArrayList<>();

    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);
        int[] list = new int[] {0, 1, 2, 3, 4};
        perm(list, list.length, list.length);
        int n = 5;
        final int[][] a = new int[n][n];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] = scanner.nextInt();
            }
        }

        int max = 0;
        for (int[] ints : lists) {
            int sum = 0;
            sum += a[ints[0]][ints[1]] + a[ints[1]][ints[0]];
            sum += a[ints[2]][ints[3]] + a[ints[3]][ints[2]];
            sum += a[ints[2]][ints[1]] + a[ints[1]][ints[2]];
            sum += a[ints[4]][ints[3]] + a[ints[3]][ints[4]];
            sum += a[ints[2]][ints[3]] + a[ints[3]][ints[2]];
            sum += a[ints[4]][ints[3]] + a[ints[3]][ints[4]];
            if (sum > max) {
                max = sum;
            }
        }

        System.out.println(max);

    }

    static void perm(int a[], int size, int n) {
        if (size == 1) {
            lists.add(Arrays.copyOf(a, a.length));
        }
        for (int i = 0; i < size; i++) {
            perm(a, size - 1, n);
            if (size % 2 == 1) {
                int temp = a[0];
                a[0] = a[size - 1];
                a[size - 1] = temp;
            } else {
                int temp = a[i];
                a[i] = a[size - 1];
                a[size - 1] = temp;
            }
        }
    }
}
