package de.bened.wikixtractor;

import de.bened.wikixtractor.TaskScheduler.TaskType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <h1>MonumentExtraction</h1>
 * Searches in all monument related entities for important information and adds them to the node
 *
 * @author symdox
 * @since 17.01.2017
 */

class MonumentExtraction extends Task {

	private final static Logger LOGGER = LogManager.getLogger(MonumentExtraction.class);

	private TaskType[] preconditions = {TaskType.HTMLDumpImport, TaskType.CategoryLinkExtraction, TaskType.EntityBaseExtraction};
	private TaskType[] postconditions = {};


    @Override String getDescription(){
        return "Searches in all monument related entities for important information and adds them to the node";
    }

    @Override void run(String[] args){

    }

}
