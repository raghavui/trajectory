@echo off
REM Compiles the project and runs the automatic test cases for Trajectory
cd /d "%~dp0"
if exist out rmdir /s /q out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
del sources.txt
java -cp out test.AppTestRunner
pause
