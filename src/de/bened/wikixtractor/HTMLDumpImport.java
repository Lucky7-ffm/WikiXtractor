package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <h1>HTMLDumpImport</h1>
 * Creates a new Database with the given HTML dump data
 *
 * @author symdox
 * @since 16.01.2017
 */

class HTMLDumpImport extends Task {

    private final static Logger LOGGER = LogManager.getLogger(HTMLDumpImport.class);

	TaskScheduler.TaskType[] preconditions = null;
	TaskScheduler.TaskType[] postconditions = null;

    @Override String getDescription(){
        return "Creates a new Database with the given HTML dump data";
    }

    @Override void run(String[] args){
        File databaseDirectory = new File(args[0]);
        Path pathToWikipediaFile = Paths.get(args[1]);
        initializeDatabase(databaseDirectory);

    }

    private static void initializeDatabase(File databaseDirectory) {
        try {
            DatabaseManager.initialize(databaseDirectory);
        } catch (Exception e) {
            // error while initializing database
            // for creation of indices and waiting for them to come online, it is already handled in DatabaseManager
            LOGGER.error("Error while initializing database, check if the database directory" +
                    "exists and is writable!");
            DatabaseManager.shutdownDatabase();
            System.exit(-1);
        }
    }
}

