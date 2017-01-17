package de.bened.wikixtractor;

import de.bened.wikixtractor.TaskScheduler.TaskType;

/**
 * <h1>Task (Abstract)</h1>
 * Abstract Class for all tasks
 *
 * @author symdox
 * @since 16.01.2017
 */

abstract class Task {

    static TaskType[] precondition = null;
    static TaskType[] postcondition = null;

    abstract String getDescription();

    abstract void run(String[] args);

}
