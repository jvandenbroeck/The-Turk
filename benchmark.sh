#!/bin/sh
# Dieses Skript startet den Centurio GGP Benchmark.
# Bitte setzen Sie weiter unten die JAVA_HOME Variable und geben damit Ihr Java Verzeichnis an.
#
# Aufruf: ./benchmark.sh
# Parameter-Einstellung ueber benchmark.xml
#
# Die Quelldateien liegen im src-Verzeichnis.
# Die kompilierten Dateien liegen im bin-Verzeichnis.
# Der lib-Ordner enthaelt die eclipse-Prolog Bibliotheken.
# Der pl-Ordner enthaelt die GameLogic.pl (unsere Prolog-Anfragen) und die geparsten Spielbeschreibungen.
# Der kif-Ordner enthaelt Spielbeschreibungen im KIF-Format fuer den Benchmark. Auch empfangene Spielbeschreibungen werden dort gespeichert.
# Der plot-Ordner enthaelt Dateien fuer gnuplot, die vom Benchmark erstellt wurden.
mkdir plot
export LD_LIBRARY_PATH="./lib/i386_linux"
export JAVA_HOME="./jdk1.6.0_16"
java -XX:+UseConcMarkSweepGC -Xmx1500m -classpath ./bin benchmark.Benchmark
