package de.bened.wikixtractor;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;

/**
 * Created by xuiqzy on 12/1/16.
 */
class DatabaseManager {

    final static private File databaseDirectory = new File("data");
    static private GraphDatabaseService database;

    static void initialize() {
        boolean isNewlyCreatedDatabase = false;
        if (databaseDirectory.exists()) {
            isNewlyCreatedDatabase = true;
        }
        // create or reopen database
        DatabaseManager.database = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);

        // index properties of labels for quicker queries and traversal after database is first created
        if (isNewlyCreatedDatabase) {
            try (Transaction transaction = database.beginTx()) {
                database.schema().indexFor(Label.label("Page")).on("NamespaceID").create();
                database.schema().indexFor(Label.label("Page")).on("PageID").create();
                database.schema().indexFor(Label.label("Page")).on("Title").create();
                // TODO create indices for the labels and properties we need to query and traverse
                //database.schema().indexFor(Label.label("Persaon")).on("name").create();
            }
        }
    }

    static Node createPageNote(int namespaceID, int pageID, String title, String htmlContent) {
        try(Transaction tx = database.beginTx()){
            Node pageNode = database.createNode();
            pageNode.setProperty("Title", title);
            pageNode.setProperty(("NamespaceID", namespaceID));
            pageNode.setProperty(("PageID"), pageID);
            pageNode.setProperty(("htmlContent"), htmlContent);

            return pageNode;
            }
    }
}
