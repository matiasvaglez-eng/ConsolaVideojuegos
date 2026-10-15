package juegos.pong;

import juegos.core.Juego;
import java.util.Scanner;

/** Pong: tú (izquierda) contra la computadora (derecha). */
public class Pong extends Juego {
    private static final int ANCHO = 30, ALTO = 11, PALA = 3, META = 5;

    public Pong() {
        super("Pong");
    }

    @Override
    public String getReglas() {
        return "Controlas la pala izquierda (#). La computadora controla la derecha.\n"
             + "Cada turno escribes un comando y presionas Enter:\n"
             + "  w = subir la pala     s = bajar la pala\n"
             + "  Enter (vacío) = quedarte quieto     q = abandonar\n"
             + "  Puedes combinar hasta 2 letras, por ejemplo: ww\n"
             + "Puntaje: +1 por cada rebote tuyo, +10 por cada gol y +50 si ganas.\n"
             + "Gana quien llegue primero a " + META + " goles.";
    }

    @Override
    protected int jugar(Scanner sc) {
        int pj = (ALTO - PALA) / 2;
        int cj = pj;
        int golesJ = 0, golesC = 0, puntos = 0;
        int bx = ANCHO / 2, by = ALTO / 2;
        int dx = azar.nextBoolean() ? 1 : -1;
        int dy = azar.nextBoolean() ? 1 : -1;
        String msg = "¡Comienza el partido!";

        while (golesJ < META && golesC < META) {
            limpiar();
            System.out.println("PONG   Tú " + golesJ + " - " + golesC + " CPU   Puntos: " + puntos);
            char[][] t = tableroVacio(ALTO, ANCHO);
            for (int i = 0; i < PALA; i++) {
                t[pj + i][0] = '#';
                t[cj + i][ANCHO - 1] = '#';
            }
            t[by][bx] = 'O';
            imprimir(t);
            System.out.println(msg);
            System.out.print("Comando (w/s/Enter/q): ");
            msg = "";

            String cmd = leer(sc);
            if (cmd.equals("q")) {
                return puntos;
            }
            for (char c : cmd.substring(0, Math.min(2, cmd.length())).toCharArray()) {
                if (c == 'w') pj = Math.max(0, pj - 1);
                if (c == 's') pj = Math.min(ALTO - PALA, pj + 1);
            }

            if (azar.nextInt(100) < 70) {
                int centro = cj + PALA / 2;
                if (by < centro) cj--;
                else if (by > centro) cj++;
                cj = Math.max(0, Math.min(ALTO - PALA, cj));
            }

            int ny = by + dy;
            if (ny < 0) { ny = 1; dy = 1; }
            else if (ny >= ALTO) { ny = ALTO - 2; dy = -1; }
            int nx = bx + dx;

            boolean gol = false;
            boolean tuGol = false;
            if (nx <= 0) {
                if (ny >= pj && ny < pj + PALA) {
                    dx = 1;
                    nx = 1;
                    puntos += 1;
                    if (ny == pj) dy = -1;
                    else if (ny == pj + PALA - 1) dy = 1;
                    msg = "¡Buen rebote! +1";
                } else {
                    golesC++;
                    gol = true;
                    msg = "La CPU anota.";
                }
            } else if (nx >= ANCHO - 1) {
                if (ny >= cj && ny < cj + PALA) {
                    dx = -1;
                    nx = ANCHO - 2;
                    if (ny == cj) dy = -1;
                    else if (ny == cj + PALA - 1) dy = 1;
                } else {
                    golesJ++;
                    puntos += 10;
                    gol = true;
                    tuGol = true;
                    msg = "¡GOL! +10";
                }
            }

            if (gol) {
                bx = ANCHO / 2;
                by = azar.nextInt(ALTO);
                dx = tuGol ? -1 : 1;
                dy = azar.nextBoolean() ? 1 : -1;
            } else {
                bx = nx;
                by = ny;
            }
        }

        if (golesJ == META) {
            puntos += 50;
            System.out.println("¡Ganaste el partido! +50");
        } else {
            System.out.println("La CPU ganó el partido.");
        }
        System.out.println("Resultado: Tú " + golesJ + " - " + golesC + " CPU");
        return puntos;
    }
}
