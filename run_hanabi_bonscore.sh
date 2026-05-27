#!/bin/bash

javac -d out -sourcepath src src/**/*.java
java -cp out hanabi.MainHanabi 42

