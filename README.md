# pdfextractor

## Services

The services are based on REST webservices which are understood as lightweight approach and allow the requesting systems to access and manipulate textual representations of Web resources using a uniform and predefined set of stateless operations, however it can be used for processing any type of informations as well. The services do the processing the PDFGenie html files and PDF2TABLE xml files. The services are defined as follows:

- processPdf2Table: the processPdf2Table 5.9 service point accepts a json String defining a sourcePath which is the path of files that were processed by Pdf2Table extraction tool. The process continues by walking through each file and extract the tables from the xml file and convert those tables in json format. Each PDF2Table xml file can contain multiple tables which the processor iterate through the xml tags using xml xpath queries and extract the tables.
After convertion into json document, then these documents will be inserted into database, the reason behind that is, the ability for multiple usages without to do the processing again.

- pdfGenieSolrImport: the pdfGenieSolrImport 5.10 service point retrieves the json documents which are already preprocessed with the processPdfGenie from database and then using the solr rest interface sends these json documents into solr search engine for indexing and evaluation process.

- pdf2TableSolrImport: the pdf2TableSolrImport 5.10 service point works almost the same as the pdfGenieSolrImport, the difference is just in getting the data from database.
