package de.bened.wikixtractor;

import de.bened.wikixtractor.TaskScheduler.TaskType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <h1>PersonExtraction</h1>
 * Searches in all person related entities for important information and adds them to the node
 *
 * @author symdox
 * @since 17.01.2017
 */

class PersonExtraction extends Task {

	private final static Logger LOGGER = LogManager.getLogger(PersonExtraction.class);

	private TaskType[] preconditions = {TaskType.HTMLDumpImport, TaskType.CategoryLinkExtraction, TaskType.EntityBaseExtraction};
	private TaskType[] postconditions = {};


    @Override String getDescription(){
        return "Searches in all person related entities for important information and adds them to the node";
    }

    @Override void run(String[] args){

    }

}
