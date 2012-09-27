@echo off
if not exist bin goto bin-notfound
echo "Starting signing: %1"
java -Djava.library.path=. -classpath bin;lib\log4j.jar;lib\iaik.jar;lib\itext-1.4.jar;lib\bcprov-jdk16-146.jar org.opensignature.opensignpdf.ui.FirmaPdf %1 %2
goto end
:bin-notfound
echo.
echo ERROR!
echo bin directory not found, you have to run the build script
pause
:end