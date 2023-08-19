package github.kasuminova.novaeng.common.hypernet.misc;

public enum ConnectResult {
    SUCCESS(true),
    CENTER_NOT_WORKING(false),
    CENTER_REACHED_CONNECTION_LIMIT(false),
    UNSUPPORTED_NODE(false),
    NODE_TYPE_REACHED_MAX_PRESENCES(false),
    ;

    private final boolean success;

    ConnectResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
