package main.individuo;

import main.fitness.Fitness;
import main.mapa.Casa;
import main.mapa.Mapa;
import main.mutaciones.Mutacion;
import main.utils.Arbol;
import main.utils.Pair;

public class IndividuoFactory  {

    private static final Pair<Double,Double> INTERVALO_1 = new Pair<>(80.00, 400.00);

    public static Individuo<?> getIndividuo(Casa m, Fitness<?> f, Mutacion<?> mut, int maxProfundidad,int n,int metodoIni, int minProf,int wraps) {
        switch (n){
            case 0:
                return new IndividuoHormiga( m, (Fitness<Arbol>) f, (Mutacion<Arbol>) mut, maxProfundidad, metodoIni,minProf);

            case 1:
                return new IndividuoGramatica( m, (Fitness<Integer[]>) f, (Mutacion<Integer[]>) mut, 15, maxProfundidad, wraps);

        }
        return null;
    }

    public static Pair<Double,Double> getIntervalo(int n) {
        switch (n) {
            default: return INTERVALO_1;
        }
    }
}
