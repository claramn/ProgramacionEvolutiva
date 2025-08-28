package main.selecciones;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Implementación de la selección por truncamiento.
 * Selecciona un porcentaje de individuos como padres según su fitness.
 */
public class Truncamiento extends Seleccion {

    private final double trunc;

    /**
     * Constructor de la selección por truncamiento.
     *
     * @param fitness      Array con los valores de fitness de la población.
     * @param tamPoblacion Tamaño de la población.
     */
    public Truncamiento(double[] fitness, int tamPoblacion, boolean min, double trunc) {
        super(fitness, tamPoblacion, min);
        this.seleccion = new int[tamPoblacion];
        this.trunc = trunc;
    }

    /**
     * Realiza la selección por truncamiento.
     * Selecciona el porcentaje 'trunc' de los mejores individuos y los replica
     * para formar un array de selección del tamaño total de la población.
     *
     * @return Array con los índices de los individuos seleccionados.
     */
    @Override
    public int[] getSeleccion() {
        // Número de individuos que se seleccionan según el truncamiento
        int numTrunc = (int) Math.ceil(trunc * tamPoblacion);
        // Número de repeticiones
        int rep = tamPoblacion / numTrunc;

        Integer[] indices = new Integer[tamPoblacion];
        for (int i = 0; i < tamPoblacion; i++) {
            indices[i] = i;
        }
        // Para minimización: orden ascendente (los mejores, es decir, con menor fitness, primero)
        if (min) {
            Arrays.sort(indices, Comparator.comparingDouble(i -> fitness[i]));
        } else {
            // Para maximización, ordenar en orden descendente
            Arrays.sort(indices, (i, j) -> Double.compare(fitness[j], fitness[i]));
        }

        int[] seleccionadosIndices = new int[tamPoblacion];
        int pos = 0;
        // Replicamos los mejores numTrunc individuos rep veces cada uno
        for (int i = 0; i < numTrunc; i++) {
            for (int j = 0; j < rep && pos < tamPoblacion; j++) {
                seleccionadosIndices[pos++] = indices[i];
            }
        }
        // Si quedan posiciones sin llenar, las completamos con los mejores individuos
        int i = 0;
        while (pos < tamPoblacion) {
            seleccionadosIndices[pos++] = indices[i++];

        }
        return seleccionadosIndices;
    }
}
