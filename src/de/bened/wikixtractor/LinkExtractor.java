package de.bened.wikixtractor;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/** Created by Symdox
 *
 */

class LinkExtractor {
    private final static Logger logger = LogManager.getLogger(LinkExtractor.class);

    static Set<String> extractLinks(String pageHTML) {
        Set<String> linkSet = new HashSet<>();
        Document doc = Jsoup.parse(pageHTML);

        //Looks for the HTML DIV called catlinks and takes just that links with the beginning <a href...>
        Elements linkText = doc.select("div[id=catlinks] a[href]");

        //Adds every link text from catlinks into a Set of Strings
        for (Element link : linkText) {
            linkSet.add(link.text());
            logger.info("Successfully added " + link.text());
        }

        return linkSet;
    }


}
