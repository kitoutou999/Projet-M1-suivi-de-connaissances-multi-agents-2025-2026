#!/bin/bash

javac -d out -sourcepath src src/**/*.java
java -cp out ville.MainVille

