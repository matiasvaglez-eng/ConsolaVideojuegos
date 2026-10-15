package juegos.core;

/** Un registro del top: nombre del jugador y puntos obtenidos. */
public final class Puntaje {
    private final String nombre;
    private final int puntos;

    public Puntaje(String nombre, int puntos) {
        this.nombre = (nombre == null || nombre.isBlank()) ? "Jugador" : nombre.trim();
        this.puntos = puntos;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }
}
