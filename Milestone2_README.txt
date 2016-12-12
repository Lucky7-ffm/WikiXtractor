#WikiXtractor

Running:
-------------
**Command:** java -jar path/to/WikiXtractor.jar \<arguments>

**Arguments:**

Import Pages from Wikipedia HTML file and parse title, namespace ID and page ID;
store html content for later processing:
importhtml <DB-Directory> \<Wikipedia-Input-File>

Create relationships from every page to the categories it belongs to, if they are in the database, too:
categorylinks <DB-Directory>

Create relationships from every article page to the article pages it links to, if they are in the database, too:
articlelinks <DB-Directory>

Get info about the page specified via namespace id and title. It prints out the title, page id and namespace id as well
as direct categories, direct and indirect categories, articles it links to and articles that link to it:
pageinfo <DB-Directory> \<namespaceID> \<page-title>


**Authors:**
doudou
symdox
xuiqzy

More authorship information is available via the @author tags in the source code.