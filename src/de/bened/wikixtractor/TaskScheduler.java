package de.bened.wikixtractor;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>TaskScheduler</h1>
 * Control class of all Task
 *
 * @author symdox
 * @since 16.01.2017
 */

class TaskScheduler {


	private static final Logger LOGGER = LogManager.getLogger(TaskScheduler.class);

    enum TaskType {HTMLDumpImport, CategoryLinkExtraction, ArticleLinkExtraction, EntityBaseExtraction, EntityLinks, PersonExtraction, CityExtraction, MonumentExtraction}
    //Class[] TaskClasses = {HTMLDumpImport.class, CategoryLinkExtraction.class, ArticleLinkExtraction.class, EntityBaseExtraction.class, PersonExtraction.class, CityExtraction.class, MonumentExtraction.class};

	private static final Map<TaskType, Class> taskTypeTaskClassMap;

	static {
		taskTypeTaskClassMap = new HashMap<>();
		taskTypeTaskClassMap.put(TaskType.HTMLDumpImport, HTMLDumpImport.class);
		taskTypeTaskClassMap.put(TaskType.CategoryLinkExtraction, CategoryLinkExtraction.class);
        taskTypeTaskClassMap.put(TaskType.ArticleLinkExtraction, ArticleLinkExtraction.class);
        taskTypeTaskClassMap.put(TaskType.EntityBaseExtraction, EntityBaseExtraction.class);
        taskTypeTaskClassMap.put(TaskType.EntityLinks, EntityLinks.class);
        taskTypeTaskClassMap.put(TaskType.PersonExtraction, PersonExtraction.class);
        taskTypeTaskClassMap.put(TaskType.CityExtraction, CityExtraction.class);
        taskTypeTaskClassMap.put(TaskType.MonumentExtraction, MonumentExtraction.class);
    }

	private static final Map<String, Class> taskNameTaskClassMap;

	static {
		taskNameTaskClassMap = new HashMap<>();
		taskNameTaskClassMap.put("HTMLDumpImport", HTMLDumpImport.class);
		taskNameTaskClassMap.put("CategoryLinkExtraction", CategoryLinkExtraction.class);
        taskNameTaskClassMap.put("ArticleLinkExtraction", ArticleLinkExtraction.class);
        taskNameTaskClassMap.put("EntityBaseExtraction", EntityBaseExtraction.class);
        taskNameTaskClassMap.put("EntityLinks", EntityLinks.class);
        taskNameTaskClassMap.put("PersonExtraction", PersonExtraction.class);
        taskNameTaskClassMap.put("CityExtraction", CityExtraction.class);
        taskNameTaskClassMap.put("MonumentExtraction", MonumentExtraction.class);
    }

	private static final Map<String, TaskType> taskNameTaskTypeMap;

	static {
		taskNameTaskTypeMap = new HashMap<>();
		taskNameTaskTypeMap.put("HTMLDumpImport", TaskType.HTMLDumpImport);
		taskNameTaskTypeMap.put("CategoryLinkExtraction", TaskType.CategoryLinkExtraction);
		taskNameTaskTypeMap.put("ArticleLinkExtraction", TaskType.ArticleLinkExtraction);
		taskNameTaskTypeMap.put("EntityBaseExtraction", TaskType.EntityBaseExtraction);
		taskNameTaskTypeMap.put("EntityLinks", TaskType.EntityLinks);
		taskNameTaskTypeMap.put("PersonExtraction", TaskType.PersonExtraction);
		taskNameTaskTypeMap.put("CityExtraction", TaskType.CityExtraction);
		taskNameTaskTypeMap.put("MonumentExtraction", TaskType.MonumentExtraction);
	}


	private static ArrayList<TaskType> currentTasks = new ArrayList<>();
	private static ArrayList<String> currentTasksNames = new ArrayList<>();
	private static ArrayList<String> currentTasksArguments = new ArrayList<>();


	private static boolean validateTasks(ArrayList<String> tasks) {
		// validate that all the names correspond to existing tasks
		for (String task : tasks) {
			if (!taskNameTaskClassMap.containsKey(task)) {
				LOGGER.error("The task \"" + task + "\" could not be found!");
				return false;
			}
			TaskType currentTask = taskNameTaskTypeMap.get(task);
			TaskScheduler.currentTasks.add(currentTask);
		}

		// check if all pre- and postconditions are met
		for (TaskType currentTask : TaskScheduler.currentTasks) {

			// get preconditions of current task
			Class taskClass = taskTypeTaskClassMap.get(currentTask);
			ArrayList<TaskType> preconditionsOfCurrentTask = new ArrayList<>();
			try {
				preconditionsOfCurrentTask.add((TaskType) taskClass.getField("preconditions").get(TaskScheduler.class));
			} catch (NoSuchFieldException | IllegalAccessException e) {
				// shouldn't occur
				LOGGER.error(e);
				return false;
			}

			// check preconditions of current task
			for (TaskType currentPreTask : TaskScheduler.currentTasks) {
				// stop removing tasks from the potentially unfulfilled preconditions if we are at the current task
				// in the list, so we don't accept tasks running after it as fulfilling the preconditions
				if (currentPreTask == currentTask) {
					break;
				}
				preconditionsOfCurrentTask.remove(currentPreTask);
			}
			if (!preconditionsOfCurrentTask.isEmpty()) {
				// TODO print name of current task in error message
				LOGGER.error("Not all preconditions of task \"" + currentTask + "\" are met!");
				return false;
			}
			LOGGER.debug("All preconditions of task \"" + currentTask + "\" are met!");

			// get postconditions of current task
			ArrayList<TaskType> postconditionsOfCurrentTask = new ArrayList<>();
			try {
				preconditionsOfCurrentTask.add((TaskType) taskClass.getField("postconditions").get(TaskScheduler.class));
			} catch (NoSuchFieldException | IllegalAccessException e) {
				// shouldn't occur
				LOGGER.error(e);
				return false;
			}

			// check postconditions of current task
			// reverse list to go from the last task to the first
			ArrayList<TaskType> reversedCurrentTasks = new ArrayList<>(TaskScheduler.currentTasks);
			Collections.reverse(reversedCurrentTasks);
			for (TaskType currentPostTask : TaskScheduler.currentTasks) {
				// stop removing tasks from the potentially unfulfilled postconditions if we are at the current task
				// in the list, so we don't accept tasks running before it as fulfilling the preconditions
				if (currentPostTask == currentTask) {
					break;
				}
				postconditionsOfCurrentTask.remove(currentPostTask);
			}
			if (!postconditionsOfCurrentTask.isEmpty()) {
				// TODO print name of current task in error message
				LOGGER.error("Not all postconditions of task \"" + currentTask + "\" are met!");
				return false;
			}
			LOGGER.debug("All postconditions of task \"" + currentTask + "\" are met!");
		}
		return true;
	}

	static boolean runTasksFromNames(ArrayList<String> tasks) {

		// strip arguments from task list
		for (String task : tasks) {
			String[] taskWithArguments = task.split(" ", 2);
			currentTasksNames.add(taskWithArguments[0]);
			if (taskWithArguments.length > 1) {
				currentTasksArguments.add(taskWithArguments[1]);
			} else {
				currentTasksArguments.add("");
			}
		}

		// return false if validation failed
		if (!validateTasks(TaskScheduler.currentTasksNames)) {
			LOGGER.error("Not all pre- and postconditions of the tasks are met!");
			return false;
		}
		LOGGER.info("All pre- and postconditions of the tasks are met!");

		int iteration = 0;
		for (TaskType currentTask : TaskScheduler.currentTasks) {
			Class taskClass = taskTypeTaskClassMap.get(currentTask);
			Task taskToRun = null;
			try {
				taskToRun = (Task) taskClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// shouldn't occur
				LOGGER.error(e);
			}
			if (taskToRun != null) {
				String[] taskArguments = new String[0];
				if (TaskScheduler.currentTasksArguments.get(0).isEmpty()) {
					taskToRun.run(taskArguments);
				}
			}
			iteration++;
		}

		return true;
	}

}

