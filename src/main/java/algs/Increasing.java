package algs;

import java.util.Arrays;
import java.util.List;

public class Increasing {

    public static void main(String[] args) {

//        System.out.println(longestSubsequenceLength(Arrays.asList(1, 2, 1, 5)));
//        System.out.println(longestSubsequenceLength(Arrays.asList(0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15)));

        System.out.println(longestSubsequenceLength(Arrays.asList(148, 333, 306, 200, 397, 361, 458, 209, 4, 436, 282, 221, 358, 126, 235, 489, 444, 134, 42, 257, 240, 305, 480, 195, 102, 175, 44, 345, 224, 452, 249, 49, 173, 200, 241, 285, 438, -9, 132, 80, 238, 428, 463, 334, 399, 449, 242, 39, 56, 453, 108, 95, 492, 277, 109, 188, 376, 400, 265, 212, 304, 223, 321, 338, 120, 380, 74, 459, 277, 423, 176, 309, 465, 135, 170, 88, 11, 242, 305, 11, 19, 486, -7, 414, 442, 419, 3, 49, 201, 150, 127, 285, -5, 166, 320, 371, 12, 312, 267, 202, 360, 418, 481, 360, 409, 347, 139, 356, 277, 389, 212, 491, 272, 31, 206, 154, 265, 291, 174, 255, 398, 30, 360, 450, 432, 405, 244, 118, 320, 147, 277, 437, 495, 459, 273, 218, 197, 111, 449, 96, 236, 341, 496, 186, 61, 384, 123, 428, 492, 200, 389, 248, 95, 248, 74, 244, 300, 295, 264, 18, 278, 283, 51, 204, 0, 78, 333, 430, 168, 384, 402, 347, 406, 130, 64, 186, 339, 385, 458, 425, 120, 151, 402)));
        System.out.println(longestSubsequenceLength(Arrays.asList(1, 11, 2, 10, 4, 5, 2, 1)));
    }

    public static int longestSubsequenceLength(final List<Integer> A) {
        if (A.isEmpty()) {
            return 0;
        }
        int[] maxLength = new int[A.size()];
        int[] minLength = new int[A.size()];
        maxLength[0] = 1;
        minLength[0] = 1;

        for (int i = 1; i < A.size(); i++) {
            final Integer current = A.get(i);
            int max = 0;
            int min = 0;
            for (int j = 0; j < i; j++) {
                if (current > A.get(j) && maxLength[j] >= max) {
                    max = maxLength[j];
                }
                if (current < A.get(j)) {
                    if (minLength[j] <= 1 && maxLength[j] >= min) {
                        min = maxLength[j];
                    }
                    if (minLength[j] >= min) {
                       min = minLength[j];
                    }
                }

            }
            maxLength[i] = max + 1;
            minLength[i] = min + 1;
        }
        return Math.max(Arrays.stream(maxLength).max().orElse(0), Arrays.stream(minLength).max().orElse(0));
    }

    public static int lis(final List<Integer> A) {
        int[] arrLength = new int[A.size()];
        arrLength[0] = 1;

        for (int i = 1; i < A.size(); i++) {
            final Integer current = A.get(i);
            int max = 0;
            for (int j = 0; j < i; j++) {
                if (current > A.get(j) && arrLength[j] >= max) {
                    max = arrLength[j];
                }
            }
            arrLength[i] = max + 1;
        }

        return Arrays.stream(arrLength).max().getAsInt();
    }
}
