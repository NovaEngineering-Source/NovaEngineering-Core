package github.kasuminova.novaeng.common.network;

import github.kasuminova.mmce.common.util.concurrent.Action;
import hellfirepvp.modularmachinery.ModularMachinery;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscLinkedAtomicQueue;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Queue;

public class ParallelNetworkManager {

    private final Map<Object, Queue<Action>> groupQueues = new IdentityHashMap<>();

    public void offerAction(final Object group, final Action action) {
        Queue<Action> queue = groupQueues.get(group);
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
        for (final Queue<Action> queue : groupQueues.values()) {
            ModularMachinery.EXECUTE_MANAGER.addTask(() -> {
                synchronized (queue) {
                    Action action;
                    while ((action = queue.poll()) != null) {
                        action.doAction();
                    }
                }
            });
        }
    }

}
