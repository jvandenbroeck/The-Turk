#!/bin/sh
# Dieses Skript kompiliert den Centurio GGP.
# Bitte setzen Sie weiter unten die JAVA_HOME Variable und geben damit Ihr Java Verzeichnis an.
#
# Aufruf: ./compile.sh
#
# Die Quelldateien liegen im src-Verzeichnis.
# Die kompilierten Dateien liegen im bin-Verzeichnis.
# Der lib-Ordner enthaelt die eclipse-Prolog Bibliotheken.
# Der pl-Ordner enthaelt die GameLogic.pl (unsere Prolog-Anfragen) und die geparsten Spielbeschreibungen.
# Der kif-Ordner enthaelt Spielbeschreibungen im KIF-Format fuer den Benchmark. Auch empfangene Spielbeschreibungen werden dort gespeichert.
# Der plot-Ordner enthaelt Dateien fuer gnuplot, die vom Benchmark erstellt wurden.
export JAVA_HOME="./jdk1.6.0_16"
mkdir bin
javac -sourcepath ./src -d ./bin ./src/centurio/CenturioStarter.java ./src/benchmark/Benchmark.java
