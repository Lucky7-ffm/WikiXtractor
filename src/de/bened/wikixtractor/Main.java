package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * <h1>Main</h1>
 * Handles user input on command line, calls extraction of pages and calls writing to xml file
 *
 * @author xuiqzy
 * @author symdox
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

			File databaseDirectory = new File(args[1]);

			switch (args[0]) {
				case "reset":
						DatabaseManager.deleteDatabase(databaseDirectory);
					initializeDatabase(databaseDirectory);
						DatabaseManager.shutdownDatabase();
						System.exit(-1);
					break;
				case "categorylinks":
					initializeDatabase(databaseDirectory);
					LinkExtractor.extractCategoryLinks();
					break;
				case "articlelinks":
					initializeDatabase(databaseDirectory);
					LinkExtractor.extractArticleLinks();
					break;
				default:
					argumentFail();
					break;
			}
		} else if (args.length == 3) {

			File databaseDirectory = new File(args[1]);

			if (args[0].equals("importhtml")) {
				Path pathToWikipediaFile = Paths.get(args[2]);

				initializeDatabase(databaseDirectory);
				try {
					PageFactory.extractPages(pathToWikipediaFile);
				} catch (IOException e) {
					// already handled in PageFactory.extractPages(), but abort program now
					System.exit(-1);
				}
			} else {
				argumentFail();
			}
		} else if (args.length == 4) {

			File databaseDirectory = new File(args[1]);

			if (args[0].equals("pageinfo")) {
				DatabaseManager.initialize(databaseDirectory);
				int namespaceID = -1;
				try {
					namespaceID = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					LOGGER.error("Third argument is not a number, but should be the number 0 or 14 " +
							"representing the namespace id", e);
					System.exit(-1);
				}
				if (namespaceID != 0 && namespaceID != 14) {
					LOGGER.error("Third argument should be the number 0 or 14 representing the namespace id");
					System.exit(-1);
				}
				String title = args[3];
				pageInfo(namespaceID, title);
			} else {
				argumentFail();
			}
		} else {
			argumentFail();
		}
		DatabaseManager.shutdownDatabase();
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

	private static void argumentFail() {
		LOGGER.error("Incorrect argument: Please check Readme to check valid arguments");
		System.exit(-1);
	}

	static private void pageInfo(int namespaceID, String title) {
		DatabaseManager.startTransaction();

		Page startPage = DatabaseManager.getPageByNamespaceIDAndTitle(namespaceID, title);

		if (startPage == null) {
			LOGGER.error("Page with namespaceID \"" + namespaceID + "\" and title \"" + title + "\" not found");
			System.exit(-1);
		}


		Set<Page> directCategories = startPage.getDirectCategories();

		System.out.println("direct categories:");
		for (Page currentCategory : directCategories) {
			System.out.println("\t" + currentCategory.getTitle());
		}


		Set<Page> directAndIndirectCategories = startPage.getDirectAndIndirectCategories();

		System.out.println("direct and indirect categories:");
		for (Page currentCategory : directAndIndirectCategories) {
			System.out.println("\t" + currentCategory.getTitle());
		}


		Set<Page> linkedToArticles = startPage.getLinkedToArticles();

		System.out.println("pages linked to:");
		for (Page currentArticle : linkedToArticles) {
			System.out.println("\t" + currentArticle.getTitle());
		}


		Set<Page> linkedFromArticles = startPage.getLinkedFromArticles();

		System.out.println("pages linking here:");
		for (Page currentArticle : linkedFromArticles) {
			System.out.println("\t" + currentArticle.getTitle());
		}


		DatabaseManager.markTransactionSuccessful();
		DatabaseManager.endTransaction();
	}
}
