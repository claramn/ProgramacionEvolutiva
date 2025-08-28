package main.mutaciones;

import main.individuo.Individuo;

import java.util.Random;

public class Intercambio extends Mutacion<Integer> {
    @Override
    public void mutar(Individuo<Integer> ind) {

        Random rand = new Random();
        Integer[] crom = ind.getCromosoma();
        int n = crom.length;
        // Selecciona dos índices distintos
        int i = rand.nextInt(n);
        int j = rand.nextInt(n);
        while (i == j) {
            j = rand.nextInt(n);
        }
        // Intercambia los elementos en las posiciones i y j
        Integer temp = crom[i];
        crom[i] = crom[j];
        crom[j] = temp;
        // Se actualiza el cromosoma en el individuo (si es necesario)
        ind.setCromosoma(crom);
    }
}
