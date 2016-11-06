#!/bin/bash

port=7100

#start simulator
java -jar MsPacManSimulator.jar randomSeed:$port usePoints:false pacmanMaxLevel:16 pacManLevelTimeLimit:8000 &

sleep 3

# run controller in 1 game with vizualization
java -classpath ./:./controllers.osc.jar B00697153PacMan -p $port -g 1 -v 5

#this command will be used to evaluate you controller
#java -classpath ./:./controllers.osc.jar B00697153PacMan -p $port -g 20
