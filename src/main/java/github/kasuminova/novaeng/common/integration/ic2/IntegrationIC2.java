package github.kasuminova.novaeng.common.integration.ic2;

import github.kasuminova.mmce.common.concurrent.TaskExecutor;
import github.kasuminova.novaeng.NovaEngineeringCore;
import ic2.core.IC2;
import net.minecraftforge.fml.common.Optional;

public class IntegrationIC2 {

    @Optional.Method(modid = "ic2")
    public static void preInit() {
        int threadCount = TaskExecutor.THREAD_COUNT;
        IC2.getInstance().threadPool.setCorePoolSize(threadCount);
        IC2.getInstance().threadPool.setMaximumPoolSize(threadCount);
        NovaEngineeringCore.log.info("Resized IC2 ThreadPool size to " + threadCount + ".");
    }

}
