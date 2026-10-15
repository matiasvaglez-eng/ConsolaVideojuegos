@echo off
chcp 65001 >nul
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
  echo No se encontro el JDK de Java. Instalalo desde https://adoptium.net y vuelve a intentar.
  pause
  exit /b 1
)
if exist out rmdir /s /q out
mkdir out
setlocal EnableDelayedExpansion
set "SOURCES="
for /r src %%f in (*.java) do (
  set "SOURCES=!SOURCES! "%%f""
)
javac -encoding UTF-8 -d out %SOURCES%
if errorlevel 1 (
  echo Error al compilar.
  pause
  exit /b 1
)
echo Main-Class: consola.Lanzador> manifest.txt
jar cfm ConsolaVideojuegos.jar manifest.txt -C out .
java -jar ConsolaVideojuegos.jar
endlocal
pause
