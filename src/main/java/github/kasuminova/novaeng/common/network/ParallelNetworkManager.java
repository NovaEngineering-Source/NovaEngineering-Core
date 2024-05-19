package github.kasuminova.novaeng.common.network;

import github.kasuminova.mmce.common.util.concurrent.Action;
import github.kasuminova.mmce.common.util.concurrent.ActionExecutor;
import github.kasuminova.novaeng.common.mod.Mods;
import hellfirepvp.modularmachinery.ModularMachinery;
import ic2.core.network.NetworkManager;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.FMLEventChannel;

import java.util.PriorityQueue;
import java.util.Queue;

public class ParallelNetworkManager {

    private final Reference2ObjectMap<Object, Queue<ActionExecutor>> groupQueues = new Reference2ObjectOpenHashMap<>();
    private final ReferenceSet<Object> blacklistChannels = new ReferenceOpenHashSet<>();

    public void init() {
        if (Mods.IC2.loaded()) {
            initializeIC2Blacklist();
        }
    }

    @Optional.Method(modid = "ic2")
    private void initializeIC2Blacklist() {
        FMLEventChannel channel = ObfuscationReflectionHelper.getPrivateValue(NetworkManager.class, null, "channel");
        if (channel != null) {
            addBlacklistChannel(channel);
        }
    }

    public void offerAction(final Object group, final Action action) {
        offerAction(group, action, 0);
    }

    public void offerAction(final Object group, final Action action, final int priority) {
        Queue<ActionExecutor> queue = groupQueues.get(group);
        if (queue == null) {
            synchronized (groupQueues) {
                queue = groupQueues.get(group);
                if (queue == null) {
                    groupQueues.put(group, queue = new PriorityQueue<>());
                }
            }
        }
        queue.offer(new ActionExecutor(action, priority));
    }

    public synchronized void execute() {
        for (final Queue<ActionExecutor> queue : groupQueues.values()) {
            ModularMachinery.EXECUTE_MANAGER.addTask(() -> {
                synchronized (queue) {
                    ActionExecutor action;
                    while ((action = queue.poll()) != null) {
                        action.run();
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
