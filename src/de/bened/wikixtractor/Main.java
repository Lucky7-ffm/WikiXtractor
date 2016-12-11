package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

			// TODO validate arguments before calling functions
			File databaseDirectory = new File(args[1]);

			switch (args[0]) {
				case "reset":
					try {
						DatabaseManager.deleteDatabase(databaseDirectory);
						DatabaseManager.initialize(databaseDirectory);
					} catch (Exception e) {
						// error while deleting or initializing database (creating indices and waiting for them to come
						// online), already handled in DatabaseManager
						DatabaseManager.shutdownDatabase();
						System.exit(-1);
					}
					break;
				case "categorylinks":
					DatabaseManager.initialize(databaseDirectory);
					LinkExtractor.extractCategoryLinks();
					break;
				case "articlelinks":
					DatabaseManager.initialize(databaseDirectory);
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

				try {
					DatabaseManager.initialize(databaseDirectory);
					PageFactory.extractPages(pathToWikipediaFile);
				} catch (IOException e) {
					// already handled in PageFactory.extractPages(), but abort program now and don't export anything
					System.exit(-1);
				}
			} else {
				argumentFail();
			}
		} else if (args.length == 4) {

			File databaseDirectory = new File(args[1]);

			if (args[0].equals("pageinfo")) {
				// TODO create and use pageinfo function
			} else {
				argumentFail();
			}
		} else {
			argumentFail();
		}
		DatabaseManager.shutdownDatabase();
	}

	static private void argumentFail() {
		LOGGER.error("Incorrect argument: Please check Readme to check valid arguments");
		System.exit(-1);
	}
}
