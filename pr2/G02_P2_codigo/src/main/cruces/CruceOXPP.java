package main.cruces;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CruceOXPP extends Cruce<Integer> {

    private final int PRIORITARIOS = 3;
    private Set<Integer> prioridades;

    /**
     * Constructor que inicializa el tamaño del cromosoma.
     *
     * @param tamCromosoma Tamaño del cromosoma (número de genes).
     */
    public CruceOXPP(int tamCromosoma) {
        super(tamCromosoma);
        prioridades = new HashSet<Integer>();
    }

    @Override
    public void cruzar(Integer[] c1, Integer[] c2) {
        Random rand = new Random();
        int n = this.tamCromosoma;
        for (int i = 0; i < PRIORITARIOS; i++) {
            int index = rand.nextInt(this.tamCromosoma);
            while (prioridades.contains(index)) {
                index = rand.nextInt(this.tamCromosoma);
            }
            prioridades.add(index);
        }

        Integer[] child1 = new Integer[this.tamCromosoma];
        Integer[] child2 = new Integer[this.tamCromosoma];

        for (int i = 0; i < PRIORITARIOS; i++) {
            int index = prioridades.iterator().next();
            child1[index] = c2[index];
            child2[index] = c1[index];
            prioridades.remove(index);
        }

        fillChild(c1, n, child1);

        fillChild(c2, n, child2);

        System.arraycopy(child1, 0, c1, 0, this.tamCromosoma);
        System.arraycopy(child2, 0, c2, 0, this.tamCromosoma);

    }

    private void fillChild(Integer[] c1, int n, Integer[] child1) {

        int idPadre = 0;
        for (int i = 0; i < n; i++) {
            if (child1[i] == null) {
                while (contains(child1, c1[idPadre])) {
                    idPadre = (idPadre + 1) % n;
                }
                child1[i] = c1[idPadre];
                idPadre = (idPadre + 1) % n;
            }
        }

    }

}
