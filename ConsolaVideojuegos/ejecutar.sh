#!/bin/sh
cd "$(dirname "$0")" || exit 1
if ! command -v javac >/dev/null 2>&1; then
  echo "No se encontro el JDK de Java. Instalalo (por ejemplo: sudo apt install default-jdk) y vuelve a intentar."
  exit 1
fi
rm -rf out
mkdir -p out
find src -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -d out || exit 1
echo "Main-Class: consola.Lanzador" > manifest.txt
jar cfm ConsolaVideojuegos.jar manifest.txt -C out .
java -jar ConsolaVideojuegos.jar
