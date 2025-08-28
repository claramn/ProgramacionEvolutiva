package main.selecciones;

import java.util.Random;

/**
 * Implementación de la selección por torneo determinístico.
 * Selecciona el mejor de tres individuos aleatorios en cada iteración.
 */
public class TorneoDeterministico extends Seleccion {

    private Random rand;  // Generador de números aleatorios

    private double[] prob; // Probabilidades de selección
    /**
     * Constructor del torneo determinístico.
     *
     * @param fitness      Array con los valores de fitness de la población.
     * @param tamPoblacion Tamaño de la población.
     * @param min          Si es `true`, minimiza en lugar de maximizar.
     */
    public TorneoDeterministico(double[] fitness, int tamPoblacion, boolean min) {
        super(fitness, tamPoblacion, min);
        this.prob = new double[this.tamPoblacion];
        this.seleccion = new int[this.tamPoblacion];
        this.rand = new Random(); // Inicialización del generador de números aleatorios
    }

    /**
     * Realiza la selección por torneo determinístico.
     * Se eligen tres individuos aleatorios y se selecciona el mejor o el peor según el criterio.
     *
     * @return Array con los índices de los individuos seleccionados.
     */
    @Override
    public int[] getSeleccion() {
        for (int i = 0; i < this.tamPoblacion; i++) {
            // Selección de 3 individuos aleatorios
            int ind1 = rand.nextInt(this.tamPoblacion);
            int ind2 = rand.nextInt(this.tamPoblacion);
            int ind3 = rand.nextInt(this.tamPoblacion);

            // Seleccionar el mejor o peor individuo según `min`
            if (this.min) {
                // Minimización: Seleccionamos el individuo con menor fitness
                this.seleccion[i] = (fitness[ind1] <= fitness[ind2]) ?
                        ((fitness[ind1] <= fitness[ind3]) ? ind1 : ind3) :
                        ((fitness[ind2] <= fitness[ind3]) ? ind2 : ind3);
            } else {
                // Maximización: Seleccionamos el individuo con mayor fitness
                this.seleccion[i] = (fitness[ind1] >= fitness[ind2]) ?
                        ((fitness[ind1] >= fitness[ind3]) ? ind1 : ind3) :
                        ((fitness[ind2] >= fitness[ind3]) ? ind2 : ind3);
            }
        }
        return this.seleccion;
    }
}
