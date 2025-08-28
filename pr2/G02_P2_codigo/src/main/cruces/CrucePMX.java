package main.cruces;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CrucePMX extends Cruce<Integer> {
    public CrucePMX(int tamCromosoma) {
        super(tamCromosoma);
    }

    @Override
    public void cruzar(Integer[] c1, Integer[] c2) {
        Random rand = new Random();
        int cut1 = rand.nextInt(this.tamCromosoma);
        int cut2 = rand.nextInt(this.tamCromosoma);
        if (cut1 > cut2) {
            int tmp = cut1;
            cut1 = cut2;
            cut2 = tmp;
        }

        // Crear copias temporales de los padres para preservar los valores originales.
        Integer[] parent1 = c1.clone();
        Integer[] parent2 = c2.clone();

        Integer[] child1 = new Integer[this.tamCromosoma];
        Integer[] child2 = new Integer[this.tamCromosoma];

        // 1) Intercambiar subsegmento
        for (int i = cut1; i <= cut2; i++) {
            child1[i] = c2[i];
            child2[i] = c1[i];
        }

        Map<Integer, Integer> mapping1 = new HashMap<>();
        Map<Integer, Integer> mapping2 = new HashMap<>();

        // 2) Construir mapping
        for (int i = cut1; i <= cut2; i++) {
            mapping1.put(parent2[i], parent1[i]);
            mapping2.put(parent1[i], parent2[i]);
        }

        //Paso 3: Rellenar las posiciones fuera del subsegmento en child1.
        for (int i = 0; i < this.tamCromosoma; i++) {
            if (i >= cut1 && i <= cut2) continue;
            Integer gene = parent1[i];
            while (inSegment(child1, gene, cut1, cut2)) {
                gene = mapping1.get(gene);
            }
            child1[i] = gene;
        }

        // Paso 4: Rellenar las posiciones fuera del subsegmento en child2 de forma análoga.
        for (int i = 0; i < this.tamCromosoma; i++) {
            if (i >= cut1 && i <= cut2) continue;
            Integer gene = parent2[i];
            while (inSegment(child2, gene, cut1, cut2)) {
                gene = mapping2.get(gene);
            }
            child2[i] = gene;
        }

        System.arraycopy(child1, 0, c1, 0, this.tamCromosoma);
        System.arraycopy(child2, 0, c2, 0, this.tamCromosoma);

    }

    private boolean inSegment(Integer[] child, Integer gene, int cut1, int cut2) {
        for (int i = cut1; i <= cut2; i++) {
            if (child[i] != null && child[i].equals(gene)) {
                return true;
            }
        }
        return false;
    }
}