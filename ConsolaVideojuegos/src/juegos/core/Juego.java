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
 * 5. (Opcional) Sobrescribe puntajesIniciales() para dar un Top 3 inicial.
 *
 * El lanzador detecta automáticamente el juego, por lo que no es necesario
 * editar Lanzador.java para registrarlo.
 */
public abstract class Juego {
    private static final int TOP = 3;
    private final String nombre;
    private TablaPuntajes tabla;
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

    /** Mejor puntaje del juego (primer lugar del top). */
    public final int getMejorPuntaje() {
        return tabla().mejor();
    }

    /** Top 3 del juego ya formateado para mostrar en pantalla. */
    public final String getTopFormateado() {
        return tabla().formatear();
    }

    /**
     * Puntajes que el juego trae de fábrica. Los juegos pueden sobrescribirlo;
     * si no lo hacen, su top empieza vacío.
     */
    protected Puntaje[] puntajesIniciales() {
        return new Puntaje[0];
    }

    /** La tabla se crea al usarse por primera vez, ya con los puntajes iniciales. */
    private TablaPuntajes tabla() {
        if (tabla == null) {
            tabla = new TablaPuntajes(TOP);
            for (Puntaje p : puntajesIniciales()) {
                tabla.agregar(p);
            }
        }
        return tabla;
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
            System.out.println("--- TOP 3 ---");
            System.out.println(tabla().formatear());
            System.out.println();
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

    /** Punto común para ejecutar cualquier juego y registrar su puntaje en el top. */
    private void iniciarPartida(Scanner sc) {
        try {
            int puntaje = jugar(sc);
            System.out.println();
            System.out.println("Puntaje final: " + puntaje);
            registrarPuntaje(sc, puntaje);
        } catch (RuntimeException e) {
            // Evita que un error de un juego cierre toda la consola.
            System.out.println();
            System.out.println("El juego terminó por un error controlado: " + e.getMessage());
        }
        pausa(sc);
    }

    /**
     * Sistema de puntuación UNIVERSAL: si el puntaje entra al Top 3 pide el nombre
     * y actualiza la tabla. Funciona igual para cualquier juego.
     */
    private void registrarPuntaje(Scanner sc, int puntaje) {
        TablaPuntajes t = tabla();
        if (!t.entra(puntaje)) {
            System.out.println("No alcanzaste el Top 3. Necesitas más de " + t.minimoParaEntrar() + " puntos.");
            return;
        }

        boolean primerLugar = puntaje > t.mejor();
        System.out.println(primerLugar ? "¡NUEVO RÉCORD! Superaste el primer lugar." : "¡Entraste al Top 3!");
        System.out.print("Escribe tu nombre: ");
        String nombreJugador = leerTexto(sc);
        if (nombreJugador.length() > 12) {
            nombreJugador = nombreJugador.substring(0, 12);
        }

        int pos = t.agregar(new Puntaje(nombreJugador, puntaje));
        System.out.println();
        System.out.println("Quedaste en el puesto " + pos + ". Top 3 actualizado:");
        System.out.println(t.formatear());
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

    /** Lee una línea respetando mayúsculas (por ejemplo, para nombres). */
    public static String leerTexto(Scanner sc) {
        return sc.hasNextLine() ? sc.nextLine().trim() : "";
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
