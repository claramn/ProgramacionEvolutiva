package main.cruces;

import java.util.Random;

public class CruceCX extends Cruce<Integer> {

    private int posInicial;

    /**
     * Constructor que inicializa el tamaño del cromosoma.
     *
     * @param tamCromosoma Tamaño del cromosoma (número de genes).
     */
    public CruceCX(int tamCromosoma) {
        super(tamCromosoma);
    }

    @Override
    public void cruzar(Integer[] c1, Integer[] c2) {

        posInicial = 0;

        Integer[] child1 = new Integer[this.tamCromosoma];
        Integer[] child2 = new Integer[this.tamCromosoma];

        cicloSucesion(c1, c2, child1);

        cicloSucesion(c2, c1, child2);

        rellenaHijos(c2, child1);

        rellenaHijos(c1, child2);

        System.arraycopy(child1, 0, c1, 0, this.tamCromosoma);

        System.arraycopy(child2, 0, c2, 0, this.tamCromosoma);


    }

    /**
     * Rellena las posiciones que quedaron sin asignar en el hijo.
     *
     * @param source Array del cual se toman los genes para rellenar.
     * @param child  Array hijo que se está construyendo.
     */
    private void rellenaHijos(Integer[] source, Integer[] child) {
        for (int i = 0; i < this.tamCromosoma; i++) {
            if (child[i] == null) {
                child[i] = source[i];
            }
        }
    }

    /**
     * Determina el ciclo (sucesión) para el cruce por ciclos.
     * Se parte desde la posición posInicial y se copian en 'child' las posiciones que forman el ciclo.
     * El ciclo se forma de la siguiente manera:
     * 1. child[i] = p1[i] en la posición de inicio.
     * 2. Se toma el gen de p2 en esa misma posición, se busca en p1 el índice donde aparece ese
     * gen y se repite el proceso hasta volver a la posición inicial.
     *
     * @param p1    Primer padre (del cual se extraerá el ciclo).
     * @param p2    Segundo padre (usado para determinar la sucesión).
     * @param child Array donde se copia el ciclo.
     */
    private void cicloSucesion(Integer[] p1, Integer[] p2, Integer[] child) {

        int startIndex = posInicial;
        int index = startIndex;

        do {
            child[index] = p1[index];
            // Tomamos el gen del segundo padre en la posición 'index'
            int gene = p2[index];
            // Buscamos el índice en p1 donde aparece ese gen
            index = findIndex(p1, gene);
        } while (index != startIndex);
    }

    /**
     * Busca y devuelve el índice donde se encuentra el gen especificado en el arreglo p.
     *
     * @param p    Arreglo (padre) donde buscar.
     * @param gene El gen a buscar.
     * @return El índice donde p[i] == gene.
     */
    private int findIndex(Integer[] p, int gene) {
        for (int i = 0; i < p.length; i++) {
            if (p[i] == gene) {
                return i;
            }
        }
        return -1; // Este caso no debería ocurrir
    }
}
