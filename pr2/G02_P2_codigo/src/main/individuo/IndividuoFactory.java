package main.individuo;

import main.fitness.Fitness;
import main.fitness.FitnessFactory;
import main.mapa.Casa;
import main.mapa.Mapa;
import main.mapa.MapaFactory;
import main.mutaciones.Mutacion;
import main.mutaciones.MutacionFactory;
import main.utils.Pair;

public class IndividuoFactory  {

    private static final Pair<Double,Double> INTERVALO_1 = new Pair<>(80.00, 400.00);
    private static final Pair<Double,Double> INTERVALO_2 = new Pair<>(-120.0, 30.0);
    private static final Pair<Double,Double> INTERVALO_3 = new Pair<>(-200.0, 40.0);
    private static final Pair<Double,Double> INTERVALO_4 = new Pair<>(-15.0, 5.0);
    private static final Pair<Double,Double> INTERVALO_5 = new Pair<>(-15.0, 5.0);

    public static Individuo<?> getIndividuo(Mapa m, Fitness<?> f, Mutacion<?> mut) {
        return new IndividuoRuta((Casa) m, (Fitness<Integer>) f, (Mutacion<Integer>) mut);
    }

    public static Pair<Double,Double> getIntervalo(int n) {
        switch (n) {
            default: return INTERVALO_1;
        }
    }
}
