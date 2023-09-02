package github.kasuminova.novaeng.client.gui.misc;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TechLevelText {
    private final String levelText;
    private final String subLevelText;

    public TechLevelText(float level) {
        String[] strings = splitSubLevel(level);
        levelText = strings[0];
        subLevelText = strings[1];
    }

    public static String getLevelText(float level) {
        int intLevel = (int) level;
        return I18n.format("novaeng.hypernet.tech_level." + intLevel);
    }

    public static String getSubLevelText(float level) {
        float subLevel = level - (int) level;
        return String.valueOf(Math.round(subLevel * 10F));
    }

    public static String[] splitSubLevel(float level) {
        // Example: §a§l§n?§a
        String levelText = getLevelText(level);
        // Example: §a.?
        String subLevelText = levelText.substring(levelText.length() - 3) + getSubLevelText(level);

        return new String[]{levelText, subLevelText.replace(".", "")};
    }

    public String getLevelText() {
        return levelText;
    }

    public String getSubLevelText() {
        return subLevelText;
    }
}
