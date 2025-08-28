package main.fitness;

import main.individuo.Individuo;

import java.awt.*;


/**
 * Clase abstracta que define la interfaz para evaluar el fitness de un individuo.
 *
 * @param <T> Tipo de dato que utiliza el cromosoma del individuo.
 */
public abstract class Fitness<T> {
    protected static final double PENALTY_VALUE = 500.0;
    /**
     * Evalúa y devuelve el fitness del individuo dado.
     *
     * @param individuo El individuo a evaluar.
     * @return Valor numérico del fitness.
     */
    public abstract double evaluar(Individuo<T> individuo);
}
