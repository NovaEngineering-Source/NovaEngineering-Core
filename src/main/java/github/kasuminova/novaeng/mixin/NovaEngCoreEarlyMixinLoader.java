package github.kasuminova.novaeng.mixin;

import github.kasuminova.novaeng.client.hitokoto.HitokotoAPI;
import github.kasuminova.novaeng.client.util.TitleUtils;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class NovaEngCoreEarlyMixinLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public static final Logger LOG = LogManager.getLogger("NOVAENG_CORE_PRE");

    static {
        if (isCleanroomLoader()) {
            LOG.info("CleanroomLoader detected.");
            checkLauncher();
        }
        CompletableFuture.runAsync(() -> {
            String hitokoto = HitokotoAPI.getRandomHitokoto();
            if (hitokoto == null || hitokoto.isEmpty()) {
                return;
            }
            LOG.info(hitokoto);
            if (isCleanroomLoader()) {
                // CRL only.
                TitleUtils.setRandomTitle();
            }
        });
    }

    /**
     * <a href="https://github.com/GBLodb/PreventCrappyLauncher/blob/retro/src/main/java/gblodb/preventCrappyLauncher/PreventCrappyLauncher.java">Original Source Code</a>
     */
    private static void checkLauncher() {
        int count = 0;

        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            return;
        }

        try {
            String line;
            String cmd = System.getenv("windir") + "\\system32\\" + "tasklist.exe" + " /FO csv /FI \"STATUS eq RUNNING\" | findstr /R \"[Plain Craft Launcher 2]";
            Process pr = new ProcessBuilder(cmd).start();
            SequenceInputStream sis = new SequenceInputStream(pr.getInputStream(), pr.getErrorStream());
            InputStreamReader inst = new InputStreamReader(sis);
            BufferedReader br = new BufferedReader(inst);
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\"")) count++;
            }
        } catch (Exception ignored) {
            // lol
        }

        if (count > 1 && Desktop.isDesktopSupported()) {
            int input = JOptionPane.showConfirmDialog(null,
                    """
                            客户端已侦测到 CleanroomLoader，但是你正在使用不受支持的启动器来启动客户端（也许你没有使用有问题的启动器启动客户端）。
                            使用不兼容的启动器会出现预期外的问题，并导致性能下降。
                            如果可能，请检查你的整合包的版本是否是最新版本。你可以点击“确定”强制启动客户端，但是这可能会导致大概率游戏崩溃。
                            """, "不受支持的启动器",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (input != JOptionPane.YES_OPTION) {
                throw new RuntimeException("Unsupported launcher detected.");
            }
        }
    }

    public static boolean isCleanroomLoader() {
        try {
            Class.forName("com.cleanroommc.boot.Main");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList(
                "mixins.novaeng_core_vanilla.json"
        );
    }

    // Noop

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(final Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
