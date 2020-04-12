package leetcode;

import java.util.Arrays;
import java.util.HashSet;

class Solution {

    public static void main(String[] args) {
        System.out.println(maxSubArray(new int[] {-1}));
    }

    public static int maxSubArray(int[] nums) {
        int[][] sum = new int[nums.length + 1][nums.length + 1];
        sum[1][0] = nums[0];
        Arrays.fill(sum[0], nums[0]);
        for (int i = 1; i < nums.length; i++) {
            Arrays.fill(sum[i], i, sum[i].length, nums[i]);
            for (int j = i; j < nums.length; j++) {
                sum[i][j] = sum[i][j - 1] + nums[j];
            }
        }
        final int asInt = Arrays.stream(sum).flatMapToInt(Arrays::stream)
                                .max().getAsInt();

        return asInt;
    }

    public static String toLowerCase(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int letter = (int) str.charAt(i);
            if (letter >= 65 && letter <= 90) {
                letter += 32;
            }
            result.append((char) letter);
        }
        return result.toString();
    }

    public static String defangIPaddr(String address) {

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < address.length() - 1; i++) {
            final char c = address.charAt(i);
            if (c == '.') {
                result.append("[.]");
            }
            result.append(c);
        }
        result.append(address.charAt(address.length() - 1));
        return result.toString();
    }

    public static int numJewelsInStones(String J, String S) {
        final HashSet<Character> characters = new HashSet<>();
        for (char c : J.toCharArray()) {
            characters.add(c);
        }
        int count = 0;
        for (char s : S.toCharArray()) {
            count += characters.contains(s) ? 1 : 0;
        }
        return count;
    }

    public static boolean isHappy(int n) {
        int sum = 0;
        final HashSet<Integer> prevs = new HashSet<>();
        while (sum != 1) {
            sum = 0;
            while (n > 0) {
                int k = n % 10;
                sum += k * k;
                n = (int) n / 10;
            }
            if (prevs.contains(sum)) {
                return false;
            }
            prevs.add(sum);
            n = sum;
        }
        return true;
    }
}