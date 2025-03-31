@echo off
setlocal enabledelayedexpansion

set SRC_DIR=src
set BIN_DIR=bin
set LIB_DIR=lib
set JAR_NAME=WGCinema.jar

echo Cleaning previous build...
mkdir "%BIN_DIR%" 2>nul
del /Q "%BIN_DIR%\*.class" 2>nul

echo Collecting JAR dependencies...
set CLASSPATH=
for %%J in ("%LIB_DIR%\*.jar") do set CLASSPATH=!CLASSPATH!;%%J

echo Compiling Java files...
set PACKAGE_FILES=
for /r "%SRC_DIR%" %%F in (*.java) do set PACKAGE_FILES=!PACKAGE_FILES! "%%F"

javac -cp "%CLASSPATH%" -d "%BIN_DIR%" %PACKAGE_FILES%
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%

echo Creating JAR file...
jar cfm "%JAR_NAME%" manifest.txt -C bin . images

echo Build completed successfully.
pause