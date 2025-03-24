@echo off
cd src
javac -d ../bin -cp "../lib/mysql-connector-j-8.4.0.jar" *.java
if %errorlevel% neq 0 (
    echo Compilation failed. Exiting.
    exit /b %errorlevel%
)
cd ..
jar cfm WGCinema.jar manifest.txt -C bin . -C lib mysql-connector-j-8.4.0.jar images
echo Build completed successfully.