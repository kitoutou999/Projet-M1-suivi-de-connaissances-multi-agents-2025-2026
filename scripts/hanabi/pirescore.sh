#!/bin/bash
# Pire partie connue : seed -7009734669824303062 -> score 5/25
cd "$(dirname "$0")/../.."
mvn -q compile exec:java -Dexec.mainClass="hanabi.MainHanabi" -Dexec.args="-7009734669824303062" 2>/dev/null
