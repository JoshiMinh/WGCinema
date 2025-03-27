@echo off
cd src
javac -d ../bin -cp "../lib/mysql-connector-j-8.4.0.jar;../lib/jfreechart-1.0.19.jar;../lib/jcommon-1.0.23.jar" -Xlint:deprecation *.java || exit /b %errorlevel%
cd ..
jar cfm WGCinema.jar manifest.txt -C bin . -C lib mysql-connector-j-8.4.0.jar -C lib jfreechart-1.0.19.jar -C lib jcommon-1.0.23.jar images
echo Build completed.
pause