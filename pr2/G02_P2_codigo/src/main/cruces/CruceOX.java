package main.cruces;

import java.util.Random;

public class CruceOX extends Cruce<Integer> {

    public CruceOX(int tamCromosoma) {
        super(tamCromosoma);
    }

    @Override
    public void cruzar(Integer[] c1, Integer[] c2) {
        Random rand = new Random();
        int n = tamCromosoma;
        // Elegir dos puntos de corte aleatorios y asegurarse que cut1 <= cut2.
        int cut1 = rand.nextInt(n);
        int cut2 = rand.nextInt(n);
        if (cut1 > cut2) {
            int tmp = cut1;
            cut1 = cut2;
            cut2 = tmp;
        }

        Integer[] child1 = new Integer[n];
        Integer[] child2 = new Integer[n];

        for (int i = cut1; i <= cut2; i++) {
            child1[i] = c2[i];
            child2[i] = c1[i];
        }
        
        int currentIndex = (cut2 + 1) % n;
        while (hayHuecos(child1)) {
            int val = c1[currentIndex];
            while (contains(child1, val)) {
                val = (val % n) + 1;
            }
            child1[currentIndex] = val;
            currentIndex = (currentIndex + 1) % n;
        }


        currentIndex = (cut2 + 1) % n;
        while (hayHuecos(child2)) {
            int val = c2[currentIndex];
            while (contains(child2, val)) {
                val = (val % n) + 1;
            }
            child2[currentIndex] = val;
            currentIndex = (currentIndex + 1) % n;
        }

        System.arraycopy(child1, 0, c1, 0, this.tamCromosoma);
        System.arraycopy(child2, 0, c2, 0, this.tamCromosoma);
    }

}
