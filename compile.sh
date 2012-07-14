#!/bin/sh -x
CLASSPATH=../craftbukkit-1.2.5-R4.0.jar javac *.java -Xlint:unchecked -Xlint:deprecation
rm -rf me 
mkdir -p me/exphc/Squirt
mv *.class me/exphc/Squirt
jar cf Squirt.jar me/ *.yml *.java
