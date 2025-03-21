@echo off
REM Compile all Java files in the src directory and output to the bin directory
javac -cp "lib/mysql-connector-j-8.4.0.jar" -d bin src/*.java

REM Run the main application class (App) with the required classpath
java -cp "lib/mysql-connector-j-8.4.0.jar;bin" App