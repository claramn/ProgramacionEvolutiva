package main.ventana;

import main.individuo.Individuo;
import main.utils.Pair;

import java.awt.*;
import java.util.List;

public class DTOResponse {

    protected Pair<Double, Double> intervalo;
    protected Individuo<?> elMejor;
    protected double[] mediaGeneracion;
    protected double[] mejorGeneracion;
    protected double[] mejorAbsoluto;
    protected int numCruce;
    protected int numMutaciones;

    public DTOResponse(double[] mediaGeneracion, double[] mejorGeneracion, double[] mejorAbsoluto, Pair<Double, Double> intervalo, Individuo<?> elMejor, int numCruce, int numMutaciones) {
        this.mediaGeneracion = mediaGeneracion;
        this.mejorGeneracion = mejorGeneracion;
        this.mejorAbsoluto = mejorAbsoluto;
        this.intervalo = intervalo;
        this.elMejor = elMejor;
        this.numCruce = numCruce;
        this.numMutaciones = numMutaciones;
    }


    public List<Point> getOptimalRoute() {
        return elMejor.getRuta();
    }
}