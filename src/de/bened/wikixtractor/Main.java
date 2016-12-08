package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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
     * logging object for this class
     */
    private final static Logger LOGGER = LogManager.getLogger(Main.class);

    /**
     * @param args first one is path to input file to be parsed, second one is output path to xml file to be created
     */
    public static void main(String[] args) {
        DatabaseManager.initialize();

        if (args.length != 2) {
            LOGGER.error("Incorrect number of arguments: Please provide one path to the file to be parsed and one " +
                    "path the output xml file should be written to!");
            System.exit(-1);
        }

        Path testPath = Paths.get(args[0]);
        HashSet<Page> pages = new HashSet<>();
        try {
            pages = PageFactory.extractPages(testPath);
        } catch (IOException e) {
            // already handled in PageFactory.extractPages(), but abort program now and don't export anything
            System.exit(-1);
        }

        File outputXmlFile = new File(args[1]);
        PageExport.exportPages(pages, outputXmlFile);
    }
}
