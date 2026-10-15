package juegos.core;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Clase base universal para todos los juegos de la consola.
 *
 * Para crear un juego nuevo:
 * 1. Crea una clase dentro de un paquete debajo de "juegos".
 * 2. Extiende esta clase.
 * 3. Usa un constructor sin parámetros.
 * 4. Implementa getReglas() y jugar(Scanner).
 *
 * El lanzador detecta automáticamente el juego, por lo que no es necesario
 * editar Lanzador.java para registrarlo.
 */
public abstract class Juego {
    private final String nombre;
    private int mejorPuntaje;
    protected final Random azar = new Random();

    protected Juego(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El juego debe tener un nombre.");
        }
        this.nombre = nombre;
    }

    public final String getNombre() {
        return nombre;
    }

    public final int getMejorPuntaje() {
        return mejorPuntaje;
    }

    /** Reglas mostradas desde el menú universal del juego. */
    public abstract String getReglas();

    /**
     * Contiene únicamente la lógica de una partida.
     * Debe devolver el puntaje obtenido.
     */
    protected abstract int jugar(Scanner sc);

    /**
     * Punto de entrada UNIVERSAL de todos los juegos.
     * Cada juego hereda este método y no necesita crear su propio menú.
     */
    public final void start(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner no puede ser null.");
        }

        boolean volver = false;
        while (!volver) {
            limpiar();
            System.out.println("=== " + nombre.toUpperCase() + " ===");
            System.out.println("Mejor puntaje: " + mejorPuntaje);
            System.out.println("1. Jugar");
            System.out.println("2. Ver reglas");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");

            switch (leer(sc)) {
                case "1" -> iniciarPartida(sc);
                case "2" -> mostrarReglas(sc);
                case "0", "q" -> volver = true;
                default -> {
                    System.out.println("Opción no válida.");
                    pausa(sc);
                }
            }
        }
    }

    /** Punto común para ejecutar y guardar el récord de cualquier juego. */
    private void iniciarPartida(Scanner sc) {
        try {
            int puntaje = jugar(sc);
            System.out.println();
            System.out.println("Puntaje final: " + puntaje);
            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                System.out.println("¡Nuevo récord!");
            }
        } catch (RuntimeException e) {
            // Evita que un error de un juego cierre toda la consola.
            System.out.println();
            System.out.println("El juego terminó por un error controlado: " + e.getMessage());
        }
        pausa(sc);
    }

    private void mostrarReglas(Scanner sc) {
        limpiar();
        System.out.println("=== REGLAS DE " + nombre.toUpperCase() + " ===");
        System.out.println(getReglas());
        pausa(sc);
    }

    // ---------- Utilidades compartidas ----------

    public static String leer(Scanner sc) {
        return sc.hasNextLine() ? sc.nextLine().trim().toLowerCase() : "q";
    }

    public static void pausa(Scanner sc) {
        System.out.print("Presiona Enter para continuar...");
        leer(sc);
    }

    public static void limpiar() {
        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }

    public static char[][] tableroVacio(int alto, int ancho) {
        char[][] t = new char[alto][ancho];
        for (char[] fila : t) {
            Arrays.fill(fila, ' ');
        }
        return t;
    }

    public static void imprimir(char[][] t) {
        if (t == null || t.length == 0 || t[0].length == 0) {
            return;
        }
        String borde = "+" + "=".repeat(t[0].length) + "+";
        System.out.println(borde);
        for (char[] fila : t) {
            System.out.println("|" + new String(fila) + "|");
        }
        System.out.println(borde);
    }
}
