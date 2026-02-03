#!/bin/bash                                                                     

javac -d out -sourcepath src src/ville/*.java src/core/*.java
java -cp out ville.MainVille

