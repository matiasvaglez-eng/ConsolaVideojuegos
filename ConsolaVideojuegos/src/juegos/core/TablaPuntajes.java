package juegos.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tabla de mejores puntajes (por defecto Top 3), ordenada de mayor a menor.
 * Cada juego tiene la suya; la maneja la clase base Juego.
 */
public class TablaPuntajes {
    private final int capacidad;
    private final List<Puntaje> lista = new ArrayList<>();

    public TablaPuntajes(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException("La capacidad debe ser al menos 1.");
        }
        this.capacidad = capacidad;
    }

    /** Un puntaje entra si es mayor que 0 y hay espacio libre o supera al último lugar. */
    public boolean entra(int puntos) {
        if (puntos <= 0) return false;
        return lista.size() < capacidad || puntos > lista.get(lista.size() - 1).getPuntos();
    }

    /**
     * Inserta el puntaje en su lugar y descarta al que queda fuera del top.
     * En caso de empate, el registro más antiguo conserva el mejor lugar.
     *
     * @return posición obtenida (1 = primer lugar) o -1 si no entró.
     */
    public int agregar(Puntaje nuevo) {
        if (!entra(nuevo.getPuntos())) return -1;
        int pos = 0;
        while (pos < lista.size() && lista.get(pos).getPuntos() >= nuevo.getPuntos()) {
            pos++;
        }
        lista.add(pos, nuevo);
        if (lista.size() > capacidad) {
            lista.remove(lista.size() - 1);
        }
        return pos + 1;
    }

    public int mejor() {
        return lista.isEmpty() ? 0 : lista.get(0).getPuntos();
    }

    /** Puntos que hay que superar para entrar al top (0 si aún hay espacio). */
    public int minimoParaEntrar() {
        return lista.size() < capacidad ? 0 : lista.get(lista.size() - 1).getPuntos();
    }

    public List<Puntaje> getPuntajes() {
        return Collections.unmodifiableList(lista);
    }

    public String formatear() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < capacidad; i++) {
            if (i < lista.size()) {
                Puntaje p = lista.get(i);
                sb.append(String.format(" %d. %-12s %6d", i + 1, p.getNombre(), p.getPuntos()));
            } else {
                sb.append(String.format(" %d. %-12s %6s", i + 1, "---", "---"));
            }
            if (i < capacidad - 1) sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}