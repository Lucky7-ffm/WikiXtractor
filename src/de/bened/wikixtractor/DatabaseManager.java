package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <h1>DatabaseManager</h1>
 * Controls the NEO4J database
 *
 * @author xuiqzy
 * @author doudou
 * @author symdox
 * @since 27.11.2016
 */
class DatabaseManager {

	private static File databaseDirectory = null;

	private static GraphDatabaseService database;
	private static final Logger LOGGER = LogManager.getLogger(DatabaseManager.class);

	private static Transaction transaction = null;

	enum PageRelationshipType implements org.neo4j.graphdb.RelationshipType {hasCategory, linksTo}

	static void initialize(File databaseDirectory) {

		DatabaseManager.databaseDirectory = databaseDirectory;

		boolean isNewlyCreatedDatabase = true;
		if (DatabaseManager.databaseDirectory.exists()) {
			isNewlyCreatedDatabase = false;
		}

		// create or reopen database
		DatabaseManager.database = new GraphDatabaseFactory().newEmbeddedDatabase(DatabaseManager.databaseDirectory);

		if (isNewlyCreatedDatabase) {
			LOGGER.info("Successfully created database.");
		} else {
			LOGGER.info("Successfully reopened database.");
		}

		try (Transaction transaction = database.beginTx()) {
			// index properties of labels for quicker queries and traversal after database is first created
			if (isNewlyCreatedDatabase) {
				database.schema().indexFor(Label.label("Article")).on("NamespaceID").create();
				database.schema().indexFor(Label.label("Article")).on("PageID").create();
				database.schema().indexFor(Label.label("Article")).on("Title").create();

				database.schema().indexFor(Label.label("Category")).on("NamespaceID").create();
				database.schema().indexFor(Label.label("Category")).on("PageID").create();
				database.schema().indexFor(Label.label("Category")).on("Title").create();
			}

			LOGGER.info("Successfully created all indices on database.");
			transaction.success();
		} catch (Exception e) {
			LOGGER.error("Error while creating indices, transaction will be rolled back!", e);
			throw e;
		}

		// always wait for indices to become online
		waitForIndices();
	}

	private static void waitForIndices() {
		try (Transaction transaction = database.beginTx()) {
			database.schema().awaitIndexesOnline(15, TimeUnit.SECONDS);

			LOGGER.info("Successfully waited for indices to become online.");
			transaction.success();
		} catch (Exception e) {
			LOGGER.error("Error while waiting for indices of database to come online," +
					"transaction will be rolled back!", e);
			throw e;
		}
	}

	static void deleteDatabase(File databaseDirectory) {
		if (databaseDirectory.exists()) {
			try {
				FileUtils.deleteRecursively(databaseDirectory);
				LOGGER.info("Successfully dropped database.");
			} catch (IOException e) {
				LOGGER.error("Database directory exists, but can't delete it!", e);
			}
		} else {
			LOGGER.error("Database directory doesn't exist!");
		}
	}

	static void startTransaction() {
		if (DatabaseManager.transaction == null) {
			DatabaseManager.transaction = DatabaseManager.database.beginTx();
		}
	}

	static void markTransactionSuccessful() {
		// if creation of a node failed previously, this statement has no effect and the whole transaction will be
		// rolled back regardless
		DatabaseManager.transaction.success();
	}

	static void endTransaction() {
		DatabaseManager.transaction.close();
		DatabaseManager.transaction = null;
	}

	static void shutdownDatabase() {
		if (DatabaseManager.database != null) {
			DatabaseManager.database.shutdown();
			LOGGER.info("Shut down the database.");
			// discard the reference to this database after shutdown cause no function can be invoked on it anymore
			DatabaseManager.database = null;
		} else {
			LOGGER.warn("Tried to shut down database, but there is no database opened.");
		}
	}

	static GraphDatabaseService getDatabase() {
		return DatabaseManager.database;
	}


	static Node createPageNode(int namespaceID, int pageID, String title, String htmlContent) {
		try (Transaction transaction = DatabaseManager.database.beginTx()) {
			Label label = null;

			if (namespaceID == 0) {
				label = Label.label("Article");
			} else if (namespaceID == 14) {
				label = Label.label("Category");
			}

			Node pageNode = DatabaseManager.database.createNode(label);
			pageNode.setProperty("Title", title);
			pageNode.setProperty("NamespaceID", namespaceID);
			pageNode.setProperty("PageID", pageID);
			pageNode.setProperty("HtmlContent", htmlContent);

			transaction.success();


			return pageNode;
		} catch (Exception e) {
			// if anything went wrong the transaction in this method will fail and either it or a transaction
			// containing it will be rolled back even if success() is called afterwards for this or the containing
			// transaction
			LOGGER.error("Error while creating page node in database, transaction will be rolled back!", e);
			throw e;
		}
	}

	static void createRelationship(Node startNode, Node endNode, org.neo4j.graphdb.RelationshipType relationshipType) {
		try (Transaction transaction = DatabaseManager.database.beginTx()) {
			startNode.createRelationshipTo(endNode, relationshipType);

			transaction.success();
		} catch (Exception e) {
			// if anything went wrong the transaction in this method will fail and either it or a transaction
			// containing it will be rolled back even if success() is called afterwards for this or the containing
			// transaction
			LOGGER.error("Error while creating Page in database, transaction will be rolled back!", e);
			throw e;
		}
	}

	static Object getPropertyFromNode(Node node, String propertyToGet) {
		try (Transaction transaction = DatabaseManager.database.beginTx()) {
			Object property = node.getProperty(propertyToGet);
			transaction.success();
			return property;
		} catch (Exception e) {
			// if anything went wrong the transaction in this method will fail and either it or a transaction
			// containing it will be rolled back even if success() is called afterwards for this or the containing
			// transaction
			LOGGER.error("Error while getting property from node in database, transaction will be rolled back!",
					e);
			throw e;
		}
	}

	static Page getPageByPageIDAndNamespaceID(int pageID, int namespaceID) {
		Node resultNode = null;

		try (Transaction transaction = DatabaseManager.database.beginTx()) {
			if (namespaceID == 0) {
				Label articleLabel = Label.label("Article");
				resultNode = DatabaseManager.database.findNode(articleLabel, "PageID", pageID);
			} else if (namespaceID == 14) {
				Label categoryLabel = Label.label("Category");
				resultNode = DatabaseManager.database.findNode(categoryLabel, "PageID", pageID);
			}
			transaction.success();
		} catch (Exception e) {
			// if anything went wrong the transaction in this method will fail and either it or a transaction
			// containing it will be rolled back even if success() is called afterwards for this or the containing
			// transaction
			LOGGER.error("Error while searching for Page by PageID and NamespaceID in database, transaction" +
					"will be rolled back!", e);
			throw e;
		}

		if (resultNode != null) {
			return new Page(resultNode);
		} else {
			return null;
		}
	}

	static ArrayList<Page> getAllArticlePages() {
		ArrayList<Page> allArticlePages;

		try (Transaction transaction = database.beginTx()) {

			ResourceIterator<Node> articleNodes = DatabaseManager.database.findNodes(Label.label("Article"));
			allArticlePages = DatabaseManager.createPagesFromResourceIterator(articleNodes);
			transaction.success();
		} catch (Exception e) {
			// if anything went wrong the transaction in this method will fail and either it or a transaction
			// containing it will be rolled back even if success() is called afterwards for this or the containing
			// transaction
			LOGGER.error("Error while getting all article pages, transaction will be rolled back!", e);
			throw e;
		}
		return allArticlePages;
	}

	static ArrayList<Page> getAllPages() {

		ArrayList<Page> allPages = new ArrayList<>();

		try (Transaction transaction = database.beginTx()) {

			Result allNodes = DatabaseManager.database.execute("MATCH (page) RETURN page;");

			while (allNodes.hasNext()) {
				Map<String, Object> currentRow = allNodes.next();
				Node currentNode = (Node) currentRow.get("page");

				try {
					allPages.add(new Page(currentNode));
				} catch (IllegalArgumentException e) {
					LOGGER.error("Creating Page with empty node failed, ResourceIterator shouldn't contain empty nodes!",
							e);
				}
			}
			transaction.success();
			return allPages;
		} catch (Exception e) {
			// if anything went wrong the transaction in this method will fail and either it or a transaction
			// containing it will be rolled back even if success() is called afterwards for this or the containing
			// transaction
			LOGGER.error("Error while getting all pages, transaction will be rolled back!", e);
			throw e;
		}
	}


	private static ArrayList<Page> createPagesFromResourceIterator(ResourceIterator<Node> resourceIterator) {
		ArrayList<Page> result = new ArrayList<>();
		while (resourceIterator.hasNext()) {
			Node currentNode = resourceIterator.next();
			try {
				result.add(new Page(currentNode));
			} catch (IllegalArgumentException e) {
				LOGGER.error("Creating Page with empty node failed, ResourceIterator shouldn't contain empty nodes!", e);
			}
		}
		return result;
	}

	static Page getPageByNamespaceIDAndTitle(int namespaceID, String title) {
		Node resultNode = null;
		try (Transaction transaction = database.beginTx()) {
			Label label = null;

			if (namespaceID == 0) {
				label = Label.label("Article");
			} else if (namespaceID == 14) {
				label = Label.label("Category");
			}
			Iterator<Node> i = database.findNodes(label, "Title", title);

			if (i.hasNext()) {
				resultNode = i.next();
			}
			transaction.success();
		}
		if (resultNode != null) {
			return new Page(resultNode);
		} else {
			return null;
		}
	}


	static Set<Page> getEndNodesOfRelationship(Page startPage, PageRelationshipType pageRelationshipType, Direction direction,
											   boolean recursive) {
		return getEndNodesOfRelationshipHelper(startPage, pageRelationshipType, direction, recursive, new HashSet<>());
	}

	private static Set<Page> getEndNodesOfRelationshipHelper(Page startPage, PageRelationshipType pageRelationshipType, Direction direction,
															 boolean recursive, Set<Page> endNodePages) {

		try (Transaction transaction = database.beginTx()) {

			for (Relationship relationship : startPage.getPageNode().getRelationships(pageRelationshipType, direction)) {
				try {
					Page endNodePage;
					if (direction == Direction.OUTGOING) {
						endNodePage = new Page(relationship.getEndNode());
					} else {
						endNodePage = new Page(relationship.getStartNode());
					}
					if (!endNodePages.contains(endNodePage)) {
						endNodePages.add(endNodePage);
						if (recursive) {
							endNodePages.addAll(getEndNodesOfRelationshipHelper(endNodePage, pageRelationshipType, direction,
									true, endNodePages));
						}
					}
				} catch (IllegalArgumentException e) {
					LOGGER.error("Creating Page with empty node failed, relationship shouldn't contain empty nodes!", e);
				}
			}
			transaction.success();
			return endNodePages;
		} catch (Exception e) {
			// if anything went wrong the transaction in this method will fail and either it or a transaction
			// containing it will be rolled back even if success() is called afterwards for this or the containing
			// transaction
			LOGGER.error("Error while getting end nodes of a relationship, transaction will be rolled back!",
					e);
			throw e;
		}
	}
}
