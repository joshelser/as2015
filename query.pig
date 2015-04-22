register /usr/local/lib/accumulo/lib/htrace-core.jar;
register /usr/local/lib/accumulo/lib/accumulo-core.jar;
register /usr/local/lib/accumulo/lib/accumulo-fate.jar;
register /usr/local/lib/accumulo/lib/accumulo-start.jar;
register /usr/local/lib/accumulo/lib/accumulo-trace.jar;

books = LOAD 'accumulo://reviews?instance=accumulo&user=root&password=password&zookeepers=localhost' using
org.apache.pig.backend.hadoop.accumulo.AccumuloStorage('category,review:score,review:userId') as (rowkey:chararray,
    category:chararray, review_score:int, review_user:chararray);

filtered = FILTER books BY category == 'books' and review_user != 'unknown';

user_with_score = FOREACH filtered GENERATE review_user, review_score;

reviews_by_user = GROUP user_with_score BY $0;

avg_reviews_by_user = FOREACH reviews_by_user GENERATE $0, AVG($1.review_score);

dump avg_reviews_by_user;
