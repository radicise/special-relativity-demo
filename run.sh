#!/bin/sh
set -eu
rm -f *.class
javac Simulator.java
java -ea Simulator
exit 0
