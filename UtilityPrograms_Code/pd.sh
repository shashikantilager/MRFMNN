#!/bin/bash
for f in $1*; do
numlines=$(wc -l < $f) 
sed  -i '1i\'$numlines'\' $f
sed -i '2i\'$2'\' $f
echo $numlines
echo $2
done

