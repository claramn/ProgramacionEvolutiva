package main.selecciones;

public abstract class Seleccion {
    int[] seleccion;
    double[] fitness;
    int tamPoblacion;
    boolean min;

    public Seleccion(double[] fitness, int tamPoblacion, boolean min) {
        this.fitness = fitness;
        this.tamPoblacion = tamPoblacion;
        this.min = min;
    }
    public abstract int[] getSeleccion();

    /**
     * Corrige los valores de fitness para maximizar correctamente.
     * Se desplaza el fitness para evitar valores negativos.
     *
     * @param max Mayor valor posible de fitness en la población.
     */
    public void corrigeMinimizar(double max) {
        double shift = max * 1.05;
        for (int i = 0; i < this.tamPoblacion; i++) {
            this.fitness[i] = shift - this.fitness[i];
            if (this.fitness[i] < 0) this.fitness[i] = 1e-4;
        }
    }

    /**
     * Corrige los valores de fitness para maximizar correctamente.
     * Se desplaza el fitness para evitar valores negativos.
     *
     * @param min Menor valor posible de fitness en la población.
     */
    public void corrigeMaximizar(double min) {
        double ajuste = Math.abs(min) + 1;  // Asegurar que fitnessCorr sea positivo
        for (int i = 0; i < tamPoblacion; i++) {
            this.fitness[i] = this.fitness[i] + ajuste;
        }
    }
}
