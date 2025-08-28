package main.cruces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CruceCO extends Cruce<Integer> {

    private List<Integer> dinamicList;
    /**
     * Constructor que inicializa el tamaño del cromosoma.
     *
     * @param tamCromosoma Tamaño del cromosoma (número de genes).
     */
    public CruceCO(int tamCromosoma) {

        super(tamCromosoma);

        generateDinamicList();
    }

    private void generateDinamicList() {
        dinamicList = new ArrayList<>();
        for (int i = 1; i <= tamCromosoma; i++) {
            dinamicList.add(i);
        }
        Collections.shuffle(dinamicList);
    }

    @Override
    public void cruzar(Integer[] c1, Integer[] c2) {

        Integer[] e1 = ordinalEncoding(c1);
        Integer[] e2 = ordinalEncoding(c2);

        int cut = tamCromosoma / 2;
        Integer[] h1 = new Integer[tamCromosoma];
        Integer[] h2 = new Integer[tamCromosoma];

        for (int i = 0; i < cut; i++) {
            h1[i] = e1[i];
            h2[i] = e2[i];
        }
        for (int i = cut; i < tamCromosoma; i++) {
            h1[i] = e2[i];
            h2[i] = e1[i];
        }

        System.arraycopy(ordinalDecoding(h1), 0, c1, 0, tamCromosoma);
        System.arraycopy(ordinalDecoding(h2), 0, c2, 0, tamCromosoma);
    }


    private Integer[] ordinalEncoding(Integer[] recorrido) {
        List<Integer> lista = new ArrayList<>(dinamicList);
        Integer[] encoded = new Integer[this.tamCromosoma];

        for (int i = 0; i < tamCromosoma; i++) {
            int pos = lista.indexOf(recorrido[i]) + 1;
            encoded[i] = pos;
            lista.remove(pos - 1);
        }
        return encoded;
    }

    private Integer[] ordinalDecoding(Integer[] encoded) {
        List<Integer> lista = new ArrayList<>(dinamicList);
        Integer[] decoded = new Integer[tamCromosoma];

        for (int i = 0; i < tamCromosoma; i++) {
            decoded[i] = lista.remove(encoded[i] - 1);
        }
        return decoded;
    }
}
