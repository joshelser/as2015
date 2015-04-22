CREATE EXTERNAL TABLE reviews(rowid STRING, category STRING, score INT, userId STRING)
STORED BY 'org.apache.hadoop.hive.accumulo.AccumuloStorageHandler'
WITH SERDEPROPERTIES('accumulo.columns.mapping' = ':rowid,category:name,review:score,review:userId');

SELECT userId, AVG(score) from reviews WHERE category = 'books' and userId != 'unknown' group by userId;
