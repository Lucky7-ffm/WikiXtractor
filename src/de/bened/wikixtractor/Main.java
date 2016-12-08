package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.server.database.Database;

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

        if (args.length == 2) {

            if (args[0].equals("reset")) {
                File databaseFile = new File(args[1]);
                try {
                    DatabaseManager.deleteDatabase(databaseFile);
                    LOGGER.info("Successfully droped database.");
                    DatabaseManager.initialize(databaseFile);
                    LOGGER.info("Successfully created a new database.");
                } catch (Exception e) {
                    LOGGER.error("No database found. Try another path.");
                }
            }
            else if (args[0].equals("categorylinks")) {
                // TODO create categorylinks function
            }
            else if (args[0].equals("articlelinks")) {
                // TODO create articlelinks function
            }
            else {
                argumentFail();
            }
        }
        else if (args.length == 3) {

            if (args[0].equals("importhtml")) {
                File databasePath = new File(args[1]);
                Path htmlPath = Paths.get(args[2]);

                HashSet<Page> pages = new HashSet<>();
                try {
                    DatabaseManager.initialize(databasePath);
                    PageFactory.extractPages(htmlPath);
                } catch (IOException e) {
                    // already handled in PageFactory.extractPages(), but abort program now and don't export anything
                    System.exit(-1);
                }
            }
            else {
                argumentFail();
            }
        }
        else if (args.length == 4) {

            if (args[0].equals("pageinfo")) {
                // TODO create pageinfo function
            }
            else {
                argumentFail();
            }
        }
        else {
            argumentFail();
        }
    }

    static private void argumentFail() {
        LOGGER.error("Incorrect argument: Please check Readme to check valid arguments");
        System.exit(-1);
    }
}
