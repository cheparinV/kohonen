package deep.layer;

public class Coords {

    public Coords(int xInput, int yInput, int xWeight, int yWeight) {
        this.xInput = xInput;
        this.yInput = yInput;
        this.xWeight = xWeight;
        this.yWeight = yWeight;
    }

    private int xInput;

    private int yInput;

    private int xWeight;

    private int yWeight;

    public int[] getInputShape() {
        return new int[]{xInput, yInput};
    }

    public int[] getWeightShape() {
        return new int[]{xWeight, yWeight};
    }

    public int getxInput() {
        return xInput;
    }

    public int getyInput() {
        return yInput;
    }

    public int getxWeight() {
        return xWeight;
    }

    public int getyWeight() {
        return yWeight;
    }
}
