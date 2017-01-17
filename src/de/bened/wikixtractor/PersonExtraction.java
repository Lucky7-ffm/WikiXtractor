package de.bened.wikixtractor;

import de.bened.wikixtractor.TaskScheduler.TaskType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <h1>PersonExtraction</h1>
 * TODO BLABLABLA
 *
 * @author symdox
 * @since 17.01.2017
 */

class PersonExtraction extends Task {

    final static Logger LOGGER = LogManager.getLogger(PersonExtraction.class);

    private TaskType[] precondition = {TaskType.HTMLDumpImport, TaskType.CategoryLinkExtraction, TaskType.EntityBaseExtraction};
    private TaskType[] postcondition = {};


    @Override String getDescription(){
        return "XYZ";
    }

    @Override void run(String[] args){

    }

}
