#!/bin/bash
if [ ! -d bin ]; then
 mkdir bin
fi
cd src
echo building ...
javac -d ../bin -classpath .:./bin:../lib/log4j.jar:../lib/iaik.jar:../lib/itext-1.4.jar:../lib/bcprov-jdk16-146.jar org/opensignature/opensignpdf/ui/FirmaPdf.java > ../build.log 2>&1
echo copying resources ...
cp  Resources*.properties ../bin
cd ..

