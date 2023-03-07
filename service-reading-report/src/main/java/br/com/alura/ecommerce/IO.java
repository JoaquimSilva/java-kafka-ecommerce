package br.com.alura.ecommerce;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class IO {
    public static void copyTo(Path source, File target) throws IOException {
        if (target.getParentFile().mkdirs()) {
            Files.copy(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void append(File target, String content) {

        try {
            try (FileOutputStream outputStream = new FileOutputStream(target.getPath(), true)) {
                outputStream.write(content.getBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
