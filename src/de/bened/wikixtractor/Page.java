package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.Node;

import java.util.Set;

/**
 * <h1>Page</h1>
 * Creates Page objects
 *
 * @author xuiqzy
 * @since 07.11.2016
 */
class Page {

	private final Node pageNode;

	Page(Node nodeToPage) throws IllegalArgumentException {
		if (nodeToPage != null) {
			this.pageNode = nodeToPage;
		} else {
			final Logger LOGGER = LogManager.getLogger(Page.class);
			IllegalArgumentException emptyNodeException = new IllegalArgumentException("Creating Page with empty node" +
					"isn't allowed!");
			LOGGER.error("Creating Page with empty node isn't allowed!", emptyNodeException);
			throw emptyNodeException;
		}
	}

	void addCategory(Page category) {
		DatabaseManager.createRelationship(this.pageNode, category.pageNode,
				DatabaseManager.RelationshipTypes.hasCategory);
	}

	void addLinkToArticle(Page article) {
		DatabaseManager.createRelationship(this.pageNode, article.pageNode,
				DatabaseManager.RelationshipTypes.linksTo);
	}

	Set<Page> getDirectCategories() {
		return null;
	}

	Set<Page> getDirectAndIndirectCategories() {
		return null;
	}

	Set<Page> getLinkedToArticles() {
		return null;
	}

	Set<Page> getLinkedFromArticles() {
		return null;
	}

	/**
	 * @return id of the page
	 */
	int getPageID() {
		return (int) DatabaseManager.getPropertyFromNode(this.pageNode, "PageID");
	}

	/**
	 * @return namespace of the page: 0 for an article, 14 for a category page
	 */
	int getNamespaceID() {
		return (int) DatabaseManager.getPropertyFromNode(this.pageNode, "NamespaceID");
	}

	/**
	 * @return title of the page
	 */
	String getTitle() {
		return (String) DatabaseManager.getPropertyFromNode(this.pageNode, "Title");
	}

	/**
	 * @return html content of the page
	 */
	String getHtmlContent() {
		return (String) DatabaseManager.getPropertyFromNode(this.pageNode, "HtmlContent");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Page page = (Page) o;

		return pageNode.equals(page.pageNode);
	}

	@Override
	public int hashCode() {
		return pageNode.hashCode();
	}

}
