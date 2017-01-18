package de.bened.wikixtractor;

import de.bened.wikixtractor.TaskScheduler.TaskType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <h1>ArticleLinkExtraction</h1>
 * Extracts all article links from the HTML dump data
 *
 * @author symdox
 * @since 17.01.2017
 */

class ArticleLinkExtraction extends Task {

    private final static Logger LOGGER = LogManager.getLogger(ArticleLinkExtraction.class);

    private TaskType[] preconditions = {TaskType.HTMLDumpImport};
    private TaskType[] postconditions = {};


    @Override String getDescription() { return "Extracts all article links from the HTML dump data"; }

    @Override void run(String[] args){
        LinkExtractor.extractArticleLinks();
    }

}