package main.mutaciones;

import main.individuo.Individuo;

import java.util.Random;

public class Inversion extends Mutacion<Integer> {

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
        int start = Math.min(i, j);
        int end = Math.max(i, j);
        // Invierte los elementos entre start y end
        while (start < end) {
            Integer temp = crom[start];
            crom[start] = crom[end];
            crom[end] = temp;
            start++;
            end--;
        }
        // Se actualiza el cromosoma en el individuo (si es necesario)
        ind.setCromosoma(crom);
    }
}
