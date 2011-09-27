@echo off
REM Dieses Skript startet den Centurio GGP Benchmark.
REM
REM Aufruf: benchmark.bat
REM Parameter-Einstellung ueber benchmark.xml
REM
REM Die Quelldateien liegen im src-Verzeichnis.
REM Die kompilierten Dateien liegen im bin-Verzeichnis.
REM Der lib-Ordner enthaelt die eclipse-Prolog Bibliotheken.
REM Der pl-Ordner enthaelt die GameLogic.pl (unsere Prolog-Anfragen) und die geparsten Spielbeschreibungen.
REM Der kif-Ordner enthaelt Spielbeschreibungen im KIF-Format fuer den Benchmark. Auch empfangene Spielbeschreibungen werden dort gespeichert.
REM Der plot-Ordner enthaelt Dateien fuer gnuplot, die vom Benchmark erstellt wurden.
java -XX:+UseConcMarkSweepGC -Xmx1500m -classpath ./bin benchmark.Benchmark
