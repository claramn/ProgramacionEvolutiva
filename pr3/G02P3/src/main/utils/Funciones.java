package main.utils;

import main.mapa.Casa;

public class Funciones {
    private static Casa casa;
    /**
     * SICOMIDA(a, b): Si hay comida delante, ejecuta 'a'; en otro caso, ejecuta 'b'.
     *
     * estado Estado actual de la hormiga.
     *  a Operación a ejecutar si hay comida delante.
     * b Operación a ejecutar en caso contrario.
     * return Nuevo estado tras la ejecución de la operación correspondiente.
     */
    public Funciones(Casa casa) {
        this.casa = casa;
    }
    public static Estado sicoMida(Estado estado, Operacion a, Operacion b) {
        if (hayComidaDelante(estado)) {
            return a.ejecutar(estado);
        } else {
            return b.ejecutar(estado);
        }
    }

    /**
     * PROG2(a, b): Ejecuta las operaciones a y b secuencialmente, retornando el estado de la última.
     *
     * @param estado Estado inicial.
     * @param a Primera operación a ejecutar.
     * @param b Segunda operación a ejecutar.
     * @return Estado final tras ejecutar ambas operaciones.
     */
    public static Estado prog2(Estado estado, Operacion a, Operacion b) {
        Estado estadoIntermedio = a.ejecutar(estado);
        return b.ejecutar(estadoIntermedio);
    }

    /**
     * PROG3(a, b, c): Ejecuta las operaciones a, b y c en secuencia, retornando el estado final.
     *
     * @param estado Estado inicial.
     * @param a Primera operación a ejecutar.
     * @param b Segunda operación a ejecutar.
     * @param c Tercera operación a ejecutar.
     * @return Estado final tras ejecutar las tres operaciones.
     */
    public static Estado prog3(Estado estado, Operacion a, Operacion b, Operacion c) {
        Estado estadoA = a.ejecutar(estado);
        Estado estadoB = b.ejecutar(estadoA);
        return c.ejecutar(estadoB);
    }

    /**
     * Método auxiliar que determina si hay comida delante de la hormiga.
     * Este método debería consultar el entorno (por ejemplo, el tablero con el rastro de comida).
     * Por ahora se implementa como stub.
     *
     * @param estado Estado actual de la hormiga.
     * @return true si hay comida delante; false en caso contrario.
     */
    public static boolean hayComidaDelante(Estado estado) {
        // Implementa la consulta al entorno. Por ejemplo, podrías tener un método en
        // la clase del tablero que indique si en la celda frente a la posición de la hormiga hay comida.
        // Aquí se retorna aleatoriamente para propósitos de ejemplo.
        Estado aux = Terminales.avanza(estado);
        return casa.getFood().contains(aux);
    }
}
