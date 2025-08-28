package main.selecciones;

import java.util.Arrays;

/**
 * Implementación de la selección por Restos (Stochastic Remainder).
 * Para ello, se corrigen los valores de fitness según sea minimización o maximización
 * usando las funciones corrigeMinimizar(...) y corrigeMaximizar(...).
 */
public class Restos extends Seleccion {

    /**
     * Constructor de la selección por Restos.
     *
     * @param fitness      Array con los valores de fitness de la población.
     * @param tamPoblacion Tamaño de la población.
     * @param min          true si es minimización, false si es maximizacion.
     */
    public Restos(double[] fitness, int tamPoblacion, boolean min, double mejor) {
        super(fitness, tamPoblacion, min);
        this.seleccion = new int[tamPoblacion];

        if (min) {
            corrigeMinimizar(mejor);
        } else {
            corrigeMaximizar(mejor);
        }
    }

    /**
     * Devuelve la selección de individuos usando el método de Restos
     * sobre el array fitnessCorr (que ya está asegurado a ser positivo).
     */
    @Override
    public int[] getSeleccion() {

        double totalFitness = 0.0;
        for (double f : fitness) {
            totalFitness += f;
        }

        if (Math.abs(totalFitness) < 1e-9) {

            for (int i = 0; i < tamPoblacion; i++) {
                seleccion[i] = i % tamPoblacion;
            }
            return seleccion;
        }
        // Calculamos probabilidades p[i]
        double[] p = new double[tamPoblacion];
        for (int i = 0; i < tamPoblacion; i++) {
            p[i] = fitness[i] / totalFitness;
        }

        //Determinar parte entera y parte fraccionaria
        int[] copiasEnteras = new int[tamPoblacion];
        double[] fracciones = new double[tamPoblacion];
        int totalCopiasEnteras = 0;

        for (int i = 0; i < tamPoblacion; i++) {
            double expected = p[i] * tamPoblacion;
            copiasEnteras[i] = (int) Math.floor(expected);
            fracciones[i] = expected - copiasEnteras[i];
            totalCopiasEnteras += copiasEnteras[i];
        }

        //Asignar copias enteras
        int pos = 0;
        for (int i = 0; i < tamPoblacion; i++) {
            for (int j = 0; j < copiasEnteras[i]; j++) {
                if (pos < tamPoblacion) {
                    seleccion[pos++] = i;
                }
            }
        }

        //Asignar copias restantes, hemos decidido usar la ruleta
        int restante = tamPoblacion - totalCopiasEnteras;
        if (restante > 0) {
            double sumFrac = Arrays.stream(fracciones).sum();
            for (int r = 0; r < restante && pos < tamPoblacion; r++) {
                double rnd = Math.random() * sumFrac;
                double acum = 0.0;
                for (int i = 0; i < tamPoblacion; i++) {
                    acum += fracciones[i];
                    if (acum >= rnd) {
                        seleccion[pos++] = i;
                        break;
                    }
                }
            }
        }

        //Si quedan huecos, rellenmaos con 0
        while (pos < tamPoblacion) {
            seleccion[pos++] = 0;
        }

        return seleccion;
    }
}
