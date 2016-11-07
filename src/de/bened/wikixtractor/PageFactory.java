package de.bened.wikixtractor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by xuiqzy.
 */
class PageFactory {
    static void extractPages(Path path) {
        try {
            String text = new String(Files.readAllBytes(path),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("error while reading file");
            e.printStackTrace(); //TODO logging
        }
    }
}
