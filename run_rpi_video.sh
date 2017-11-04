#!/bin/bash
cp -n ExampleConfig/RaspberryVideo/config.json .
export DISPLAY=:0
java -jar piyoutube.jar
