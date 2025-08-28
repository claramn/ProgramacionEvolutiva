package main.selecciones;

import java.util.Arrays;
import java.util.Random;

/**
 * Implementación de la selección estocástica universal (SUS).
 * 1) Se calculan probabilidades acumuladas (probAcum).
 * 2) Se genera un único número aleatorio r en [0, 1/N).
 * 3) Se colocan N marcas: r, r+1/N, r+2/N, ..., r+(N-1)/N
 * 4) Se busca en la probAcum a qué individuo corresponde cada marca.
 */
public class EstocasticoUniversal extends Seleccion {

    private double[] probAcum;         // Distribución acumulada de probabilidades

    /**
     * Constructor de la selección estocástica universal.
     *
     * @param fitness      Array con los valores de fitness de la población.
     * @param tamPoblacion Tamaño de la población.
     * @param min          true si el problema es de minimización, false si es de maximización.
     * @param mejor        Valor de referencia (peor) para hacer la corrección de fitness si es necesario.
     */
    public EstocasticoUniversal(double[] fitness, int tamPoblacion, boolean min, double mejor) {
        super(fitness, tamPoblacion, min);
        this.seleccion = new int[tamPoblacion];

        this.probAcum = new double[tamPoblacion];
        if (min) {
            corrigeMinimizar(mejor);
        } else {
            corrigeMaximizar(mejor);
        }

        double total = 0.0;
        for (double f : this.fitness) {
            total += f;
        }

        // Si toda la población tiene el mismo fitness o total ~ 0, asignamos probabilidades uniformes
        if (Math.abs(total) < 1e-9) {
            for (int i = 0; i < this.tamPoblacion; i++) {
                probAcum[i] = (i + 1.0) / this.tamPoblacion;
            }
        } else {
            // Calculamos la distribución acumulada
            double acumulado = 0.0;
            for (int i = 0; i < this.tamPoblacion; i++) {
                double p_i = this.fitness[i] / total; // Probabilidad de individuo i
                acumulado += p_i;
                probAcum[i] = acumulado;
            }
            // Aseguramos que el último valor sea 1.0
            probAcum[tamPoblacion - 1] = 1.0;
        }
    }

    /**
     * Selecciona individuos usando Stochastic Universal Sampling.
     *
     * @return Array con los índices de los individuos seleccionados.
     */
    @Override
    public int[] getSeleccion() {
        Random rand = new Random();

        // Distancia entre marcas
        double dist = 1.0 / tamPoblacion;

        // Se genera un número aleatorio en [0, dist)
        double r = rand.nextDouble() * dist;

        // Para cada marca, se busca en la probAcum el individuo correspondiente
        for (int i = 0; i < tamPoblacion; i++) {
            double marca = r + i * dist;
            int index = Arrays.binarySearch(probAcum, marca);

            if (index < 0) {
                index = -index - 1;  // Ajuste de Arrays.binarySearch cuando no hay coincidencia exacta
            }
            // Aseguramos no pasarnos del final
            if (index >= tamPoblacion) {
                index = tamPoblacion - 1;
            }

            this.seleccion[i] = index;
        }

        return this.seleccion;
    }
}
