SET port=7100

START /B java -jar MsPacManSimulator.jar randomSeed:%port% usePoints:false pacmanMaxLevel:16 pacManLevelTimeLimit:8000

TIMEOUT 3

java -classpath "%CLASSPATH%;controllers.osc.jar" B00697153PacMan -p %port% -g 1 -v 40

