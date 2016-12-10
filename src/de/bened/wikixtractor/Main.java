package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

/**
 * <h1>Main</h1>
 * Handles user input on command line, calls extraction of pages and calls writing to xml file
 *
 * @author xuiqzy
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

			switch (args[0]) {
				case "reset":
					File pathToDatabaseDirectory = new File(args[1]);
					try {
						DatabaseManager.deleteDatabase(pathToDatabaseDirectory);
						LOGGER.info("Successfully dropped database.");
						DatabaseManager.initialize(pathToDatabaseDirectory);
						LOGGER.info("Successfully created a new database.");
					} catch (Exception e) {
						LOGGER.error("No database found. Try another path.");
					}
					break;
				case "categorylinks":
					// TODO create categorylinks function
					break;
				case "articlelinks":
					// TODO create articlelinks function
					break;
				default:
					argumentFail();
					break;
			}
		} else if (args.length == 3) {

			if (args[0].equals("importhtml")) {
				File pathToDatabaseDirectory = new File(args[1]);
				Path pathToWikipediaFile = Paths.get(args[2]);

				HashSet<Page> pages = new HashSet<>();
				try {
					DatabaseManager.initialize(pathToDatabaseDirectory);
					PageFactory.extractPages(pathToWikipediaFile);
				} catch (IOException e) {
					// already handled in PageFactory.extractPages(), but abort program now and don't export anything
					System.exit(-1);
				}
			} else {
				argumentFail();
			}
		} else if (args.length == 4) {

			if (args[0].equals("pageinfo")) {
				// TODO create pageinfo function
			} else {
				argumentFail();
			}
		} else {
			argumentFail();
		}
	}

	static private void argumentFail() {
		LOGGER.error("Incorrect argument: Please check Readme to check valid arguments");
		System.exit(-1);
	}
}
