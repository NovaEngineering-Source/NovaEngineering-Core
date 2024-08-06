package github.kasuminova.novaeng.common.block.efabricator.prop;

public enum Levels {

    L4(2, 4),
    L6(4, 8),
    L9(8, 16);

    private final int queueDepthMul;
    private final int energyUsageMul;

    Levels(final int queueDepthMul, final int energyUsageMul) {
        this.queueDepthMul = queueDepthMul;
        this.energyUsageMul = energyUsageMul;
    }

    public int applyOverclockQueueDepth(final int value) {
        return value * this.queueDepthMul;
    }

    public int applyOverclockEnergyUsage(final int value) {
        return value * this.energyUsageMul;
    }

}
