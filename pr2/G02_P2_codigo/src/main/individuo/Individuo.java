package main.individuo;

import main.fitness.Fitness;
import main.mapa.Casa;
import main.mutaciones.Mutacion;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Individuo<T> {

    protected T[] cromosoma;
    protected int d;

    protected Casa casa;
    protected Fitness<Integer> fitnessEvaluator;
    protected Mutacion<Integer> mutacionEvaluator;


    public abstract double getFenotipo(int n);

    public abstract void mutacion();

    public abstract double getFitness();

    public int getTamGenes() {
        return cromosoma.length;
    }

    public T[] getCromosoma() {
        return cromosoma;
    }

    public void setCromosoma(T[] c1) {
        cromosoma = c1;
    }

    public double[] getFenotipos() {
        double[] fenotipos = new double[cromosoma.length];
        for (int i = 0; i < cromosoma.length; i++) {
            fenotipos[i] = getFenotipo(i);
        }
        return fenotipos;
    }

    public abstract Individuo<T> clone();

    public abstract List<Point> getRuta();

    public Map<Integer, Integer> getRoomsVisitOrder() {
        // Mapa: roomId -> índice de visita (1,2,3,...)
        Map<Integer, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < d; i++) {
            T roomId =  this.cromosoma[i];
            // i+1 porque el primer elemento del cromosoma se visita en el paso 1
            orderMap.put((int) roomId, i + 1);
        }
        return orderMap;
    }


    public abstract Casa getCasa();
}
