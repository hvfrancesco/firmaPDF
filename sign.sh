#!/bin/bash
echo "Starting signing: $1"
java -Djava.library.path=./ -classpath ./:./bin:./lib/log4j.jar:./lib/iaik.jar:./lib/itext-1.4.jar:./lib/bcprov-jdk16-146.jar org.opensignature.opensignpdf.FirmaPdf "$1" "$2"