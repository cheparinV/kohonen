package gng;

import java.util.ArrayList;
import java.util.List;

public class Weight {

    public Weight(int size) {
        this.error = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.error.add(0.0);
        }
    }

    private List<Double> error;

    public List<Double> getError() {
        return error;
    }
}
