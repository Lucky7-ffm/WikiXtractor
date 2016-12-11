package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * <h1>LinkExtractor</h1>
 * The LinkExtractor class that returns the title from a <a href> Link
 * after the div element catlinks in a HTML-Page.
 *
 * @author Symdox
 * @author xuiqzy
 * @since 07.11.2016
 */
class LinkExtractor {
	/**
	 * logging object for this class
	 */
	private final static Logger LOGGER = LogManager.getLogger(LinkExtractor.class);

	static void extractCategoryLinks() {
		ArrayList<Page> allPages = DatabaseManager.getAllPages();
		LOGGER.info("Read all pages from database for extraction of categories of the pages.");
		for (Page currentPage : allPages) {
			String htmlContent = currentPage.getHtmlContent();
			Set<String> categoryTitlesOfCurrentPage = extractCategoryTitlesFromHtmlString(htmlContent);
			for (String currentCategoryTitle : categoryTitlesOfCurrentPage) {
				Page category = DatabaseManager.getPageByNamespaceIDAndTitle(14, currentCategoryTitle);
				if (category != null) {
					currentPage.addCategory(category);
					LOGGER.info("Category \"" + category.getTitle() + "\" to \"" + currentPage.getTitle() + "\" added.");
				} else {
					LOGGER.debug("Category \"" + currentCategoryTitle + "\" isn't in database, therefore it can't be " +
							"added as category of the Page \"" + currentPage.getTitle() + "\"");
				}
			}
			LOGGER.info("Relationship from Page \"" + currentPage.getTitle() + "\" to all categories that are in the" +
					"database added ");
		}
	}

	/**
	 * @param htmlPage valid HTML string to extract category names from
	 * @return names of categories
	 */
	private static Set<String> extractCategoryTitlesFromHtmlString(String htmlPage) {
		Set<String> categoryTitles = new HashSet<>();
		Document documentToParse = Jsoup.parse(htmlPage);

		// Looks for the div with catlinks as id and takes every a element with an href attribute under that.
		Elements allLinks = documentToParse.select("div[id=catlinks] a[href]");

		//Adds every link title from the category links into a Set of Strings
		for (Element link : allLinks) {
			categoryTitles.add(link.attr("title"));
		}

		return categoryTitles;
	}

	static void extractArticleLinks() {
		ArrayList<Page> allArticlePages = DatabaseManager.getAllArticlePages();
		LOGGER.info("Read all article pages from database for extraction of links to other articles.");
		for (Page currentPage : allArticlePages) {
			String htmlContent = currentPage.getHtmlContent();
			Set<String> articleLinkTitlesOfCurrentPage = extractArticleLinksFromHtmlString(htmlContent);
			for (String currentArticleLinkTitle : articleLinkTitlesOfCurrentPage) {
				Page articlePageLinksTo = DatabaseManager.getPageByNamespaceIDAndTitle(0, currentArticleLinkTitle);
				if (articlePageLinksTo != null) {
					currentPage.addLinkToArticle(articlePageLinksTo);
					LOGGER.info("Article link from \"" + currentPage.getTitle() + "\" to \" " +
							articlePageLinksTo.getTitle() + "\" added.");
				} else {
					LOGGER.debug("Page \"" + currentArticleLinkTitle + "\" isn't in database, therefore it can't " +
							"be added as a linked article of the Page \"" + currentPage.getTitle() + "\"");
				}
			}
			LOGGER.info("Relationship from Page \"" + currentPage.getTitle() + "\" to all pages it links to " +
					"that are in the database added ");
		}
	}

	private static Set<String> extractArticleLinksFromHtmlString(String htmlPage) {
		Set<String> articleTitles = new HashSet<>();
		Document documentToParse = Jsoup.parse(htmlPage);

		// Looks for every a element where the href attribute begins with /wiki/
		Elements allLinks = documentToParse.select("a[href^=/wiki/]");

		//Adds every link title from the article links into a Set of Strings
		for (Element link : allLinks) {
			articleTitles.add(link.attr("title"));
		}

		return articleTitles;
	}

}
