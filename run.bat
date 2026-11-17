@echo off
REM Compiles and runs Trajectory (needs JDK 17 or newer)
cd /d "%~dp0"
if exist out rmdir /s /q out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
    echo Compilation failed.
    del sources.txt
    pause
    exit /b 1
)
del sources.txt
java -cp out main.Main
