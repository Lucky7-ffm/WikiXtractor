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

    final static Logger LOGGER = LogManager.getLogger(EntityBaseExtraction.class);

    private TaskType[] precondition = {TaskType.HTMLDumpImport, TaskType.CategoryLinkExtraction};
    private TaskType[] postcondition = {TaskType.EntityLinks};


    @Override String getDescription(){
        return "XYZ";
    }

    @Override void run(String[] args){

    }

}
