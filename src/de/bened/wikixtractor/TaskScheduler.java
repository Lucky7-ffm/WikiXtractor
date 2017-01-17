package de.bened.wikixtractor;


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

    enum TaskType {HTMLDumpImport, CategoryLinkExtraction, ArticleLinkExtraction, EntityBaseExtraction, EntityLinks, PersonExtraction, CityExtraction, MonumentExtraction}
    //Class[] TaskClasses = {HTMLDumpImport.class, CategoryLinkExtraction.class, ArticleLinkExtraction.class, EntityBaseExtraction.class, PersonExtraction.class, CityExtraction.class, MonumentExtraction.class};

    private static Map<TaskType, Class> taskTypeTaskClassMap;
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

    private static Map<String, Class> taskNameTaskClassMap;
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

}

