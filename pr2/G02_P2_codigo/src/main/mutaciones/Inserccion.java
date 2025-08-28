package main.mutaciones;

import main.individuo.Individuo;

import java.util.Random;


public class Inserccion extends Mutacion<Integer> {
    @Override
    public void mutar(Individuo<Integer> individuoRuta) {

        Random rand = new Random();
        Integer[] crom = individuoRuta.getCromosoma();
        int n = crom.length;
        int posOrigen = rand.nextInt(n);
        int posDestino = rand.nextInt(n);
        while (posDestino == posOrigen) {
            posDestino = rand.nextInt(n);
        }
        Integer gene = crom[posOrigen];
        if (posOrigen < posDestino) {
            // Desplazar los genes a la izquierda desde posOrigen hasta posDestino
            for (int i = posOrigen; i < posDestino; i++) {
                crom[i] = crom[i + 1];
            }
            crom[posDestino] = gene;
        } else { // posOrigen > posDestino
            // Desplazar los genes a la derecha desde posOrigen hasta posDestino
            for (int i = posOrigen; i > posDestino; i--) {
                crom[i] = crom[i - 1];
            }
            crom[posDestino] = gene;
        }
    }
}
