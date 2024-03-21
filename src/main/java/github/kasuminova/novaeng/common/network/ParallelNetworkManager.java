package github.kasuminova.novaeng.common.network;

import github.kasuminova.mmce.common.util.concurrent.Action;
import hellfirepvp.modularmachinery.ModularMachinery;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscLinkedAtomicQueue;

import java.util.IdentityHashMap;
import java.util.Map;

public class ParallelNetworkManager {

    private final Map<Object, MpscLinkedAtomicQueue<Action>> groupQueues = new IdentityHashMap<>();

    public void offerAction(final Object group, final Action action) {
        MpscLinkedAtomicQueue<Action> queue = groupQueues.get(group);
        if (queue == null) {
            synchronized (groupQueues) {
                queue = groupQueues.get(group);
                if (queue == null) {
                    groupQueues.put(group, queue = new MpscLinkedAtomicQueue<>());
                }
            }
        }
        queue.offer(action);
    }

    public synchronized void execute() {
        for (final MpscLinkedAtomicQueue<Action> queue : groupQueues.values()) {
            ModularMachinery.EXECUTE_MANAGER.addTask(() -> {
                Action action;
                while ((action = queue.poll()) != null) {
                    action.doAction();
                }
            });
        }
    }

}
