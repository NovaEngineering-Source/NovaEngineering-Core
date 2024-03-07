package github.kasuminova.novaeng.mixin.util;

public interface IMixinInGameInfoCore {

    void addPostDrawAction(Runnable action);

    boolean isPostDrawing();

}
