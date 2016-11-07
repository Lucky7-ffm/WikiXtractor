package de.bened.wikixtractor;

import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.log4j.Logger;


/** Created by Symdox
 *
 */

class LinkExtractor {
    private final static Logger logger = Logger.getLogger(LinkExtractor.class);

    static Set<String> extractLinks(String pageHTML) {
        Set<String> linkSet = new HashSet<>();
        Document doc = Jsoup.parse(pageHTML);

        //Looks for the HTML DIV called catlinks and take just that links with the beginning <a href...>
        Elements linkText = doc.select("div[id=catlinks] a[href]");

        //Adds every Linktext from catlinks into a Set of Strings
        for (Element link : linkText) {
            linkSet.add(link.text());
            logger.info("Successfully added " + link.text());
        }

        return linkSet;
    }


}
