package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

/**
 * <h1>Main</h1>
 * Handles user input on command line, calls extraction of information and info about a page.
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
	 * @param args first is mode of operation, second is the database directory, third and forth is dependant on the
	 *             mode of operation
	 */
	public static void main(String[] args) {

		if (args.length == 2) {
//
//			File databaseDirectory = new File(args[1]);
//
//			switch (args[0]) {
//				case "reset":
//						DatabaseManager.deleteDatabase(databaseDirectory);
//						initializeDatabase(databaseDirectory);
//						DatabaseManager.shutdownDatabase();
//					break;
//				case "categorylinks":
//					initializeDatabase(databaseDirectory);
//					LinkExtractor.extractCategoryLinks();
//					break;
//				case "articlelinks":
//					initializeDatabase(databaseDirectory);
//					LinkExtractor.extractArticleLinks();
//					break;
//				default:
//					incorrectArgumentsFound();
//					break;
//			}
		} else if (args.length == 3) {

			if (args[1].equals("executetasks")) {

				File databaseDirectory = new File(args[0]);
				Path tasksFile = Paths.get(args[2]);

				initializeDatabase(databaseDirectory);

				ArrayList<String> tasks = new ArrayList<>();

				try (BufferedReader reader = Files.newBufferedReader(tasksFile, StandardCharsets.UTF_8)) {
					String currentLine;
					while ((currentLine = reader.readLine()) != null) {
						tasks.add(currentLine);
					}
				} catch (IOException e) {
					LOGGER.error("Error while reading tasks file!", e);
				}
				LOGGER.info("Successfully read tasks file.");
				TaskScheduler.runTasksFromNames(tasks);

				DatabaseManager.shutdownDatabase();
			} else if (args[1].equals("queryentity")) {

				File databaseDirectory = new File(args[0]);
				String articleTitle = args[2];
				initializeDatabase(databaseDirectory);

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
				printInfoAboutPage(namespaceID, title);
			} else if (args[1].equals("reset")) {
				File databaseDirectory = new File(args[0]);

				DatabaseManager.deleteDatabase(databaseDirectory);
				initializeDatabase(databaseDirectory);
				DatabaseManager.shutdownDatabase();
			} else {
				incorrectArgumentsFound();
			}

//
//			File databaseDirectory = new File(args[0]);
//
//			if (args[0].equals("importhtml")) {
//				Path pathToWikipediaFile = Paths.get(args[2]);
//
//				initializeDatabase(databaseDirectory);
//				try {
//					PageFactory.extractPages(pathToWikipediaFile);
//				} catch (IOException e) {
//					// already handled in PageFactory.extractPages(), but abort program now
//					System.exit(-1);
//				}
//			} else {
//				incorrectArgumentsFound();
//			}
		} else if (args.length == 4) {
//
//			File databaseDirectory = new File(args[1]);
//
//			if (args[0].equals("pageinfo")) {
//				DatabaseManager.initialize(databaseDirectory);
//				int namespaceID = -1;
//				try {
//					namespaceID = Integer.parseInt(args[2]);
//				} catch (NumberFormatException e) {
//					LOGGER.error("Third argument is not a number, but should be the number 0 or 14 " +
//							"representing the namespace id", e);
//					System.exit(-1);
//				}
//				if (namespaceID != 0 && namespaceID != 14) {
//					LOGGER.error("Third argument should be the number 0 or 14 representing the namespace id");
//					System.exit(-1);
//				}
//				String title = args[3];
//				printInfoAboutPage(namespaceID, title);
//			} else {
//				incorrectArgumentsFound();
//			}
		} else {
			incorrectArgumentsFound();
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

	private static void incorrectArgumentsFound() {
		LOGGER.error("Incorrect arguments: Please check Readme to check valid arguments!");
		System.exit(-1);
	}

	/**
	 * Prints the following information about the specified page (if it exists):
	 * categories of the page
	 * categories of the page and categories of that category (recursively)
	 * articles this page links to
	 * articles which link to this page
	 */
	static private void printInfoAboutPage(int namespaceID, String title) {
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
