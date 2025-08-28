package main.fitness;

import main.individuo.Individuo;
import main.individuo.IndividuoRuta;

public class FitnessBasico extends Fitness<Integer> {

    /**
     * Evalúa el fitness de un IndividuoRuta.
     * Se asume que el individuo es del tipo IndividuoRuta, el cual debe tener un método
     * público getDistanciaTotal() que devuelva la distancia total del recorrido.
     *
     * @param individuo El individuo a evaluar.
     * @return El fitness calculado.
     */
    @Override
    public double evaluar(Individuo<Integer> individuo) {
        IndividuoRuta ind = (IndividuoRuta) individuo;
        var cromosoma = ind.getCromosoma();
        var casa = ind.getCasa();
        double total = 0.0;
        double seg;
        seg = casa.distanciaDesdeBase(cromosoma[0]);
        total += (seg == Double.MAX_VALUE ? PENALTY_VALUE : seg);
        for (int i = 1; i < casa.getNumHabitaciones(); i++) {
            seg = casa.distanciaEntre(cromosoma[i - 1], cromosoma[i]);
            total += (seg == Double.MAX_VALUE ? PENALTY_VALUE : seg);
        }
        seg = casa.distanciaDesdeBase(cromosoma[casa.getNumHabitaciones() - 1]);
        total += (seg == Double.MAX_VALUE ? PENALTY_VALUE : seg);
        return total;
    }
}
