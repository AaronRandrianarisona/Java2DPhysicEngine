@echo off
setlocal
set "JAVA_HOME=D:\tools\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0"
if not exist "target\classes" (
    echo Building project...
    mvn -q -DskipTests package
)
java -classpath "target\classes" com.physicengine.Main
