package de.bened.wikixtractor;

import de.bened.wikixtractor.TaskScheduler.TaskType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <h1>EntityBaseExtraction</h1>
 * Search in category "Person", "Ort", "Denkmal" for all article and add them to a new entity base
 *
 * @author symdox
 * @since 16.01.2017
 */

class EntityBaseExtraction extends Task {

	private final static Logger LOGGER = LogManager.getLogger(EntityBaseExtraction.class);

	private TaskType[] preconditions = {TaskType.HTMLDumpImport, TaskType.CategoryLinkExtraction};
	private TaskType[] postconditions = {TaskType.EntityLinks};


    @Override String getDescription(){
        return "Search in category Person, Ort and Denkmal for all article and add them to a new entity base";
    }

    @Override void run(String[] args){
        DatabaseManager.startTransaction();


    }

}
