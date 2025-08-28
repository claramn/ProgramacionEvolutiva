package main.fitness;

import main.individuo.Individuo;
import main.individuo.IndividuoRuta;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class FitnessPenalizacionProximidadObstaculos extends Fitness<Integer> {

    // Umbral a partir del cual se empieza a penalizar la proximidad a obstáculos.
    private static final double UMBRAL = 1.0;
    // Factor multiplicador de la penalización.
    private static final double FACTOR_PENALIZACION = 5.0;

    private Set<Point> obstaculos = new HashSet<>();
    @Override
    public double evaluar(Individuo<Integer> individuo) {
        double total = 0.0;
        double seg;
        IndividuoRuta ind = (IndividuoRuta) individuo;
        var cromosoma = ind.getCromosoma();
        var casa = ind.getCasa();
        obstaculos = casa.getObstaculos();

        // Desde la base hasta la primera habitación.
        seg = casa.distanciaDesdeBase(cromosoma[0]);
        total += (seg == Double.MAX_VALUE ? PENALTY_VALUE : seg);
        // Penalización por proximidad en la primera habitación.
        total += penalizacionPorObstaculos(casa.getPosicionHabitacion(cromosoma[0]));

        // Para cada segmento entre habitaciones consecutivas
        for (int i = 1; i < casa.getNumHabitaciones(); i++) {
            seg = casa.distanciaEntre(cromosoma[i - 1], cromosoma[i]);
            total += (seg == Double.MAX_VALUE ? PENALTY_VALUE : seg);
            // Penalización por proximidad para la habitación actual.
            total += penalizacionPorObstaculos(casa.getPosicionHabitacion(cromosoma[i]));
        }

        // Desde la última habitación de vuelta a la base.
        seg = casa.distanciaDesdeBase(cromosoma[casa.getNumHabitaciones() - 1]);
        total += (seg == Double.MAX_VALUE ? PENALTY_VALUE : seg);

        return total;
    }

    /**
     * Calcula la penalización extra por proximidad a obstáculos para una posición dada.
     * Si la distancia mínima desde esa posición a un obstáculo es menor que UMBRAL,
     * se devuelve FACTOR_PENALIZACION * (UMBRAL - distanciaMínima); si no, se devuelve 0.
     *
     * @param pos Posición (Point) a evaluar.
     * @return Penalización extra (0 si está a una distancia segura).
     */
    private double penalizacionPorObstaculos(Point pos) {
        double minDist = Double.MAX_VALUE;
        for (Point obs : obstaculos) {
            double d = pos.distance(obs);
            if (d < minDist) {
                minDist = d;
            }
        }
        if (minDist < UMBRAL) {
            return FACTOR_PENALIZACION * (UMBRAL - minDist);
        }
        return 0.0;
    }
}
