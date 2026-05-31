#!/bin/bash
cd "$(dirname "$0")"
mvn -q compile exec:java -Dexec.mainClass="ville.MainVille" 2>/dev/null

