package tree;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NodeUtil {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static Integer parallelHeight(Node p) throws ExecutionException, InterruptedException {
        if (p == null) {
            return 0;
        }
        final Future<Integer> left = executor.submit(() -> height(p.getLeft()));
        final Future<Integer> right = executor.submit(() -> height(p.getRight()));
        if (left.isDone() && right.isDone()) {
            return 1 + Math.max(left.get(), right.get());
        }
        return 1;
    }

    public static Integer height(Node p) {
        return Optional.ofNullable(p)
                       .map(node -> 1 + Math.max(height(p.getLeft()), height(p.getRight())))
                       .orElse(0);
    }

    public static Integer bFactor(Node p) {
        if (p == null) {
            return 0;
        }
        return height(p.getLeft()) - height(p.getRight());
    }

    public static void fixHeight(Node p) {
        Integer height = 0;
        try {
            height = parallelHeight(p);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        p.setHeight(height);
    }

    public static Node rotateRight(Node p) {
        Node node = p.getLeft();
        p.setLeft(node.getRight());
        node.setRight(p);
        fixHeight(p);
        fixHeight(node);
        return node;
    }

    public static Node rotateLeft(Node p) {
        Node node = p.getRight();
        p.setRight(node.getLeft());
        node.setLeft(p);
        fixHeight(p);
        fixHeight(node);
        return node;
    }

    public static Node balance(Node p) {
        fixHeight(p);
        Integer bFactor = bFactor(p);
        if (bFactor == 2) {
            if (bFactor(p.getRight()) < 0) {
                p.setRight(rotateRight(p.getRight()));
            }
            return rotateLeft(p);
        }
        if (bFactor == -2) {
            if (bFactor(p.getLeft()) > 0) {
                p.setLeft(rotateLeft(p.getLeft()));
            }
            return rotateRight(p);
        }
        return p;
    }

    public static Node balanceAll(Node p) {
        fixHeight(p);
        Integer bFactor = bFactor(p);
        if (bFactor > 2 || bFactor < -2) {
            final Future<?> left = executor.submit(() -> p.setLeft(balance(p.getLeft())));
            final Future<?> right = executor.submit(() -> p.setRight(balance(p.getRight())));
            try {
                left.wait();
                right.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return balance(p);
    }

    public static Node insert(Node p, Integer k) {
        if (p == null) {
            return new Node(k);
        }
        if (k < p.getKey()) {
            p.setLeft(insert(p.getLeft(), k));
        } else {
            p.setRight(insert(p.getRight(), k));
        }
        return balance(p);
    }

    public static Node findMin(Node p) {
        return Optional.ofNullable(p.getLeft())
                       .map(NodeUtil::findMin)
                       .orElse(p);
    }

    public static Node removeMin(Node p) {
        if (p.getLeft() == null) {
            return p.getRight();
        }
        p.setLeft(removeMin(p.getLeft()));
        return balance(p);
    }

    public static Node remove(Node p, Integer k) {
        if (p == null) {
            return null;
        }
        if (k < p.getKey()) {
            p.setLeft(remove(p.getLeft(), k));
        }
        if (k > p.getKey()) {
            p.setRight(remove(p.getRight(), k));
        }
        if (k.equals(p.getKey())) {
            final Node left = p.getLeft();
            final Node right = p.getRight();
            if (right == null) {
                return left;
            }
            final Node min = findMin(right);
            min.setRight(removeMin(right));
            min.setLeft(left);
            return balance(min);
        }
        return balance(p);
    }

}
