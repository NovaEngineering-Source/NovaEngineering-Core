package github.kasuminova.novaeng.common.integration.ic2;

import github.kasuminova.mmce.common.concurrent.TaskExecutor;
import github.kasuminova.novaeng.NovaEngineeringCore;
import ic2.core.IC2;
import ic2.core.util.PriorityExecutor;
import net.minecraftforge.fml.common.Optional;

public class IntegrationIC2 {

    @Optional.Method(modid = "ic2")
    public static void preInit() {
        PriorityExecutor pool = IC2.getInstance().threadPool;
        int threadCount = Math.min(TaskExecutor.THREAD_COUNT, pool.getMaximumPoolSize());
        pool.setCorePoolSize(threadCount);
        pool.setMaximumPoolSize(threadCount);
        NovaEngineeringCore.log.info("Resized IC2 ThreadPool size to " + threadCount + ".");
    }

}
