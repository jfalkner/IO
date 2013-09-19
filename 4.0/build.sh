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

# doc the code
rm -fr dist/docs/api/*
cd src
javadoc -classpath $JAVA_LIBRARIES -d ../dist/docs/api org/proteomecommons/io/*.java org/proteomecommons/io/*/*.java
cd ..

# copy the source-code
rm -fr dist/src
cp -r src dist

# make a JAR
cd dist
jar -cfm ProteomeCommons.org-IO.jar src/META-INF/MANIFEST.MF org
jarsigner -keystore ../../../ProteomeCommons/keystore -storepass password ProteomeCommons.org-IO.jar jfalkner
rm -fr META-INF

# make the Archive file
zip -9r ../Archive.zip *
