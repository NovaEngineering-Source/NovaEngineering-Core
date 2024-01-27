package github.kasuminova.novaeng.mixin.astralsorcery;

import github.kasuminova.novaeng.NovaEngineeringCore;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

/**
 * 尝试兼容 PlayerDataSQL 的申必还原机制导致服务器崩溃的问题。
 */
@Mixin(PlayerProgress.class)
public class MixinPlayerProgress {
    @Unique
    private static Field novaeng$playerProgressServer = null;

    static {
        try {
            novaeng$playerProgressServer = ResearchManager.class.getDeclaredField("playerProgressServer");
            novaeng$playerProgressServer.setAccessible(true);
        } catch (Exception e) {
            NovaEngineeringCore.log.warn("Failed to get ResearchManager#playerProgressServer!", e);
        }
    }

    @Inject(method = "load", at = @At("HEAD"), remap = false)
    private void onLoadPre(final NBTTagCompound compound, final CallbackInfo ci) {
        EntityPlayerMP player = novaeng$getCurrentPlayer();
        if (player == null) {
            return;
        }

        NovaEngineeringCore.log.info("Try to removing old perk data for player " + player.getGameProfile().getName() + ".");
        try {
            ((InvokerPerkEffectHelper) PerkEffectHelper.EVENT_INSTANCE).invokeHandlePerkModification(player, Side.SERVER, true);
        } catch (Exception e) {
            NovaEngineeringCore.log.warn("Remove failed!", e);
        }
    }

    @Inject(method = "load", at = @At("TAIL"), remap = false)
    private void onLoadPost(final NBTTagCompound compound, final CallbackInfo ci) {
        EntityPlayerMP player = novaeng$getCurrentPlayer();
        if (player == null) {
            return;
        }

        NovaEngineeringCore.log.info("Try to restoring new perk data for player " + player.getGameProfile().getName() + ".");
        try {
            ((InvokerPerkEffectHelper) PerkEffectHelper.EVENT_INSTANCE).invokeHandlePerkModification(player, Side.SERVER, false);
        } catch (Exception e) {
            NovaEngineeringCore.log.warn("Restore failed!", e);
        }
    }

    @Unique
    @SuppressWarnings("unchecked")
    private EntityPlayerMP novaeng$getCurrentPlayer() {
        Map<UUID, PlayerProgress> playerProgress = null;
        try {
            playerProgress = (Map<UUID, PlayerProgress>) novaeng$playerProgressServer.get(null);
        } catch (IllegalAccessException e) {
            return null;
        }

        UUID playerUUID = null;
        for (final Map.Entry<UUID, PlayerProgress> entry : playerProgress.entrySet()) {
            if (entry.getValue() == (Object) this) {
                playerUUID = entry.getKey();
            }
        }
        //noinspection ConstantValue
        if (playerUUID == null) {
            return null;
        }
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerUUID);
    }

}
