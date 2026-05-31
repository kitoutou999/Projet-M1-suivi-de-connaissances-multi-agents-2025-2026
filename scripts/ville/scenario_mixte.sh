#!/bin/bash
# Scénario mixte second ordre (scénario par défaut)
cd "$(dirname "$0")/../.."
mvn -q compile exec:java -Dexec.mainClass="ville.MainVille" 2>/dev/null
