@echo off
if "%JDK_HOME%" == "" goto jdk-unset
if exist bin goto bin-exists
mkdir bin
:bin-exists
cd src
echo building ...
%JDK_HOME%\bin\javac -d ..\bin -classpath .;..\bin;..\lib\log4j.jar;..\lib\iaik.jar;..\lib\itext-1.4.jar;..\lib\bcprov-jdk16-146.jar org/opensignature/opensignpdf/ui/FirmaPdf.java
@echo copying resources ...
copy /b Resources*.properties ..\bin
cd ..
goto end
:jdk-unset
echo You have to set JDK_HOME variable
pause
:end