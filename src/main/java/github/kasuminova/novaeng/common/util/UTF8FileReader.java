package github.kasuminova.novaeng.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class UTF8FileReader extends InputStreamReader {

    public UTF8FileReader(File file) throws FileNotFoundException {
        super(new FileInputStream(file), StandardCharsets.UTF_8);
    }

}
