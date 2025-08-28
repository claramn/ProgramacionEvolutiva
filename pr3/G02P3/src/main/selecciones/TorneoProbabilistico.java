package main.selecciones;

import java.util.Random;

/**
 * Implementación de la selección por torneo probabilístico.
 * En cada torneo se seleccionan k individuos aleatorios y se elige:
 * - el mejor con probabilidad p = 0.75
 * - el peor con probabilidad (1 - p).
 */
public class TorneoProbabilistico extends Seleccion {

    private final Random rand;             // Generador de números aleatorios
    private final double p;      // Probabilidad de seleccionar el  mejor individuo
    private final int tamTorneo; // Tamaño del torneo

    /**
     * Constructor del torneo probabilístico.
     *
     * @param fitness      Array con los valores de fitness de la población.
     * @param tamPoblacion Tamaño de la población.
     * @param min          Si es `true`, el problema es de
    minimización; de lo contrario, maximización.
     * @param probabilidad Probabilidad de seleccionar al mejor individuo.
     * @param tamTorneo    Tamaño del torneo (número de individuos que
    compiten).
     */
    public TorneoProbabilistico(double[] fitness, int tamPoblacion,
                                boolean min, double probabilidad, int tamTorneo) {
        super(fitness, tamPoblacion, min);
        this.seleccion = new int[tamPoblacion];
        this.rand = new Random();
        this.p = probabilidad;
        this.tamTorneo = tamTorneo;
    }

    /**
     * Realiza la selección por torneo probabilístico con torneo de k
     individuos.
     * Se seleccionan k individuos aleatorios y se determina:
     * - el mejor (el que cumple el criterio de optimización) y
     * - el peor,
     * para luego elegir:
     * - el mejor con probabilidad p, o
     * - el peor con probabilidad (1 - p).
     *
     * @return Array con los índices de los individuos seleccionados.
     */
    @Override
    public int[] getSeleccion() {
        for (int i = 0; i < this.tamPoblacion; i++) {
            // Seleccionar k individuos aleatorios para el torneo
            int[] torneo = new int[this.tamTorneo];
            for (int j = 0; j < this.tamTorneo; j++) {
                torneo[j] = rand.nextInt(this.tamPoblacion);
            }
            // Determinar el mejor y el peor de esos k
            int best = torneo[0];
            int worst = torneo[0];
            for (int j = 1; j < this.tamTorneo; j++) {
                int candidate = torneo[j];
                if (min) { // Problema de minimización: menor fitness es mejor
                    if (fitness[candidate] < fitness[best]) {
                        best = candidate;
                    }
                    if (fitness[candidate] > fitness[worst]) {
                        worst = candidate;
                    }
                } else { // Problema de maximización: mayor fitness es mejor
                    if (fitness[candidate] > fitness[best]) {
                        best = candidate;
                    }
                    if (fitness[candidate] < fitness[worst]) {
                        worst = candidate;
                    }
                }
            }
            double x = rand.nextDouble();
            // Si x < p, se selecciona el mejor; en caso contrario, se selecciona el peor
            this.seleccion[i] = (x < this.p) ? best : worst;
        }
        return this.seleccion;
    }
}