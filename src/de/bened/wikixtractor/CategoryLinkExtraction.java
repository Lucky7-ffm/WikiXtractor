package de.bened.wikixtractor;

import de.bened.wikixtractor.TaskScheduler.TaskType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <h1>CategoryLinkExtraction</h1>
 * TODO BLABLABLA
 *
 * @author symdox
 * @since 17.01.2017
 */

class CategoryLinkExtraction extends Task {

    final static Logger LOGGER = LogManager.getLogger(CategoryLinkExtraction.class);

    private TaskType[] precondition = {TaskType.HTMLDumpImport};
    private TaskType[] postcondition = {};


    @Override String getDescription(){
        return "XYZ";
    }

    @Override void run(String[] args){

    }

}
