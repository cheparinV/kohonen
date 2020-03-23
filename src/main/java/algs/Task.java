package algs;

public class Task {

    private static int steps[] = {1, 2, 3, 4};

    public static void main(String[] args) {
        int n = 8;
        final int i = stepCount(4);
        System.out.println(i);
    }

    public static int stepCount(int n) {
        int count = 0;
        for (int i = 0; i < steps.length; i++) {
            for (int i1 = 1; i1 < n; i1++) {
                if (n%steps[i] == 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
