package main.cruces;

import main.utils.Bloating;

import java.util.Random;

/**
        * Implementación del cruce uniforme.
        * Cada gen tiene una probabilidad del 50% de ser intercambiado entre los cromosomas.
        */
public class CruceUniforme extends Cruce<Integer[]> {

    private static final double PROBABILITY = 0.5;

    /**
     * Constructor que inicializa el operador de cruce con el tamaño del cromosoma.
     *
     * @param tamCromosoma Tamaño del cromosoma (cantidad de genes).
     */
    public CruceUniforme(int tamCromosoma) {
        super(tamCromosoma);
    }

    /**
     * Realiza el cruce uniforme entre dos cromosomas.
     * Cada gen tiene un 50% de probabilidad de ser intercambiado entre los cromosomas.
     *
     * @param c1 Primer cromosoma (se modifica directamente).
     * @param c2 Segundo cromosoma (se modifica directamente).
     */
    @Override
    public void cruzar(Integer[] c1, Integer[] c2, Bloating b) {
        Random rand = new Random();
        for (int i = 0; i < c1.length; i++) {
            if (rand.nextDouble() > PROBABILITY) {
                Integer temp = c1[i];
                c1[i] = c2[i];
                c2[i] = temp;
            }
        }
    }
}
