package github.kasuminova.novaeng.common.hypernet.misc;

public enum ConnectResult {
    SUCCESS(true),

    // Node Only
    UNKNOWN_CENTER(),

    // Center Only
    CENTER_NOT_WORKING(),
    CENTER_REACHED_CONNECTION_LIMIT(),
    UNSUPPORTED_NODE(),
    NODE_TYPE_REACHED_MAX_PRESENCES(),
    ;

    private final boolean success;

    ConnectResult(boolean success) {
        this.success = success;
    }

    ConnectResult() {
        this.success = false;
    }

    public boolean isSuccess() {
        return success;
    }
}
