package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <h1>Task (Abstract)</h1>
 * Abstract Class for all tasks
 *
 * @author symdox
 * @since 16.01.2017
 */

abstract class Task {

    final static Logger LOGGER = LogManager.getLogger(Main.class);

    public abstract String getDescription();

    public abstract void run(String[] args);

    public abstract boolean previouslyTasks();

    public abstract String nextTask();

}
