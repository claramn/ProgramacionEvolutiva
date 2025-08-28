package main.selecciones;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Ranking extends Seleccion {
    static private final double _beta = 1.5;    //ajusta la presion selectiva, a mayor beta, mas se favorece a los mejores individuos
    private double[] probAcu;    // Probabilidades acumuladas
    private double[] prob;
    public Ranking(double[] fitness, int tamPoblacion, boolean min, double mejor) {
        super(fitness, tamPoblacion, min);
        this.prob = new double[tamPoblacion];
        probAcu = new double[tamPoblacion];
        prob = new double[tamPoblacion];
        this.seleccion = new int[tamPoblacion];
        this.corrigeMinimizar(mejor);
    }


    //calcula la probabilidad d seleccion para cada individuo con ranking lineal
    private void rankingPuntuacion() {

        Integer[] indices = new Integer[tamPoblacion];
        for (int i = 0; i < tamPoblacion; i++) {
            indices[i] = i;
        }
        // Para minimización: orden ascendente (los mejores, es decir, con menor fitness, primero)
        Arrays.sort(indices, Comparator.comparingDouble(i -> fitness[i]));

        double accPunc = 0;
        for (int i = 0; i < tamPoblacion; i++) {
            double probOfIth = (double) i / tamPoblacion;     //asigna un ranking basado en la posicion i en la lista
            probOfIth = 2 * (_beta - 1);       //ajusta la probabilida
            probOfIth = _beta - probOfIth;      //calcula la probabilidad final d seleccion
            probOfIth = (double) probOfIth * ((double) 1 / tamPoblacion);
            probAcu[indices[i]] = accPunc;
            prob[indices[i]] = probOfIth;
            accPunc += probOfIth;
        }
    }

    @Override
    public int[] getSeleccion() {
        rankingPuntuacion();
        Random rand = new Random();
        int pos = 0;
        for (int r = 0; r < tamPoblacion && pos < tamPoblacion; r++) {
            double rnd = rand.nextDouble();
            for (int i = 0; i < tamPoblacion; i++) {
                if (probAcu[i] >= rnd) {
                    seleccion[pos++] = i;
                    break;
                }
            }
        }
        return seleccion;
    }
}
