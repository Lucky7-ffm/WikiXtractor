package de.bened.wikixtractor;

import java.io.File;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    private final static Logger logger = LogManager.getLogger(LinkExtractor.class);

    /**
     * @param pages pages to be written in the xml file
     * @param fileToWriteTo the file where the output xml file should be written to
     */
    static void exportPages(Set<Page> pages, File fileToWriteTo){

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("pages");
            doc.appendChild(rootElement);

            // page element
            for(Page currentPage : pages) {
                Element page = doc.createElement("page");
                rootElement.appendChild(page);

                // set attributes to page element
                Attr pageID = doc.createAttribute("pageID");
                pageID.setValue(String.valueOf(currentPage.getPageID()));
                page.setAttributeNode(pageID);

                Attr namespaceID = doc.createAttribute("namespaceID");
                namespaceID.setValue(String.valueOf(currentPage.getNamespaceID()));
                page.setAttributeNode(namespaceID);

                Attr title = doc.createAttribute("title");
                title.setValue(currentPage.getTitle());
                page.setAttributeNode(title);

                // shorten way
                // page.setAttribute("pageID", "value_pageID");

                // categories element
                Element categories = doc.createElement("categories");
                page.appendChild(categories);

                // category element
                for(String currentCategory : currentPage.getCategories()) {
                    Element category = doc.createElement("category");
                    categories.appendChild(category);

                    // set attributes to category
                    Attr name = doc.createAttribute("name");
                    name.setValue(currentCategory);
                    category.setAttributeNode(name);
                }



            }

            //write the content into xml file

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer serializer = transformerFactory.newTransformer();

            //Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            serializer.transform(new DOMSource(doc), new StreamResult(fileToWriteTo));

            //TransformerFactory transformerFactory = TransformerFactory.newInstance();
            //Transformer transformer = transformerFactory.newTransformer();
            //DOMSource source = new DOMSource(doc);
            //StreamResult result = new StreamResult(fileToWriteTo);

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            //transformer.transform(source, result);

            logger.info("File saved");

        } catch (ParserConfigurationException | TransformerException e) {
         logger.error("Error while writing XML output file", e);
        }
    }
}