package github.kasuminova.novaeng.common.hypernet.proc.server.modifier;

public class CalculateModifier {
    public static final CalculateModifier DEFAULT_MODIFIER = new CalculateModifier() {
        @Override
        public double apply(final double value) {
            return value;
        }

        @Override
        public void add(final double value) {
        }

        @Override
        public void multiply(final double value) {
        }
    };

    protected double add = 0;
    protected double mul = 1;

    public double apply(final double value) {
        return (value + add) * mul;
    }

    public void add(double value) {
        add += value;
    }

    public void multiply(double value) {
        mul *= value;
    }

    public double getAdd() {
        return add;
    }

    public double getMul() {
        return mul;
    }
}
