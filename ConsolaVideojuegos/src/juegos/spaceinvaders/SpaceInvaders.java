package juegos.spaceinvaders;

import juegos.core.Juego;
import juegos.core.Puntaje;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/** Space Invaders: elimina las oleadas antes de que lleguen abajo. */
public class SpaceInvaders extends Juego {
    private static final int ANCHO = 21, ALTO = 13, FILAS = 3, COLS = 6, VIDAS = 3;
    private static final int[] VALOR_FILA = {30, 20, 10};
    private static final char[] ICONO = {'M', 'W', 'A'};

    private boolean[][] inv;
    private int invX, invY, dir, vivos;
    private int px, bx, by, puntos, vidas;
    private final List<int[]> balasEnemigas = new ArrayList<>();
    private String msg;

    public SpaceInvaders() {
        super("Space Invaders");
    }

    @Override
    protected Puntaje[] puntajesIniciales() {
        return new Puntaje[]{
                new Puntaje("ACE", 850),
                new Puntaje("MAX", 520),
                new Puntaje("ZOE", 300)
        };
    }

    @Override
    public String getReglas() {
        return "Eres la nave (^) en la parte inferior. Los invasores bajan poco a poco.\n"
                + "Cada turno escribes un comando y presionas Enter:\n"
                + "  a = izquierda     d = derecha     f = disparar\n"
                + "  Enter (vacío) = esperar     q = abandonar\n"
                + "  Puedes combinar hasta 3 letras, por ejemplo: aaf\n"
                + "Solo puedes tener un disparo en el aire a la vez.\n"
                + "Puntaje por invasor: M = 30, W = 20, A = 10. Limpiar una oleada da +50 x oleada.\n"
                + "Pierdes una vida si un disparo enemigo (!) te alcanza (tienes " + VIDAS + ").\n"
                + "Si los invasores llegan a tu fila, pierdes de inmediato.";
    }

    private void nuevaOleada(int oleada) {
        inv = new boolean[FILAS][COLS];
        for (boolean[] fila : inv) Arrays.fill(fila, true);
        vivos = FILAS * COLS;
        invX = 0;
        invY = Math.min(oleada - 1, 3);
        dir = 1;
        balasEnemigas.clear();
        by = -1;
    }

    private boolean hayInvasor(int y, int x) {
        int r = y - invY;
        int d = x - invX;
        if (r < 0 || r >= FILAS || d < 0 || d % 2 != 0) return false;
        int c = d / 2;
        return c < COLS && inv[r][c];
    }

    private void revisarImpacto() {
        if (by >= 0 && hayInvasor(by, bx)) {
            int r = by - invY;
            inv[r][(bx - invX) / 2] = false;
            vivos--;
            puntos += VALOR_FILA[r];
            msg = "¡Impacto! +" + VALOR_FILA[r];
            by = -1;
        }
    }

    @Override
    protected int jugar(Scanner sc) {
        puntos = 0;
        vidas = VIDAS;
        px = ANCHO / 2;
        int oleada = 1, tick = 0;
        nuevaOleada(oleada);
        msg = "¡Defiende la Tierra!";

        while (vidas > 0) {
            dibujar(oleada);
            System.out.print("Comando (a/d/f/Enter/q): ");
            msg = "";

            String cmd = leer(sc);
            if (cmd.equals("q")) {
                return puntos;
            }
            for (char c : cmd.substring(0, Math.min(3, cmd.length())).toCharArray()) {
                if (c == 'a') px = Math.max(0, px - 1);
                if (c == 'd') px = Math.min(ANCHO - 1, px + 1);
                if (c == 'f' && by < 0) {
                    bx = px;
                    by = ALTO - 2;
                }
            }

            for (int paso = 0; paso < 2 && by >= 0; paso++) {
                by--;
                revisarImpacto();
            }

            tick++;
            if (vivos <= 8 || tick % 2 == 0) {
                int borde = invX + 2 * (COLS - 1);
                if ((dir == 1 && borde >= ANCHO - 1) || (dir == -1 && invX <= 0)) {
                    invY++;
                    dir = -dir;
                } else {
                    invX += dir;
                }
                revisarImpacto();
            }

            for (int i = balasEnemigas.size() - 1; i >= 0; i--) {
                int[] b = balasEnemigas.get(i);
                b[1]++;
                if (b[1] == ALTO - 1 && b[0] == px) {
                    vidas--;
                    msg = "¡Te alcanzaron! Vidas: " + vidas;
                    balasEnemigas.remove(i);
                } else if (b[1] >= ALTO) {
                    balasEnemigas.remove(i);
                }
            }

            if (vivos > 0 && balasEnemigas.size() < 3 && azar.nextInt(100) < 35) {
                int c = azar.nextInt(COLS);
                for (int r = FILAS - 1; r >= 0; r--) {
                    if (inv[r][c]) {
                        balasEnemigas.add(new int[]{invX + 2 * c, invY + r + 1});
                        break;
                    }
                }
            }

            for (int r = FILAS - 1; r >= 0; r--) {
                boolean fila = false;
                for (int c = 0; c < COLS; c++) fila |= inv[r][c];
                if (fila) {
                    if (invY + r >= ALTO - 1) {
                        vidas = 0;
                        msg = "¡Los invasores llegaron a la Tierra!";
                    }
                    break;
                }
            }

            if (vivos == 0) {
                puntos += 50 * oleada;
                oleada++;
                nuevaOleada(oleada);
                msg = "¡Oleada superada! Bonus +" + (50 * (oleada - 1));
            }
        }

        dibujar(oleada);
        System.out.println("GAME OVER - llegaste a la oleada " + oleada);
        return puntos;
    }

    private void dibujar(int oleada) {
        limpiar();
        System.out.println("SPACE INVADERS   Puntos: " + puntos + "   Vidas: " + Math.max(vidas, 0)
                + "   Oleada: " + oleada);
        char[][] t = tableroVacio(ALTO, ANCHO);
        for (int r = 0; r < FILAS; r++) {
            for (int c = 0; c < COLS; c++) {
                int y = invY + r, x = invX + 2 * c;
                if (inv[r][c] && y >= 0 && y < ALTO) t[y][x] = ICONO[r];
            }
        }
        if (by >= 0 && by < ALTO) t[by][bx] = '|';
        for (int[] b : balasEnemigas) {
            if (b[1] >= 0 && b[1] < ALTO && b[0] >= 0 && b[0] < ANCHO) t[b[1]][b[0]] = '!';
        }
        t[ALTO - 1][px] = '^';
        imprimir(t);
        System.out.println(msg);
    }
}
