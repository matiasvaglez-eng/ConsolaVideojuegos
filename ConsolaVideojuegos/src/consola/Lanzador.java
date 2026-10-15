package consola;

import juegos.core.Juego;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Punto de entrada de la consola.
 * Los juegos se descubren automáticamente dentro del paquete "juegos".
 */
public class Lanzador {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Juego> juegos = descubrirJuegos();

        if (juegos.isEmpty()) {
            System.out.println("No se encontraron juegos en el paquete 'juegos'.");
            return;
        }

        boolean salir = false;
        while (!salir) {
            Juego.limpiar();
            System.out.println("==============================");
            System.out.println("      CONSOLA DE VIDEOJUEGOS  ");
            System.out.println("==============================");

            for (int i = 0; i < juegos.size(); i++) {
                System.out.println((i + 1) + ". " + juegos.get(i).getNombre());
            }
            System.out.println("P. Mejores puntajes");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            String op = Juego.leer(sc);
            if (op.equals("0") || op.equals("q")) {
                salir = true;
            } else if (op.equals("p")) {
                mostrarPuntajes(juegos, sc);
            } else {
                try {
                    int n = Integer.parseInt(op);
                    if (n >= 1 && n <= juegos.size()) {
                        juegos.get(n - 1).start(sc);
                    } else {
                        System.out.println("Opción no válida.");
                        Juego.pausa(sc);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Opción no válida.");
                    Juego.pausa(sc);
                }
            }
        }

        System.out.println("¡Gracias por jugar!");
    }

    /**
     * Busca cualquier clase concreta que extienda Juego debajo de "juegos".
     * Funciona tanto desde clases compiladas como desde el JAR final.
     */
    private static List<Juego> descubrirJuegos() {
        Set<String> clases = new HashSet<>();
        try {
            Enumeration<URL> recursos = Thread.currentThread().getContextClassLoader()
                    .getResources("juegos");

            while (recursos.hasMoreElements()) {
                URL recurso = recursos.nextElement();
                if ("file".equals(recurso.getProtocol())) {
                    descubrirDesdeCarpeta(recurso, "juegos", clases);
                } else if ("jar".equals(recurso.getProtocol())) {
                    descubrirDesdeJar((JarURLConnection) recurso.openConnection(), clases);
                }
            }

            descubrirDesdeClasspath(clases);
        } catch (IOException e) {
            System.out.println("Aviso: no se pudieron detectar todos los juegos: " + e.getMessage());
        }

        List<Juego> juegos = new ArrayList<>();
        for (String nombreClase : clases) {
            try {
                Class<?> clase = Class.forName(nombreClase);
                if (!Juego.class.isAssignableFrom(clase)
                        || clase.isInterface()
                        || java.lang.reflect.Modifier.isAbstract(clase.getModifiers())) {
                    continue;
                }

                Juego juego = (Juego) clase.getDeclaredConstructor().newInstance();
                juegos.add(juego);
            } catch (ReflectiveOperationException | LinkageError e) {
                System.out.println("Se ignoró el juego '" + nombreClase + "': " + e.getMessage());
            }
        }

        juegos.sort(java.util.Comparator.comparing(Juego::getNombre, String.CASE_INSENSITIVE_ORDER));
        return juegos;
    }

    private static void descubrirDesdeCarpeta(URL recurso, String paquete, Set<String> clases) {
        try {
            File carpeta = new File(new URI(recurso.toString()));
            descubrirRecursivo(carpeta, paquete, clases);
        } catch (Exception e) {
            System.out.println("Aviso al explorar juegos: " + e.getMessage());
        }
    }

    private static void descubrirRecursivo(File carpeta, String paquete, Set<String> clases) {
        File[] archivos = carpeta.listFiles();
        if (archivos == null) return;

        for (File archivo : archivos) {
            if (archivo.isDirectory()) {
                descubrirRecursivo(archivo, paquete + "." + archivo.getName(), clases);
            } else if (archivo.getName().endsWith(".class")
                    && !archivo.getName().contains("$")) {
                String simple = archivo.getName().substring(0, archivo.getName().length() - 6);
                clases.add(paquete + "." + simple);
            }
        }
    }

    private static void descubrirDesdeJar(JarURLConnection conexion, Set<String> clases) throws IOException {
        try (JarFile jar = conexion.getJarFile()) {
            Enumeration<JarEntry> entradas = jar.entries();
            while (entradas.hasMoreElements()) {
                JarEntry entrada = entradas.nextElement();
                String nombre = entrada.getName();
                if (nombre.startsWith("juegos/")
                        && nombre.endsWith(".class")
                        && !nombre.contains("$")) {
                    clases.add(nombre.substring(0, nombre.length() - 6).replace('/', '.'));
                }
            }
        }
    }

    private static void descubrirDesdeClasspath(Set<String> clases) {
        String classpath = System.getProperty("java.class.path", "");
        for (String entrada : classpath.split(java.util.regex.Pattern.quote(File.pathSeparator))) {
            File ruta = new File(URLDecoder.decode(entrada, StandardCharsets.UTF_8));
            if (ruta.isDirectory()) {
                File juegos = new File(ruta, "juegos");
                if (juegos.isDirectory()) descubrirRecursivo(juegos, "juegos", clases);
            }
        }
    }

    private static void mostrarPuntajes(List<Juego> juegos, Scanner sc) {
        Juego.limpiar();
        System.out.println("=== MEJORES PUNTAJES (esta sesión) ===");
        for (Juego j : juegos) {
            System.out.println(j.getNombre() + ": " + j.getMejorPuntaje());
        }
        Juego.pausa(sc);
    }
}
