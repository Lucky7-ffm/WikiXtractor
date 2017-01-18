package de.bened.wikixtractor;

import de.bened.wikixtractor.TaskScheduler.TaskType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <h1>ArticleLinkExtraction</h1>
 * TODO BLABLABLA
 *
 * @author symdox
 * @since 17.01.2017
 */

class ArticleLinkExtraction extends Task {

    private final static Logger LOGGER = LogManager.getLogger(ArticleLinkExtraction.class);

    private TaskType[] preconditions = {TaskType.HTMLDumpImport};
    private TaskType[] postconditions = {};


    @Override String getDescription(){
        return "XYZ";
    }

    @Override void run(String[] args){

    }

}