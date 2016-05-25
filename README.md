# README #

This application was a thought exercise in how we might replace the CPDB JSON Exporter, but the JSON currently produced doesn't match that program exactly. There are, however, numerous uses this program currently serves.

### How do I run this thing? ###

The file src/driver/LocalRunner.java is where the program is kicked off from. In the main method, you specify the business and the output format (and optionally the business purpose). 

### What are the current production uses? ###

* Google XML Shopping Feed: 

```
#!java
runBuilder(Business.KPNA, OutputFormat.XML, BusinessPurpose.GOOGLE_XML_SHOPPING);

```


* STRL Price Spider Feed: runBuilder(Business.STRL, OutputFormat.XML, BusinessPurpose.PRICE_SPIDER);
* Dumping the data to a *local* CouchDB - this is currently used for EQ Specs: runBuilder(Business.MIRA, OutputFormat.COUCHDB);
* To create tabbed spreadsheet creators. Simply update the program to reflect your need (runBuilder(Business.PORT, OutputFormat.XLSX);), then follow the directions in resources/how_to_add_jar.txt to create the jar. Jars should end up in: 
\\internetcomm\CPDB\Global\Applications\newschematabbedspreadsheetcreator\dist

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact