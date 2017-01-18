package de.bened.wikixtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Set;

/**
 * <h1>PageExport</h1>
 * Exports all page title and all category names into a xml file
 *
 * @author doudou
 * @author symdox
 * @since 07.11.2016
 */
class PageExport {

	/**
	 * logging object for this class
	 */
	private final static Logger LOGGER = LogManager.getLogger(PageExport.class);

	/**
	 * @param pages         pages to be written in the xml file
	 * @param fileToWriteTo the file where the output xml file should be written to
	 */
	static void exportPages(Set<Page> pages, File fileToWriteTo) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root element
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("pages");
			doc.appendChild(rootElement);

			// page elements
			for (Page currentPage : pages) {
				Element page = doc.createElement("page");
				rootElement.appendChild(page);

				// set attributes for page element
				Attr pageID = doc.createAttribute("pageID");
				pageID.setValue(String.valueOf(currentPage.getPageID()));
				page.setAttributeNode(pageID);

				Attr namespaceID = doc.createAttribute("namespaceID");
				namespaceID.setValue(String.valueOf(currentPage.getNamespaceID()));
				page.setAttributeNode(namespaceID);

				Attr title = doc.createAttribute("title");
				title.setValue(currentPage.getTitle());
				page.setAttributeNode(title);

				// categories element
				Element categories = doc.createElement("categories");
				page.appendChild(categories);

//                // category elements
//                for(String currentCategory : currentPage.getCategories()) {
//                    Element category = doc.createElement("category");
//                    categories.appendChild(category);
//
//                    // set attributes in category
//                    Attr name = doc.createAttribute("name");
//                    name.setValue(currentCategory);
//                    category.setAttributeNode(name);
//                }
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer serializer = transformerFactory.newTransformer();

			// Setup indenting for pretty output
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			// write the content into the xml file
			serializer.transform(new DOMSource(doc), new StreamResult(fileToWriteTo));
			LOGGER.info("File saved");
		} catch (ParserConfigurationException | TransformerException e) {
			LOGGER.error("Error while writing XML output file, check your specified output file", e);
		}
	}
}