package main.mutaciones;

import main.individuo.Individuo;

import java.util.Random;

public class MutacionGramatica extends Mutacion<Integer[]> {
    private final static double p = 0.1;
    Random rand = new Random();


    @Override
    public void mutar(Individuo<Integer[]> ind) {
       Integer[] crom = ind.getCromosoma();
            for (int i = 0; i < crom.length; i++) {
                if (this.rand.nextDouble() < p) {
                    // Opción 1: Mutación por reemplazo con valor aleatorio
                    crom[i] = rand.nextInt(256); // Rango típico 0-255 para codones

                    // Opción 2: Mutación incremental (variación pequeña)
                    // int cambio = rand.nextInt(21) - 10; // [-10, +10]
                    // crom[i] = Math.abs(crom[i] + cambio) % 256; // Aseguramos positivo y en rango

                    // Opción 3: Mutación basada en módulo (útil para gramáticas)
                    // crom[i] = (crom[i] + rand.nextInt(50) + 1) % 256;
                }
            }
        ind.setCromosoma(crom);
    }
}
