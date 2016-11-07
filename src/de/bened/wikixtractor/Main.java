package de.bened.wikixtractor;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        Path testPath = Paths.get("/home/xuiqzy/wikipedia_de_prgpr_subset.txt");
        PageFactory.extractPages(testPath);
    }
}
