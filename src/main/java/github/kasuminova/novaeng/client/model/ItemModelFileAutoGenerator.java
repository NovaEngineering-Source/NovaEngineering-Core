package github.kasuminova.novaeng.client.model;

import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ItemModelFileAutoGenerator {

    public static void generate(IResourceManager resourceManager, String newPath) throws IOException {
        IResource itemModelPattern = resourceManager.getResource(
                new ResourceLocation(NovaEngineeringCore.MOD_ID, "models/item/item_model_pattern.json"));

        File newItemModelFile = new File("resources/novaeng_core/models/item/" + newPath + ".json");
        if (newItemModelFile.exists()) {
            return;
        }
        final InputStream inputStream = itemModelPattern.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        final String content = IOUtils.toString(reader).replace("{item_model_pattern}", newPath);

        final FileOutputStream outputStream = FileUtils.openOutputStream(newItemModelFile);
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.write(content);
        writer.flush();

        inputStream.close();
        outputStream.close();
    }

}
