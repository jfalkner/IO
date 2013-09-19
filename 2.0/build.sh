#!/bin/sh

# build the code
javac -sourcepath src -d dist src/org/proteomecommons/io/*.java src/org/proteomecommons/io/*/*.java

# doc the code
rm -fr dist/docs/api/*
cd src
javadoc -d ../dist/docs/api org/proteomecommons/io/*.java org/proteomecommons/io/*/*.java
cd ..

# copy the source-code
rm -fr dist/src
cp -r src dist

# make a JAR
cd dist
zip -9r ProteomeCommons.org-IO.jar org/*

# make the Archive file
zip -9r ../Archive.zip *
