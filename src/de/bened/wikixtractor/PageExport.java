package de.bened.wikixtractor;

/**
 * Created by xuiqzy on 11/3/16.
 */


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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PageExport {

    public static void main(String argv[]) {



    }

    static void exportPages(Set<Page> pages){

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
                pageID.setValue(currentPage.getPageID());
                page.setAttributeNode(pageID);

                Attr namespaceID = doc.createAttribute("namespaceID");
                namespaceID.setValue(currentPage.getNamespaceID);
                page.setAttributeNode(namespaceID);

                Attr title = doc.createAttribute("title");
                title.setValue(currentPage.getTitle);
                page.setAttributeNode(title);

                // shorten way
                // page.setAttribute("pageID", "value_pageID");

                // categories element
                Element categories = doc.createElement("categories");
                page.appendChild(categories);

                // category element
                for(Category currentCategory : currentPage.categories) {
                    Element category = doc.createElement("category");
                    categories.appendChild(category);

                    // set attributes to category
                    Attr name = doc.createAttribute("name");
                    name.setValue(currentCategory.getCategory);
                    category.setAttributeNode(name);
                }



            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("file.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}