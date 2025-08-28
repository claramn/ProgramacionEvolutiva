package main.cruces;

import main.utils.Bloating;

import java.util.Random;

/**
 * Implementación del cruce de un solo punto.
 * Se selecciona un punto aleatorio y se intercambian todos los genes antes de ese punto.
 */
public class CruceMonopunto extends Cruce<Integer[]> {

    /**
     * Constructor que inicializa el operador de cruce con el tamaño del cromosoma.
     *
     * @param tamCromosoma Tamaño del cromosoma (cantidad de genes).
     */
    public CruceMonopunto(int tamCromosoma) {
        super(tamCromosoma);
    }

    /**
     * Realiza el cruce de un solo punto entre dos cromosomas.
     * Se elige un punto aleatorio y se intercambian todos los genes antes de ese punto.
     *
     * @param c1 Primer cromosoma (se modifica directamente).
     * @param c2 Segundo cromosoma (se modifica directamente).
     */
    @Override
    public void cruzar(Integer[] c1, Integer[] c2, Bloating b) {
        Random rand = new Random();
        int puntoCorte = rand.nextInt(c1.length);

        for (int i = 0; i < puntoCorte; i++) {
            Integer temp = c1[i];
            c1[i] = c2[i];
            c2[i] = temp;
        }
    }
}
