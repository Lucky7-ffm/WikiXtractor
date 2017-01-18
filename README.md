#WikiXtractor

**!!NOT FULLY WORKING!!**

Running:
-------------
**Command:** java -jar path/to/WikiXtractor.jar \<arguments>

**Arguments:**

Reset Database:  
<DB-Directory> reset

Works all tasks in the specified task file. CAUTION! Just HTMLDumpImport, ArticleLinkExtraction and CategoryLinkExtraction
are supported. All other Tasks are dummy tasks:  
<Database-Directory> executetasks <Task-File>

Get info about the page specified via title. It prints out the title, page id and namespace id as well
as direct categories, direct and indirect categories, articles it links to and articles that link to it:  
<Database-Directory> queryentity <Article-Title>


**Authors:**  
doudou  
symdox  
xuiqzy  

More authorship information is available via the @author tags in the source code.