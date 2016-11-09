package de.bened.wikixtractor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

/**
 * <h1>Main</h1>
 * Handles user input on command line, calls extraction of pages and calls writing to xml file
 *
 * @author xuiqzy
 * @since 07.11.2016
 */

class Main {

    /**
     * @param args first one is path to input file to be parsed, second one is output path to xml file to be created
     */
    public static void main(String[] args) {
        // TODO convert program to use input and output file from arguments
        Path testPath = Paths.get("C:/Users/pohls/OneDrive/Documents/WikiXtractor/wikipedia_de_prgpr_subset.txt");
        try {
            HashSet<Page> pages = PageFactory.extractPages(testPath);
            PageExport.exportPages(pages);
        } catch (IOException e) {
            // already handled in PageFactory.extractPages(), but abort program now
        }
    }
}
