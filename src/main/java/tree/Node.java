package tree;

public class Node {

    private Integer key;

    private Integer height;

    private Node left;

    private Node right;

    public Node(Integer key) {
        this.key = key;
        this.height = 1;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        if (height < 0) {
            height = -height;
        }
        this.height = height;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "Node{" +
                "key=" + key +
                ", height=" + height +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
