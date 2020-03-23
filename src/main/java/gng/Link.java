package gng;

public class Link {

    private final GasNeuron first;

    private final GasNeuron second;

    private int age;

    public Link(GasNeuron first, GasNeuron second) {
        this.first = first;
        this.second = second;
        this.age = 0;
    }

    public GasNeuron notLike(GasNeuron key) {
        if (!first.equals(key)) {
            return first;
        }
        return second;
    }

    public GasNeuron getFirst() {
        return first;
    }

    public GasNeuron getSecond() {
        return second;
    }

    public int getAge() {
        return age;
    }

    public Link setAge(int age) {
        this.age = age;
        return this;
    }

    public int incAge() {
        age++;
        return age;
    }

    @Override
    public String toString() {
        return "Link{" +
                "age=" + age +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Link link = (Link) o;

        if (!(first.equals(link.first) || first.equals(link.second))) {
            return false;
        }
        return second.equals(link.second) || second.equals(link.first);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }
}
