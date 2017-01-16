package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.cypher.internal.compiler.v2_3.commands.expressions.Null;
import org.neo4j.cypher.internal.compiler.v2_3.commands.predicates.True;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * <h1>HTMLDumpImport</h1>
 * Creats a new Database with the given HTML-Data
 *
 * @author symdox
 * @since 16.01.2017
 */

public class HTMLDumpImport extends Task {

    @Override public String getDescription(){
        return "Creates a new Database with the given HTML-Data";
    }

    @Override public void run(String[] args){
        if (previouslyTasks()) {
            File databaseDirectory = new File(args[0]);

            Path pathToWikipediaFile = Paths.get(args[1]);

            initializeDatabase(databaseDirectory);
        }
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

    @Override public boolean previouslyTasks(){
        return true;
    }

    @Override public String nextTask(){
        return "CategoryLinkExtraction";
    }
}
