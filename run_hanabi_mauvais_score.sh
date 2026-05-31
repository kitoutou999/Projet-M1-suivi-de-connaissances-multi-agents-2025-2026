#!/bin/bash

javac -d out -sourcepath src src/**/*.java
java -cp out hanabi.MainHanabi 41863777

