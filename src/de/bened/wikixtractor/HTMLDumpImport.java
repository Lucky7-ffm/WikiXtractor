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

    }

}

