package juegos.tragaperras;

import juegos.core.Juego;

import java.util.Scanner;

/**
 * Tragaperras: juego de tragamonedas compatible con el start() universal.
 * No depende de clases externas como Jugador, Estadisticas o InputHelper.
 */
public class Tragaperras extends Juego {
    private static final int SALDO_INICIAL = 100;
    private static final int COSTO_GIRO = 10;
    private static final String[] SIMBOLOS = {"7", "+", "*", "-", "O", "@", "#"};

    public Tragaperras() {
        super("Tragaperras");
    }

    @Override
    public String getReglas() {
        return "Comienzas con $" + SALDO_INICIAL + ".\n"
             + "Cada giro cuesta $" + COSTO_GIRO + ".\n"
             + "Presiona Enter para girar. Escribe r para retirarte o q para salir.\n"
             + "Tres 7 = premio de $500.\n"
             + "Tres simbolos iguales (excepto 7) = premio de $100.\n"
             + "Dos simbolos iguales = premio de $15.\n"
             + "El puntaje final es el saldo que te quede.";
    }

    @Override
    protected int jugar(Scanner sc) {
        int saldo = SALDO_INICIAL;
        String mensaje = "¡Buena suerte!";

        while (saldo >= COSTO_GIRO) {
            limpiar();
            System.out.println("==============================");
            System.out.println("         TRAGAPERRAS           ");
            System.out.println("==============================");
            System.out.println("Saldo: $" + saldo);
            System.out.println(mensaje);
            System.out.print("Presiona Enter para girar, R para retirarte o Q para salir: ");

            String opcion = leer(sc);
            if (opcion.equals("q") || opcion.equals("r")) {
                break;
            }

            saldo -= COSTO_GIRO;
            System.out.println("\nGirando...");

            for (int i = 0; i < 8; i++) {
                String s1 = SIMBOLOS[azar.nextInt(SIMBOLOS.length)];
                String s2 = SIMBOLOS[azar.nextInt(SIMBOLOS.length)];
                String s3 = SIMBOLOS[azar.nextInt(SIMBOLOS.length)];
                System.out.print("\r   [ " + s1 + " | " + s2 + " | " + s3 + " ]   ");
                pausaAnimacion(120);
            }

            String r1 = SIMBOLOS[azar.nextInt(SIMBOLOS.length)];
            String r2 = SIMBOLOS[azar.nextInt(SIMBOLOS.length)];
            String r3 = SIMBOLOS[azar.nextInt(SIMBOLOS.length)];

            System.out.println();
            System.out.println();
            System.out.println("+----------------+");
            System.out.println("| [ " + r1 + " | " + r2 + " | " + r3 + " ] |");
            System.out.println("+----------------+");

            if (r1.equals(r2) && r2.equals(r3)) {
                int premio = r1.equals("7") ? 500 : 100;
                saldo += premio;
                mensaje = "¡TRES IGUALES! Ganaste $" + premio + ".";
            } else if (r1.equals(r2) || r1.equals(r3) || r2.equals(r3)) {
                saldo += 15;
                mensaje = "¡PAR! Ganaste $15.";
            } else {
                mensaje = "No hubo premio en este giro.";
            }

            pausa(sc);
        }

        limpiar();
        if (saldo < COSTO_GIRO) {
            System.out.println("Te quedaste sin saldo suficiente para otro giro.");
        } else {
            System.out.println("Te retiraste de la tragaperras.");
        }
        System.out.println("Saldo final: $" + saldo);
        return saldo;
    }

    private void pausaAnimacion(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
