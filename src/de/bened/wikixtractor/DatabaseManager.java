package de.bened.wikixtractor;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;

/**
 * Created by xuiqzy on 12/1/16.
 */
class DatabaseManager {

    final static private File databaseDirectory = new File("data");

    static void initialize() {
        boolean isNewlyCreatedDatabase = false;
        if (databaseDirectory.exists()) {
            isNewlyCreatedDatabase = true;
        }
        // create or reopen database
        GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);

        // index properties of labels for quicker queries and traversal after database is first created
        if (isNewlyCreatedDatabase) {
            try (Transaction transaction = database.beginTx()) {
                // TODO create indices for the labels and properties we need to query and traverse
                //database.schema().indexFor(Label.label("Persaon")).on("name").create();
            }
        }
    }
}
