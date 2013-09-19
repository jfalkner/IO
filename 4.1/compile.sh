#!/bin/sh

# get all the libraries from lib
JAVA_LIBRARIES=".";
for LIB in $( ls dist/lib/*  )
do
  JAVA_LIBRARIES=$JAVA_LIBRARIES:$LIB
done
echo "JAVA_LIBRARIES=$JAVA_LIBRARIES"

# build the code
javac -classpath $JAVA_LIBRARIES -sourcepath src -d dist src/org/proteomecommons/io/*.java src/org/proteomecommons/io/*/*.java

# make a JAR
cd dist
jar -cfm ProteomeCommons.org-IO.jar src/META-INF/MANIFEST.MF org
jarsigner -keystore ../../../ProteomeCommons/keystore -storepass password ProteomeCommons.org-IO.jar jfalkner
rm -fr META-INF
