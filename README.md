# Accumulo Summit 2015 Code

A collection of code I wrote to support my talk at Accumulo Summit 2015.

This project uses Amazon review data from https://snap.stanford.edu/data/web-Amazon.html into CSV files.

J. McAuley and J. Leskovec. Hidden factors and hidden topics: understanding rating dimensions with review text. RecSys, 2013.

If you reuse this code on the original dataset, be sure to read the limitations set forth on the data to be in compliance with
original owner's requests.

## Parser

Parse files into CSV: `mvn package exec:java -Dexec.mainClass=joshelser.as2015.parser.Driver -Dexec.args="-c jewelry -f Jewelry.txt -of
Jewelry.csv"`

The original files are gzipped. Be sure to unzip them before running.

## Ingest

Ingest said CSV file into Accumulo: `mvn package exec:java -Dexec.mainClass=joshelser.as2015.ingester.Ingester -Dexec.args="-f Jewelry.csv 
-u root -p password -i accumulo -z localhost -t reviews"`

Data is ingested into a table structure which can be leveraged by Hive and Pig.

## Query

Try to answer the question: For each user that reviewed at least one book, what was the average review score for each
user on each book they reviewed.

Compute the answer via the Accumulo API: `mvn package exec:java -Dexec.mainClass=joshelser.as2015.query.Query -Dexec.args="-t reviews -u root -p password
-i accumulo -z localhost"`

Alternatively, run `query.hql` in Hive or `query.pig` in Pig to compute the same answer.
