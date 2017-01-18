package de.bened.wikixtractor;

import de.bened.wikixtractor.TaskScheduler.TaskType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <h1>EntityBaseExtraction</h1>
 * TODO BLABLABLA
 *
 * @author symdox
 * @since 16.01.2017
 */

class EntityBaseExtraction extends Task {

	private final static Logger LOGGER = LogManager.getLogger(EntityBaseExtraction.class);

	private TaskType[] preconditions = {TaskType.HTMLDumpImport, TaskType.CategoryLinkExtraction};
	private TaskType[] postconditions = {TaskType.EntityLinks};


    @Override String getDescription(){
        return "XYZ";
    }

    @Override void run(String[] args){

    }

}
