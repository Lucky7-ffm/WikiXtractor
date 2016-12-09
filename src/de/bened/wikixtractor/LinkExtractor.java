package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;


/**
 * <h1>LinkExtractor</h1>
 * The LinkExtractor class that returns the title from a <a href> Link
 * after the div element catlinks in a HTML-Page.
 *
 * @author Symdox
 * @since 07.11.2016
 */
class LinkExtractor {
	/**
	 * logging object for this class
	 */
	private final static Logger LOGGER = LogManager.getLogger(LinkExtractor.class);

	/**
	 * @param pageHTML valid HTML string to extract category names from
	 * @return names of categories
	 */
	static Set<String> extractLinks(String pageHTML) {
		Set<String> linkSet = new HashSet<>();
		Document doc = Jsoup.parse(pageHTML);

		//Looks for the HTML DIV called catlinks and takes just that links with the beginning <a href...>
		Elements linkText = doc.select("div[id=catlinks] a[href]");

		//Adds every link text from catlinks into a Set of Strings
		for (Element link : linkText) {
			linkSet.add(link.text());
			LOGGER.info("Successfully added " + link.text());
		}

		return linkSet;
	}


}
