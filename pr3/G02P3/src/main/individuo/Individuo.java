package main.individuo;

import main.fitness.Fitness;
import main.mapa.Casa;
import main.mutaciones.Mutacion;
import main.utils.Arbol;
import main.utils.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Individuo<T> {

    protected T cromosoma;

    protected Casa casa;
    protected Fitness<T> fitnessEvaluator;
    protected Mutacion<T> mutacionEvaluator;
    protected int pasosVacios;
    protected int giros;
    protected int pasos;

    /**
     * Devuelve un símbolo correspondiente a una función, en función del índice.
     * Se mapea:
     *   0 -> "SICOMIDA"
     *   1 -> "PROG2"
     *   2 -> "PROG3"
     *
     * @param index Valor entero que determina qué función se devuelve.
     * @return Cadena representando la función.
     */
    public static String getFunc(int index) {
        // Utilizamos el módulo para asegurar un índice válido, ya que se esperan 3 funciones.
        switch (index % 3) {
            case 0:
                return "SICOMIDA";
            case 1:
                return "PROG2";
            case 2:
                return "PROG3";
            default:
                return "SICOMIDA";
        }
    }

    /**
     * Devuelve un símbolo correspondiente a un terminal, en función del índice.
     * Se mapea:
     *   0 -> "AVANZA"
     *   1 -> "DERECHA"
     *   2 -> "IZQUIERDA"
     *
     * Si se utiliza un índice mayor (por ejemplo, se llama con nextInt(6)), se usa el módulo para
     * forzar la selección entre los 3 terminales disponibles.
     *
     * @param index Valor entero que determina qué terminal se devuelve.
     * @return Cadena representando el terminal.
     */
    public static String getTerm(int index) {
        // Se aplica módulo 3 para tener 3 terminales: AVANZA, DERECHA, IZQUIERDA.
        switch (index % 3) {
            case 0:
                return "AVANZA";
            case 1:
                return "DERECHA";
            case 2:
                return "IZQUIERDA";
            default:
                return "AVANZA";
        }
    }

    public abstract void mutacion();

    public abstract double getFitness();

    public abstract int getTamGenes();

    public abstract T getCromosoma();

    public abstract void setCromosoma(T c1);

    public abstract double[] getFenotipos();

    public abstract Individuo<T> clone();

    public abstract void setRuta(List<Point> ruta);
    public abstract Casa getCasa();
    public abstract List<Point> getRuta();
    public abstract String getResult();
    public abstract void setResult(String result);
    public abstract List<Arbol> getNodosTerminales();
    public abstract void actualizaNodosTerminales();
    public abstract String obtenerPrograma();
    public abstract Pair<Integer,Integer> getPasos();
    public abstract void setPasos( int pasosTotales,int pasosVacios);
}
