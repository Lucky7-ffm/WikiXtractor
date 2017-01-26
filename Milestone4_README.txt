#WikiXtractor

**!!NOT WORKING!!**

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

How to run the calculation:
There is no run commands in this application.

Where will be the final data be stored:
No final data will be created. So there is no data to be stored.

How worked on which part of the application:
All member of the programming team has working on all classes.

Class: ArticleLinkExtraction: symdox
Class: CategoryLinkExtraction: symdox
Class: CityExtraction: symdox
Class: DatabaseManager: xuiqzy, doudou, symdox
Class: EnityBaseExtraction: symdox
Class: EnityLinks: symdox
Class: HTMLDumpImport: symdox
Class: LinkExtractor: xuiqzy, symdox
Class: Main: xuiqzy, doudou, symdox
Class: MonumentExtraction: symdox
Class: Page: xuiqzy, doudou
Class: PageExport: doudou, symdox
Class: PageFactory: xuiqzy
Class: PersonExtraction: symdox
Class: Task: symdox
Class: TaskScheduler: xuiqzy

Which problems still remain:
Since Milestone 3 there is no progress. The application won't run.


**Authors:**
doudou
symdox
xuiqzy

More authorship information is available via the @author tags in the source code.