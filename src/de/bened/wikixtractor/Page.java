package de.bened.wikixtractor;

import java.util.Set;

/**
 * Created by xuiqzy on 11/3/16.
 */
class Page {
    private final int pageID;
    private final int namespaceID;
    private final String title;
    private final Set<String> categories;

    public Page(int pageID, int namespaceID, String title, Set<String> categories) {
        this.pageID = pageID;
        this.namespaceID = namespaceID;
        this.title = title;
        this.categories = categories;
    }


    public int getPageID() {
        return pageID;
    }

    public int getNamespaceID() {
        return namespaceID;
    }

    public String getTitle() {
        return title;
    }

    public Set<String> getCategories() {
        return categories;
    }


}
