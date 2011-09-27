@echo off
REM Dieses Skript kompiliert den Centurio GGP.
REM
REM Aufruf: compile.bat
REM
REM Die Quelldateien liegen im src-Verzeichnis.
REM Die kompilierten Dateien liegen im bin-Verzeichnis.
REM Der lib-Ordner enthaelt die eclipse-Prolog Bibliotheken.
REM Der pl-Ordner enthaelt die GameLogic.pl (unsere Prolog-Anfragen) und die geparsten Spielbeschreibungen.
REM Der kif-Ordner enthaelt Spielbeschreibungen im KIF-Format fuer den Benchmark. Auch empfangene Spielbeschreibungen werden dort gespeichert.
REM Der plot-Ordner enthaelt Dateien fuer gnuplot, die vom Benchmark erstellt wurden.
javac -sourcepath ./src -d ./bin ./src/centurio/CenturioStarter.java ./src/benchmark/Benchmark.java
