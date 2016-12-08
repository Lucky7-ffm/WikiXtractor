package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by xuiqzy on 12/1/16.
 */
class DatabaseManager {

    final static private File databaseDirectory = new File("data");

    static private GraphDatabaseService database;
    final static private Logger LOGGER = LogManager.getLogger(DatabaseManager.class);

    static private Transaction transaction;

    static void initialize() {
        boolean isNewlyCreatedDatabase = true;
        if (databaseDirectory.exists()) {
            isNewlyCreatedDatabase = false;
        }
        // create or reopen database
        DatabaseManager.database = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);

        // index properties of labels for quicker queries and traversal after database is first created
        if (isNewlyCreatedDatabase) {
            try (Transaction transaction = database.beginTx()) {
                database.schema().indexFor(Label.label("Article")).on("NamespaceID").create();
                database.schema().indexFor(Label.label("Article")).on("PageID").create();
                database.schema().indexFor(Label.label("Article")).on("Title").create();

                database.schema().indexFor(Label.label("Category")).on("NamespaceID").create();
                database.schema().indexFor(Label.label("Category")).on("PageID").create();
                database.schema().indexFor(Label.label("Category")).on("Title").create();

                // TODO create indices for the labels and properties we need to query and traverse
                //database.schema().indexFor(Label.label("Person")).on("name").create();
                transaction.success();
            }
        }
    }

    static void startTransaction() {
        DatabaseManager.transaction = DatabaseManager.database.beginTx();
    }

    static void endTransaction() {
        DatabaseManager.transaction.close();
    }

    static GraphDatabaseService getDatabase() {
        return DatabaseManager.database;
    }


    static Node createPageNode(int namespaceID, int pageID, String title, String htmlContent) {
        // TODO use transaction from attribute and catch if no transaction is open
        try(Transaction transaction = DatabaseManager.database.beginTx()){
            Label label = null;

            if (namespaceID == 0) {
                label = Label.label("Article");
            } else if (namespaceID == 14) {
                label = Label.label("Category");
            }

            Node pageNode = database.createNode(label);
            pageNode.setProperty("Title", title);
            pageNode.setProperty("NamespaceID", namespaceID);
            pageNode.setProperty("PageID", pageID);
            pageNode.setProperty("htmlContent", htmlContent);

            transaction.success();

            return pageNode;
            }
    }

    static ArrayList<Node> createPageNodes(ArrayList pagesToCreate) {
        // TODO use transaction from attribute and catch if no transaction is open
        return new ArrayList<>();
    }

    static Object getPropertyFromNode(Node node, String propertyToGet) {
        // TODO use transaction from attribute and catch if no transaction is open
        try(Transaction transaction = database.beginTx()) {
            Object property = node.getProperty(propertyToGet);
            transaction.success();
            return property;
        }
    }

    static Result getPageByPageIDAndNamespaceID(int pageID, int namespaceID) {
        Result result = null;
        if (namespaceID == 0) {
            Label articleLabel = Label.label("Article");
            result = database.execute("MATCH (node:" + articleLabel + ") WHERE node.PageID =" + pageID + " RETURN node");
        } else if (namespaceID == 14) {
            Label categoryLabel = Label.label("Category");
            result = database.execute("MATCH (node:" + categoryLabel + ") WHERE node.PageID =" + pageID + " RETURN node");
        }
        return result;
    }
}
