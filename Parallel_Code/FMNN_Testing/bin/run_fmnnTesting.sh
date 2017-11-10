#!/bin/bash

if [ $# -ne 6 ]; then
    echo Usage: [partition File][dimension][Testing dataFile][NumberofMap Tasks][NumberofReduce Teasks][OutFilePrefix]
    exit -1
fi


cp=$TWISTER_HOME/bin:.

for i in ${TWISTER_HOME}/lib/*.jar;
  do cp=$i:${cp}
done

for i in ${TWISTER_HOME}/apps/*.jar;
  do cp=$i:${cp}
done

java -Xmx1024m -Xms512m -XX:SurvivorRatio=10 -classpath $cp cgl.imr.samples.FMNN_Testing.FMNN_TestingMapReduce $1 $2 $3 $4 $5 $6
