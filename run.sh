#!/bin/sh
# Compiles and runs Trajectory (macOS / Linux)
cd "$(dirname "$0")"
rm -rf out
javac -encoding UTF-8 -d out $(find src -name "*.java") && java -cp out main.Main
