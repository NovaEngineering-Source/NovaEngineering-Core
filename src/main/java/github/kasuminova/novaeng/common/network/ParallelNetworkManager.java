package github.kasuminova.novaeng.common.network;

import appeng.core.sync.network.NetworkHandler;
import github.kasuminova.mmce.common.util.concurrent.Action;
import github.kasuminova.mmce.common.util.concurrent.ActionExecutor;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.mod.Mods;
import hellfirepvp.modularmachinery.ModularMachinery;
import ic2.core.network.NetworkManager;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import mekanism.common.Mekanism;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.FMLEventChannel;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class ParallelNetworkManager {

    private final Map<Object, Queue<ActionExecutor>> groupQueues = new ConcurrentHashMap<>();
    private final ReferenceSet<Object> blacklistChannels = new ReferenceOpenHashSet<>();

    public void init() {
        if (Mods.IC2.loaded()) {
            initializeIC2Blacklist();
        }
        if (Mods.AE2.loaded()) {
            initializeAE2Blacklist();
        }
        if (Mods.MEKCEU.loaded()) {
            initializeMekanismCEuBlacklist();
        }
    }

    @Optional.Method(modid = "ic2")
    private void initializeIC2Blacklist() {
        try {
            FMLEventChannel channel = ObfuscationReflectionHelper.getPrivateValue(NetworkManager.class, null, "channel");
            if (channel != null) {
                addBlacklistChannel(channel);
            }
        } catch (Throwable e) {
            NovaEngineeringCore.log.warn(e);
        }
    }

    @Optional.Method(modid = "appliedenergistics2")
    private void initializeAE2Blacklist() {
        try { 
            FMLEventChannel ec = ObfuscationReflectionHelper.getPrivateValue(NetworkHandler.class, NetworkHandler.instance(), "ec");
            if (ec != null) {
                addBlacklistChannel(ec);
            }
        } catch (Throwable e) {
            NovaEngineeringCore.log.warn(e);
        }
    }

    /**
     * MekCEu is async, so we dont need proxy that.
     */
    @Optional.Method(modid = "mekanism")
    private void initializeMekanismCEuBlacklist() {
        addBlacklistChannel(Mekanism.packetHandler.netHandler);
    }

    public void offerAction(final Object group, final Action action) {
        offerAction(group, action, 0);
    }

    public void offerAction(final Object group, final Action action, final int priority) {
        Queue<ActionExecutor> queue = groupQueues.get(group);
        if (queue == null) {
            synchronized (groupQueues) {
                queue = groupQueues.computeIfAbsent(group, k -> new PriorityQueue<>());
            }
        }
        synchronized (queue) {
            queue.offer(new ActionExecutor(action, priority));
        }
    }

    public void execute() {
        synchronized (groupQueues) {
            ModularMachinery.EXECUTE_MANAGER.addTask(() -> {
                for (final Queue<ActionExecutor> queue : groupQueues.values()) {
                    synchronized (queue) {
                        ActionExecutor action;
                        while ((action = queue.poll()) != null) {
                            action.run();
                        }
                    }
                }
            });
        }
    }

    public boolean isBlacklistChannel(final Object channel) {
        return blacklistChannels.contains(channel);
    }

    public void addBlacklistChannel(final Object channel) {
        blacklistChannels.add(channel);
    }

}
