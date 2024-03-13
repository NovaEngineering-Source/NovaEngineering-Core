package github.kasuminova.novaeng.client.util;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.hitokoto.HitokotoAPI;
import org.lwjgl.opengl.Display;

import java.util.concurrent.CompletableFuture;

public class TitleUtils {
    /**
     * TODO 喜欢我硬编码吗.jpg
     */
    public static final String DEFAULT_TITLE = "Nova Engineering: World 1.9.1 by Hikari_Nova | Core Ver: " + NovaEngineeringCore.VERSION;
    public static final String VANILLA_TITLE = "Minecraft 1.12.2";

    public static String currentTitle = null;
    public static String lastCurrentTitle = null;

    /**
     * 设置一言随机标题，必须在客户端主线程使用。
     * 如果一言缓存为空，则尝试重新获取一言。
     *
     * @param state 当前状态
     */
    public static void setRandomTitle(final String state) {
        lastCurrentTitle = currentTitle;

        String hitokotoCache = HitokotoAPI.getHitokotoCache();
        if (hitokotoCache != null) {
            currentTitle = buildTitle(state, hitokotoCache);
            Display.setTitle(currentTitle);
        } else {
            CompletableFuture.runAsync(HitokotoAPI::getRandomHitokoto);
            currentTitle = buildTitle(state, null);
            Display.setTitle(currentTitle);
        }
    }

    /**
     * 设置一言随机标题，必须在客户端主线程使用。
     * 如果一言缓存为空，则尝试重新获取一言。
     */
    public static void setRandomTitle() {
        lastCurrentTitle = currentTitle;

        String hitokotoCache = HitokotoAPI.getHitokotoCache();

        if (hitokotoCache != null) {
            currentTitle = buildTitle(null, hitokotoCache);
            Display.setTitle(currentTitle);
        } else {
            currentTitle = buildTitle(null, null);
            Display.setTitle(currentTitle);
        }
    }

    /**
     * 设置一言随机标题，可以在其他线程使用。
     *
     * @param state 当前状态
     */
    public static void setRandomTitleSync(String state) {
        lastCurrentTitle = currentTitle;
        currentTitle = buildTitle(state, HitokotoAPI.getHitokotoCache());
    }

    /**
     * 设置一言随机标题，可以在其他线程使用。
     */
    public static void setRandomTitleSync() {
        lastCurrentTitle = currentTitle;
        currentTitle = buildTitle(null, HitokotoAPI.getHitokotoCache());
    }

    public static String buildTitle(final String state, final String hitokoto) {
        if (state == null) {
            if (hitokoto == null) {
                currentTitle = String.format("%s", DEFAULT_TITLE);
            }
            return String.format("%s | %s", DEFAULT_TITLE, hitokoto);
        }
        if (hitokoto == null) {
            return String.format("%s | %s", DEFAULT_TITLE, state);
        }

        return String.format("%s | %s | %s", DEFAULT_TITLE, state, hitokoto);
    }

    public static void checkTitleState() {
        if (currentTitle == null) {
            return;
        }

        String title = Display.getTitle();
        if (!title.equals(currentTitle)) {
            if (!title.equals(TitleUtils.VANILLA_TITLE) && !title.equals(lastCurrentTitle)) {
                NovaEngineeringCore.log.debug("Invalid title: {}, Excepted: {}", title, lastCurrentTitle);
//                // 嗯？
//                Minecraft.getMinecraft().shutdown();
                return;
            }
        }
        Display.setTitle(currentTitle);
    }
}
