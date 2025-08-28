package main.selecciones;

import java.util.Random;

/**
 * Implementación de la selección por torneo determinístico.
 * En cada torneo se seleccionan k individuos aleatorios y se elige al mejor.
 */
public class TorneoDeterministico extends Seleccion {

    private final Random rand;   // Generador de números aleatorios
    private final int tamTorneo; // Tamaño del torneo

    /**
     * Constructor del torneo determinístico.
     *
     * @param fitness      Array con los valores de fitness de la población.
     * @param tamPoblacion Tamaño de la población.
     * @param min          Si es `true`, el problema es de
    minimización; de lo contrario, maximización.
     * @param tamTorneo    Tamaño del torneo (número de individuos que
    compiten).
     */
    public TorneoDeterministico(double[] fitness, int tamPoblacion,
                                boolean min, int tamTorneo) {
        super(fitness, tamPoblacion, min);
        this.seleccion = new int[tamPoblacion];
        this.rand = new Random();
        this.tamTorneo = tamTorneo;
    }

    /**
     * Realiza la selección por torneo determinístico con torneo de k
     individuos.
     * Se seleccionan k individuos aleatorios y se elige al mejor.
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
            // Determinar el mejor de esos k
            int best = torneo[0];
            for (int j = 1; j < this.tamTorneo; j++) {
                int candidate = torneo[j];
                if (min) { // Problema de minimización: menor fitness es mejor
                    if (fitness[candidate] < fitness[best]) {
                        best = candidate;
                    }
                } else { // Problema de maximización: mayor fitness es mejor
                    if (fitness[candidate] > fitness[best]) {
                        best = candidate;
                    }
                }
            }
            this.seleccion[i] = best;
        }
        return this.seleccion;
    }
}
