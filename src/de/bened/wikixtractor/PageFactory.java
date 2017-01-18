package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <h1>PageFactory</h1>
 * Creates Page objects from an HTML file
 *
 * @author xuiqzy
 * @since 07.11.2016
 */
class PageFactory {

	/**
	 * logging object for this class
	 */
	private final static Logger LOGGER = LogManager.getLogger(PageFactory.class);
	/**
	 * symbol on which pages are separated and on which page metadata header starts
	 */
	private final static String PAGE_SPLIT_SYMBOL = "¤";

	private final static int maximumNumberOfPagesPerTransaction = 1000;

	/**
	 * @param pathToWikipediaFile path to file to be parsed
	 * @throws IOException if file cannot be read
	 */
	static void extractPages(Path pathToWikipediaFile) throws IOException {

		Charset charset = StandardCharsets.UTF_8;
		try (BufferedReader reader = Files.newBufferedReader(pathToWikipediaFile, charset)) {
			// is set to true while the current line should be appended to the StringBuilder building the
			// html page String
			boolean lineIsPartOfHtmlPage = false;
			boolean headerInfoParsed = false;
			boolean abortCurrentPageParsing = false;

			// builds html content
			StringBuilder stringBuilder = new StringBuilder();

			int pageID = 0;
			int namespaceID = 0;
			String title = "";

			int numberOfPagesParsed = 0;
			boolean errorInCurrentTransaction = false;

			String currentLine;

			DatabaseManager.startTransaction();

			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.startsWith(PAGE_SPLIT_SYMBOL)) {
					// \t is the tab character
					String[] splittedHeaderOfPage = currentLine.split("\t");
					if (splittedHeaderOfPage.length == 4 && splittedHeaderOfPage[0].equals(PAGE_SPLIT_SYMBOL)) {
						pageID = Integer.valueOf(splittedHeaderOfPage[1]);
						namespaceID = Integer.valueOf(splittedHeaderOfPage[2]);
						title = splittedHeaderOfPage[3];

						// validate that namespaceID is only 0 or 14 so we can assert that when working with the data in
						// the database later
						if (namespaceID != 0 && namespaceID != 14) {
							headerInfoParsed = false;
							continue;
						}

						// signals that header info like title, pageID, etc. was parsed correctly so that page with
						// categories is later only created when this header info is available, too
						headerInfoParsed = true;
						lineIsPartOfHtmlPage = true;
					} else if (splittedHeaderOfPage.length == 1 && splittedHeaderOfPage[0].equals(PAGE_SPLIT_SYMBOL)
							&& lineIsPartOfHtmlPage && headerInfoParsed && !abortCurrentPageParsing) {
						// this is the first line after </html> so don't append lines to the html string and stop it for
						// the next lines, too
						lineIsPartOfHtmlPage = false;

						String htmlContent = stringBuilder.toString();
						//Set<String> categories = LinkExtractor.extractCategoryTitlesFromHtmlString(stringBuilder.toString());

						Page potentialExistingPage = DatabaseManager.getPageByPageIDAndNamespaceID(pageID, namespaceID);
						// no page with same pageID and same namespaceID/label found in database
						if (potentialExistingPage == null) {
							try {
								DatabaseManager.createPageNode(namespaceID, pageID, title, htmlContent);
								LOGGER.info("Page \"" + title + "\" added");
							} catch (Exception e) {
								LOGGER.error("Creation of page node failed, current transaction with up to " +
										maximumNumberOfPagesPerTransaction + " new Pages will be rolled back");
								errorInCurrentTransaction = true;
							}
							numberOfPagesParsed++;

							// start a new transaction every maximumNumberOfPagesPerTransaction Pages so one transaction
							// doesn't get too big, heap size seems to limit here, too
							if ((numberOfPagesParsed % maximumNumberOfPagesPerTransaction) == 0) {
								// if creation of a node failed previously, this statement has no effect and the whole transaction will be
								// rolled back regardless
								DatabaseManager.markTransactionSuccessful();

								DatabaseManager.endTransaction();
								if (!errorInCurrentTransaction) {
									LOGGER.info("Pages " +
											(numberOfPagesParsed - maximumNumberOfPagesPerTransaction + 1) +
											" - " + numberOfPagesParsed + " added in a transaction");
								}
								errorInCurrentTransaction = false;
								DatabaseManager.startTransaction();
							}
						} else {
							LOGGER.error("Page: \"" + title + "\" already exists in database!");
						}

						// get ready for parsing next Page:

						// reset StringBuilder to contain nothing, faster than using new StringBuilder, when size of
						// resulting String isn't known
						// see: https://stackoverflow.com/questions/5192512/how-can-i-clear-or-empty-a-stringbuilder
						stringBuilder.setLength(0);

						headerInfoParsed = false;
						abortCurrentPageParsing = false;

					} else { // invalid header
						LOGGER.error("invalid header of page detected, ignoring this page");
						// don't set lineIsPartOfHtmlPage to true so it won't read this page into a String and doesn't
						// process it, set it to false to also abort any creation of pages which were interrupted by
						// this invalid header (in that case it would be true at this moment)
						lineIsPartOfHtmlPage = false;
						abortCurrentPageParsing = true;
						// we don't want to use the parsed header info because the whole Page seems to be broken somehow
						headerInfoParsed = false;
					}
					// everything needed is done now when PAGE_SPLIT_SYMBOL is detected, continues with the next line then
				} else if (lineIsPartOfHtmlPage) {
					stringBuilder.append(currentLine);
				}
			}
			// if creation of a node failed previously, this statement has no effect and the whole transaction will be
			// rolled back regardless
			DatabaseManager.markTransactionSuccessful();
			DatabaseManager.endTransaction();
		} catch (IOException e) {
			LOGGER.error("Could not access the specified input file to parse!" + e);
			throw e;
		}
	}
}