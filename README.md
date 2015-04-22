# Accumulo Summit 2015 Code

A collection of code I wrote to support my talk.

Parses review data from https://snap.stanford.edu/data/web-Amazon.html into CSV files.

J. McAuley and J. Leskovec. Hidden factors and hidden topics: understanding rating dimensions with review text. RecSys, 2013.

## Parser

Parse files into CSV: `mvn package exec:java -Dexec.mainClass=joshelser.as2015.parser.Driver -Dexec.args="-c jewelry -f Jewelry.txt -of
Jewelry.csv"`

## Ingest

Ingest CSV file into Accumulo: `mvn package exec:java -Dexec.mainClass=joshelser.as2015.ingester.Ingester -Dexec.args="-f Jewelry.csv 
-u root -p password -i accumulo -z localhost -t reviews"`

## Query

Run a "complex" query: `mvn package exec:java -Dexec.mainClass=joshelser.as2015.query.Query -Dexec.args="-t reviews -u root -p password
-i accumulo -z localhost"`