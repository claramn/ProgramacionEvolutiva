package main.selecciones;

import java.util.Arrays;
import java.util.Random;

/**
 * Implementación del algoritmo de selección por ruleta.
 * La selección es proporcional a la aptitud (fitness) de cada individuo.
 */
public class Ruleta extends Seleccion {

    private double[] probAcu;// Probabilidades acumuladas

    private double[] prob;
    /**
     * Constructor de la selección por ruleta.
     * Calcula las probabilidades acumuladas con base en el fitness de la población.
     *
     * @param fitness      Array con los valores de fitness de la población.
     * @param tamPoblacion Tamaño de la población.
     * @param min          Si es `true`, se minimiza en lugar de maximizar.
     * @param mejor        Valor de referencia para la corrección de fitness.
     */
    public Ruleta(double[] fitness, int tamPoblacion, boolean min, double mejor) {
        super(fitness, tamPoblacion, min);
        this.prob = new double[tamPoblacion];
        this.probAcu = new double[tamPoblacion];
        this.seleccion = new int[tamPoblacion];


        if (min) {
            this.corrigeMinimizar(mejor);
        } else {
            this.corrigeMaximizar(mejor);
        }

        double uTotal = Arrays.stream(this.fitness).sum();

        if (uTotal == 0) {
            Arrays.fill(this.prob, 1.0 / tamPoblacion);
        } else {

            for (int i = 0; i < tamPoblacion; i++) {
                this.prob[i] = this.fitness[i] / uTotal;
                this.probAcu[i] = (i == 0) ? this.prob[i] : this.prob[i] + this.probAcu[i - 1];
            }
        }
    }

    /**
     * Selecciona individuos usando el metodo de la ruleta.
     * Devuelve un array con los índices de los individuos seleccionados.
     *
     * @return Array con los índices de los individuos seleccionados.
     */
    @Override
    public int[] getSeleccion() {
        int seleccionados = 0;
        Random rand = new Random();
        while (seleccionados < tamPoblacion) {
            double x = rand.nextDouble();
            int elegido = Arrays.binarySearch(probAcu, x);

            if (elegido < 0) elegido = -elegido - 1; // Ajuste si binarySearch no encuentra un valor exacto

            seleccion[seleccionados++] = elegido;
        }
        return seleccion;
    }
}
