package github.kasuminova.novaeng.common.util;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    @SideOnly(Side.CLIENT)
    public static String formatResearchRequiredTime(long time) {
        long t = time;

        long hours = TimeUnit.HOURS.convert(t, TimeUnit.MILLISECONDS);
        if (hours > 0) {
            t -= TimeUnit.HOURS.toMillis(hours);
        }
        long minutes = TimeUnit.MINUTES.convert(t, TimeUnit.MILLISECONDS);
        if (minutes > 0) {
            t -= TimeUnit.MINUTES.toMillis(minutes);
        }
        long seconds = TimeUnit.SECONDS.convert(t, TimeUnit.MILLISECONDS);

        return I18n.format("gui.terminal_controller.screen.info.start.time",
                hours, minutes, seconds, time / 50
        );
    }
}
