#!/bin/bash
# Meilleure partie connue : seed 7950951865960141691 -> score 25/25
cd "$(dirname "$0")/../.."
mvn -q compile exec:java -Dexec.mainClass="hanabi.MainHanabi" -Dexec.args="7950951865960141691" 2>/dev/null
