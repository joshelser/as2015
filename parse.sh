#!/bin/sh

if [ "$#" -ne 1 ]; then
  echo 'Must pass in file'
  exit
fi

# File where each line is 'category amazon_review_file.txt.gz'
file=$1

while read line; do
  # Parse out category and file
  category=`echo $line | awk '{print $1}'`
  file=`echo $line | awk '{print $2}'`
  # gunzip the file
  echo "Unzipping $file to ${file}.txt"
  gunzip -c $file > ${file}.txt
  # Convert it to a CSV from from the format the stanford provided
  echo "Converting $file to ${file}.csv as $category"
  mvn exec:java -Dexec.mainClass=joshelser.as2015.Driver -Dexec.args="-c $category -f ${file}.txt -of ${file}.csv"
  echo "Removing ${file}.txt"
  # Remove the unzipped plain file
  rm ${file}.txt
done < $file
