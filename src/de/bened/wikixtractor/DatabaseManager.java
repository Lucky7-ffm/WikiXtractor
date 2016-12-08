package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.register.Register;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * <h1>DatabaseManager</h1>
 * Controls the NEO4J database
 *
 * @author xuiqzy, symodx
 * @since 27.11.2016
 */
class DatabaseManager {

    static private File databaseDirectory = null;

    static private GraphDatabaseService database;
    final static private Logger LOGGER = LogManager.getLogger(DatabaseManager.class);

    static private Transaction transaction;

    static void initialize(File path) {

        setDatabaseDirectory(path);

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

                transaction.success();
            }

            isDatabaseOnline();
        }
    }

    static private void isDatabaseOnline() {
        try (Transaction transaction = database.beginTx()) {
            database.schema().awaitIndexesOnline(1, TimeUnit.DAYS);
            transaction.success();
        }

    }

    static void deleteDatabase(File path)
    {
        for (File file : path.listFiles())
        {
            if (file.isDirectory())
                deleteDatabase(file);
            else
                if (!file.delete());
        }
        if (!path.delete());
    }

    static private void setDatabaseDirectory(File path) { databaseDirectory = path; }

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

    static Node searchForNamespaceIDAndTitle(int namespaceID, String title) {
        try(Transaction transaction = database.beginTx()) {
            Label label = null;
            Node node = null;

            if (namespaceID == 0) {
                label = Label.label("Article");
            }
            else if (namespaceID == 14) {
                label = Label.label("Category");
            }
            Iterator<Node> i = database.findNodes(label, "Title", title);

            if (i.hasNext()) {
                node = i.next();
            }

            return node;
        }
    }
}
