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
* STRL Price Spider Feed: 

```
#!java

runBuilder(Business.STRL, OutputFormat.XML, BusinessPurpose.PRICE_SPIDER);
```
* Dumping the data to a *local* CouchDB - this is currently used for EQ Specs:

```
#!java

runBuilder(Business.MIRA, OutputFormat.COUCHDB);
```
* To create tabbed spreadsheet creators. Simply update the program to reflect your need, then follow the directions in resources/how_to_add_jar.txt to create the jar. Jars should end up in: 
\\internetcomm\CPDB\Global\Applications\newschematabbedspreadsheetcreator\dist

```
#!java

runBuilder(Business.PORT, OutputFormat.XLSX);
```

### Contribution guidelines ###

Adding a new PUNI business to the builder is straightforward. You can view this [previous commit](https://bitbucket.org/kohler_source/db-exporter/commits/6cc71dfcab009b56fd39cbe2cb184e9f99ef44e9), but the steps are:

* Add the database connection information [connection.properties]
* Add the business to the Business enum [Business.java]
* Add the business to the list of standard businesses in STANDARD_PUNI_BUSINESSES [SqlService.java]

### Who do I talk to? ###

* Kohler Internet Operations Team
* @zachary-telschow 