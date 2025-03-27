@echo off
javac -cp "lib/*" -d bin src/*.java
java -cp "lib/*;bin" App