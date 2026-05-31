#!/bin/bash
cd "$(dirname "$0")"
mvn -q compile exec:java -Dexec.mainClass="hanabi.MainHanabi" 2>/dev/null
