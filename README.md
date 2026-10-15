CONSOLA DE VIDEOJUEGOS
======================

Requisito: JDK de Java 11 o superior.

Estructura:
  src/consola/                    -> lanzador principal
  src/juegos/core/                -> clase base universal Juego, Puntaje y TablaPuntajes
  src/juegos/pong/               -> Pong
  src/juegos/spaceinvaders/      -> Space Invaders
  src/juegos/tragaperras/        -> Tragaperras

METODO START UNIVERSAL
----------------------
Todos los juegos heredan de juegos.core.Juego y usan automaticamente:
  juego.start(scanner)

Ese metodo controla el menu del juego, reglas, inicio de partida,
puntaje y regreso al menu principal.

IMPORTANTE: LOS JUEGOS NO DEBEN SOBRESCRIBIR start()
-----------------------------------------------------
El metodo start() es universal y ya esta implementado en Juego.
Cada juego solamente debe implementar getReglas() y jugar(Scanner).

SISTEMA DE PUNTUACION UNIVERSAL (TOP 3)
---------------------------------------
Cada juego tiene su propio Top 3, manejado por la clase base Juego.
Al terminar una partida, Juego revisa el puntaje devuelto por jugar():
  - Si entra al Top 3, pide el nombre del jugador y actualiza la tabla.
  - Si no entra, muestra cuantos puntos hacen falta.
  - Un puntaje de 0 nunca entra. En empate se queda arriba el mas antiguo.
El Top 3 se ve al entrar a cada juego y en la opcion P del menu principal.
Los puntajes duran solo mientras la consola esta abierta.

Los juegos NO necesitan escribir codigo de puntuacion: solo devolver el puntaje en jugar().
Opcional: para dar un Top 3 inicial, sobrescribe puntajesIniciales():

   @Override
   protected Puntaje[] puntajesIniciales() {
       return new Puntaje[]{ new Puntaje("ACE", 500), new Puntaje("MAX", 300) };
   }
(import juegos.core.Puntaje;). Si no lo sobrescribes, el Top 3 empieza vacio.

COMO AGREGAR UN JUEGO NUEVO SIN MODIFICAR Lanzador.java
--------------------------------------------------------
1. Crea una carpeta dentro de src/juegos/, por ejemplo:
     src/juegos/tetris/

2. Crea una clase publica que extienda Juego y tenga constructor sin parametros:

   package juegos.tetris;

   import juegos.core.Juego;
   import java.util.Scanner;

   public class Tetris extends Juego {
       public Tetris() {
           super("Tetris");
       }

       @Override
       public String getReglas() {
           return "Reglas del juego...";
       }

       @Override
       protected int jugar(Scanner sc) {
           // Logica del juego.
           return 0;
       }
   }

3. Ejecuta ejecutar.bat en Windows o ./ejecutar.sh en Linux/Mac.

El lanzador busca automaticamente cualquier clase concreta que extienda Juego
dentro del paquete "juegos". No hace falta agregar juegos.add(...) en Lanzador.java.

COMPARTIR UN JUEGO
------------------
Un juego nuevo debe:
  - estar dentro de un paquete debajo de "juegos";
  - extender juegos.core.Juego;
  - tener un constructor publico sin parametros;
  - implementar getReglas() y jugar(Scanner);
  - no depender de clases externas que no esten incluidas en el proyecto.

TRAGAPERRAS
-----------
El juego fue adaptado a la estructura universal de la consola.
Ya no depende de Jugador, Estadisticas ni InputHelper.

Controles:
  Enter = girar
  r = retirarse
  q = salir del juego

El juego inicia con $100 y cada giro cuesta $10.
El saldo final se usa como puntaje.

COMPILAR Y EJECUTAR
-------------------
Windows: doble clic en ejecutar.bat
Linux/Mac: ./ejecutar.sh

Tambien puedes ejecutar el JAR ya creado con:
  java -jar ConsolaVideojuegos.jar

CONTROLES
---------
Pong:
  w = subir, s = bajar, Enter = quieto, q = salir

Space Invaders:
  a = izquierda, d = derecha, f = disparar, q = salir

Tragaperras:
  Enter = girar, r = retirarse, q = salir
