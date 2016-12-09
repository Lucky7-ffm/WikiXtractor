package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
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

	static private Transaction transaction = null;

	static void initialize(File pathToDatabaseDirectory) {

		DatabaseManager.databaseDirectory = pathToDatabaseDirectory;

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


				database.schema().awaitIndexesOnline(1, TimeUnit.DAYS);

				transaction.success();
			}
		}
	}

	static void deleteDatabase(File path) {
		for (File file : path.listFiles()) {
			if (file.isDirectory())
				deleteDatabase(file);
			else if (!file.delete()) ;
		}
		if (!path.delete()) ;
	}

	static void startTransaction() {
		if (DatabaseManager.transaction == null) {
			System.out.print("blaaaa");
			DatabaseManager.transaction = DatabaseManager.database.beginTx();
		}
	}

	static void endTransaction() {
		DatabaseManager.transaction.close();
		DatabaseManager.transaction = null;
	}

	static GraphDatabaseService getDatabase() {
		return DatabaseManager.database;
	}


	static Node createPageNode(int namespaceID, int pageID, String title, String htmlContent) {
		try {
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
		} catch (NotInTransactionException e) {
			LOGGER.error("Tried to create a Node in the database without starting a transaction first", e);
			throw e;
		}
	}

	static Object getPropertyFromNode(Node node, String propertyToGet) {
		try {
			Object property = node.getProperty(propertyToGet);
			transaction.success();
			return property;
		} catch (NotInTransactionException e) {
			LOGGER.error("Tried to create a Node in the database without starting a transaction first", e);
			throw e;
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
		try (Transaction transaction = database.beginTx()) {
			Label label = null;
			Node node = null;

			if (namespaceID == 0) {
				label = Label.label("Article");
			} else if (namespaceID == 14) {
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
