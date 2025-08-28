package main.fitness;

import main.individuo.Individuo;
import main.individuo.IndividuoRuta;

import java.awt.*;

public class FitnessDistanciaEuclidiana extends Fitness<Integer> {
    @Override
    public double evaluar(Individuo<Integer> individuo) {
        double penalizacion = 0;
        IndividuoRuta ind = (IndividuoRuta) individuo;
        var cromosoma = ind.getCromosoma();
        var casa = ind.getCasa();
        double total = 0.0;
        double seg;
        seg = casa.distanciaDesdeBase(cromosoma[0]);
        double distanciaEuclediana = distanciaEuclidiana(casa.getBase(), casa.getHabitaciones().get(cromosoma[0]));
        double desviacion = seg - distanciaEuclediana;
        if (desviacion > 0) penalizacion += desviacion * 2;
        total += (seg == Double.MAX_VALUE ? PENALTY_VALUE : seg);
        for (int i = 1; i < casa.getNumHabitaciones(); i++) {
            seg = casa.distanciaEntre(cromosoma[i - 1], cromosoma[i]);
            total += (seg == Double.MAX_VALUE ? PENALTY_VALUE : seg);
            distanciaEuclediana = distanciaEuclidiana(casa.getHabitaciones().get(cromosoma[i - 1]), casa.getHabitaciones().get(cromosoma[i]));
            desviacion = seg - distanciaEuclediana;
            if (desviacion > 0) penalizacion += desviacion * 2;
        }
        seg = casa.distanciaDesdeBase(cromosoma[casa.getNumHabitaciones() - 1]);
        distanciaEuclediana = distanciaEuclidiana(casa.getHabitaciones().get(casa.getNumHabitaciones() - 1), casa.getBase());
        desviacion = seg - distanciaEuclediana;
        if (desviacion > 0) penalizacion += desviacion * 2;
        total += (seg == Double.MAX_VALUE ? PENALTY_VALUE : seg);
        return total + penalizacion;
    }


    private double distanciaEuclidiana(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }
}
