#!/bin/bash

if [ $# -ne 3 ]; then
    echo Usage: [partition File][num maps][InputDimension]
    exit -1
fi


cp=$TWISTER_HOME/bin:.

for i in ${TWISTER_HOME}/lib/*.jar;
  do cp=$i:${cp}
done

for i in ${TWISTER_HOME}/apps/*.jar;
  do cp=$i:${cp}
done

java -Xmx1024m -Xms512m -XX:SurvivorRatio=10 -classpath $cp cgl.imr.samples.FMNN.FMNNMapReduce $1 $2 $3
