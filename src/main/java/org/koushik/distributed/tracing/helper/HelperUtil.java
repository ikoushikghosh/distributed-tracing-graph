package org.koushik.distributed.tracing.helper;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class HelperUtil {

    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
            // check if the path is a directory or not
            File file = new File(path);

            if(!file.exists() ||
                    !file.isFile() ||
                        !file.getAbsolutePath().endsWith(".txt")) return false;

        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }
}
