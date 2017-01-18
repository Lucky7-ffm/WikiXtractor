package de.bened.wikixtractor;

import de.bened.wikixtractor.TaskScheduler.TaskType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <h1>EntityLink</h1>
 * Creates a relationship between related entities
 *
 * @author symdox
 * @since 17.01.2017
 */

class EntityLinks extends Task {

	private final static Logger LOGGER = LogManager.getLogger(EntityLinks.class);

	private TaskType[] preconditions = {TaskType.HTMLDumpImport, TaskType.CategoryLinkExtraction, TaskType.EntityBaseExtraction};
	private TaskType[] postconditions = {};


    @Override String getDescription(){
        return "Creates a relationship between related entities";
    }

    @Override void run(String[] args){

    }

}

