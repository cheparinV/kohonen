package algs.hw2;

import java.util.Scanner;

public class Chess {

    private int cols;

    private int rows;

    private int size;

    private int[] y;

    private int count = 0;

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        int k = scanner.nextInt();
        final Chess chess = new Chess();
        chess.rows = n;
        chess.cols = n;
        chess.size = k - 1;
        for (int i = 0; i <= chess.size && i < chess.cols; i++) {
            chess.y = new int[chess.rows];
            chess.putQueen(i, i);
        }
        System.out.println(chess.count);
    }

    public void putQueen(int x, int start) {
        if (x == cols) {
            return;
        } else {
            for (y[x] = 0; y[x] < rows; y[x]++) {
                if (correct(x, start)) {
                    if (x - start >= size) {
                        count++;
                    } else {
                        putQueen(x + 1, start);
                    }
                }
            }
            if (x - start >= size) {
                putQueen(x + 1, start);
            }
        }
    }

    public boolean correct(int x, int start) {
        for (int x1 = start; x1 < x && x1 < size + start; x1++) {
            if (y[x1] == y[x]
                    || Math.abs(x - x1) == Math.abs(y[x] - y[x1])) {
                return false;
            }
        }
        return true;
    }
}
