@echo off
REM Navigate to the source directory
cd src

REM Compile Java files and output to the bin directory, including the MySQL connector in the classpath
javac -d ../bin -cp "../lib/mysql-connector-j-8.4.0.jar" *.java
if %errorlevel% neq 0 (
    echo Compilation failed. Exiting.
    exit /b %errorlevel%
)

REM Return to the root directory
cd ..

REM Create a JAR file with the manifest, compiled classes, library, and images
jar cfm App.jar manifest.txt -C bin . -C lib mysql-connector-j-8.4.0.jar images

echo Build completed successfully.