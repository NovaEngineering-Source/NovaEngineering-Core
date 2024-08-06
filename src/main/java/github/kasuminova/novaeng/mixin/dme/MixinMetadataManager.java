package github.kasuminova.novaeng.mixin.dme;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import github.kasuminova.novaeng.common.util.UTF8FileReader;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.metadata.MetadataManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Mixin(MetadataManager.class)
public class MixinMetadataManager {

    /**
     * @author Kasumi_Nova
     * @reason UTF-8!
     */
    @Overwrite(remap = false)
    private static Optional<JsonArray> readConfigFile(File file) {
        String filename = file.getName();

        UTF8FileReader fileReader;
        try {
            fileReader = new UTF8FileReader(file);
        } catch (FileNotFoundException var9) {
            FileNotFoundException e = var9;
            DMLRelearned.logger.error("Config file \"{}\" not found! Error message: {}", filename, e.getMessage());
            return Optional.empty();
        }

        JsonElement result;
        try {
            JsonReader reader = new JsonReader(fileReader);

            try {
                JsonParser parser = new JsonParser();
                reader.setLenient(true);
                result = parser.parse(reader);
            } catch (Throwable var8) {
                try {
                    reader.close();
                } catch (Throwable var7) {
                    var8.addSuppressed(var7);
                }

                throw var8;
            }

            reader.close();
        } catch (Exception var10) {
            Exception e = var10;
            if (e instanceof IOException) {
                DMLRelearned.logger.error("Error reading config file \"{}\"! Error message: {}", filename, e.getMessage());
            } else if (e instanceof JsonSyntaxException) {
                DMLRelearned.logger.error("Invalid JSON in config file \"{}\"! Error message: {}", filename, e.getMessage());
            } else {
                DMLRelearned.logger.error("Exception while reading config file \"{}\"! Error message: {}", filename, e.getMessage());
            }

            return Optional.empty();
        }

        if (!result.isJsonArray()) {
            DMLRelearned.logger.error("Error parsing config file \"{}\": root element must be an array!", filename);
            return Optional.empty();
        } else {
            return Optional.of(result.getAsJsonArray());
        }
    }

}
