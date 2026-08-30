@echo off
setlocal
set "JAVA_HOME=D:\tools\jdk-25.0.2"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0"

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Java 25 was not found at %JAVA_HOME%.
    exit /b 1
)

if exist "target" (
    echo Cleaning stale build artifacts...
    rmdir /s /q target
)

echo Building project with Java 25...
mvn -q clean package

java -classpath "target\classes" com.physicengine.Main
