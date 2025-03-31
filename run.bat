@echo off
setlocal enabledelayedexpansion

set "SRC_DIR=src"
set "BIN_DIR=bin"
set "LIB_DIR=lib"

echo Cleaning previous build...
mkdir "%BIN_DIR%" 2>nul
del /Q "%BIN_DIR%\*.class" 2>nul

echo Compiling Java files...
set "PACKAGE_FILES="
for /r "%SRC_DIR%" %%F in (*.java) do set "PACKAGE_FILES=!PACKAGE_FILES! "%%F""

javac -cp "%LIB_DIR%/*" -d "%BIN_DIR%" %PACKAGE_FILES%
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    exit /b %ERRORLEVEL%
)

echo Running application...
java -cp "%BIN_DIR%;%LIB_DIR%/*" app.App