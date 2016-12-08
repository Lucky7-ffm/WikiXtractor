package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.cypher.internal.ExecutionEngine;
import org.neo4j.cypher.internal.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

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
    private final static Logger LOGGER = LogManager.getLogger(LinkExtractor.class);
    /**
     * symbol on which pages are separated and on which page metadata header starts
     */
    private final static String PAGE_SPLIT_SYMBOL = "¤";

    /**
     * @param path path to file to be parsed
     * @return extracted Page objects with title, page id, namespace id and names of the categories it belongs to
     * @throws IOException if file cannot be read
     */
    static HashSet<Page> extractPages(Path path) throws IOException {

        Charset charset = StandardCharsets.UTF_8;
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            // is set to true while the current line should be appended to the StringBuilder building the
            // html page String
            boolean lineIsPartOfHtmlPage = false;
            boolean headerInfoParsed = false;
            boolean abortCurrentPageParsing = false;

            // builds html content
            StringBuilder stringBuilder = new StringBuilder();

            HashSet<Page> pages = new HashSet<>();

            int pageID = 0;
            int namespaceID = 0;
            String title = "";


            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.startsWith(PAGE_SPLIT_SYMBOL)) {
                    // \t is the tab character
                    String[] splittedHeaderOfPage = currentLine.split("\t");
                    if (splittedHeaderOfPage.length == 4 && splittedHeaderOfPage[0].equals(PAGE_SPLIT_SYMBOL)) {
                        pageID = Integer.valueOf(splittedHeaderOfPage[1]);
                        namespaceID = Integer.valueOf(splittedHeaderOfPage[2]);
                        title = splittedHeaderOfPage[3];

                        // TODO validate namespaceID to be either 0 or 14

                        // signals that header info like title, pageID, etc. was parsed so that page with categories is
                        // later only created when this header info is available, too
                        headerInfoParsed = true;
                        lineIsPartOfHtmlPage = true;
                    } else if (splittedHeaderOfPage.length == 1 && splittedHeaderOfPage[0].equals(PAGE_SPLIT_SYMBOL)
                            && lineIsPartOfHtmlPage && headerInfoParsed && !abortCurrentPageParsing) {
                        // this is the first line after </html> so don't append lines to the html string and stop it for
                        // the next lines, too
                        lineIsPartOfHtmlPage = false;

                        String htmlContent = stringBuilder.toString();
                        //Set<String> categories = LinkExtractor.extractLinks(stringBuilder.toString());

                        Result result = DatabaseManager.getPageByPageIDAndNamespaceID(pageID, namespaceID);
                        // no page with same pageID and same namespaceID/label found in database
                        if(!result.hasNext()) {
                           new Page(pageID, namespaceID, title, htmlContent);
                            LOGGER.info("Page \"" + title + "\" added");
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

            return pages;
        } catch (IOException e) {
            LOGGER.error("Could not access specified file" + e);
            throw e;
        }
    }
}