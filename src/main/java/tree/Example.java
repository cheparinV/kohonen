package tree;

public class Example {

    public static void main(String[] args) {

        final Node root = new Node(100);
        root.setRight(new Node(120));
        root.setLeft(new Node(50));

        final Node insert = NodeUtil.insert(root, 60);
        System.out.println(insert);

        final Node node = NodeUtil.balanceAll(root);
        System.out.println(node);
    }
}
