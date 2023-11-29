package github.kasuminova.novaeng.common.hypernet.proc.server.modifier;

public class CalculateModifier {
    protected float add = 0;
    protected float mul = 1;

    public double apply(final double value) {
        return (value + add) * mul;
    }

    public void add(float value) {
        add += value;
    }

    public void multiply(float value) {
        mul *= value;
    }

    public float getAdd() {
        return add;
    }

    public float getMul() {
        return mul;
    }
}
