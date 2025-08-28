package main.individuo;

import main.fitness.Fitness;
import main.mapa.Casa;
import main.mutaciones.Mutacion;
import main.utils.Arbol;
import main.utils.Gramatica;
import main.utils.Pair;
import main.utils.ReglaProduccion;

import java.awt.*;
import java.util.*;
import java.util.List;

public class IndividuoGramatica extends Individuo<Integer[]> {
    private Gramatica gramatica;
    private List<Point> ruta;
    private int[] tam;
    private String resultado;
    private Random rand;
    private final static int MAX_VALOR_GEN = 3;
    private final int maxProfundidad;
    private int wraps;
    private Map<String, List<String>> reglas = Map.of(
            "<expr>", List.of("<func>", "<term>"),
            "<func>", List.of("SICOMIDA(<expr>,<expr>)", "PROG2(<expr>,<expr>)", "PROG3(<expr>,<expr>,<expr>)"),
            "<term>", List.of("AVANZA", "DERECHA", "IZQUIERDA")
    );
    private static final List<String> PALABRAS_CLAVE = Arrays.asList(
            "PROG2", "PROG3", "SICOMIDA", "AVANZA", "IZQUIERDA", "DERECHA"
    );  //para contar "nodos"

    public IndividuoGramatica(Casa casa, Fitness<Integer[]> fitnessEvaluator,
                              Mutacion<Integer[]> mutacionEvaluator, int longitudCromosoma, int maxProf, int wraps) {
        this.casa = casa;
        this.fitnessEvaluator = fitnessEvaluator;
        this.mutacionEvaluator = mutacionEvaluator;
        this.ruta = new ArrayList<>();
        this.cromosoma = new Integer[longitudCromosoma];
        gramatica = new Gramatica();
        rand = new Random();
        this.wraps = wraps;
        this.maxProfundidad = maxProf;
        tam = new int[1];
        for (int i = 0; i < longitudCromosoma; i++) {
            cromosoma[i] = rand.nextInt(800);
        }

    }

    @Override
    public void mutacion() {
        this.mutacionEvaluator.mutar(this);
    }

    @Override
    public double getFitness() {
        return fitnessEvaluator.evaluar(this);
    }

    @Override
    public int getTamGenes() {
        return contarPalabrasClave(resultado);
    }

    @Override
    public Integer[] getCromosoma() {
        return cromosoma;
    }

    @Override
    public void setCromosoma(Integer[] c1) {
        cromosoma = c1.clone();
    }


    @Override
    public double[] getFenotipos() {
        return new double[0];
    }

    @Override
    public Individuo<Integer[]> clone() {
        IndividuoGramatica nuevo = new IndividuoGramatica(casa, fitnessEvaluator, mutacionEvaluator, cromosoma.length, maxProfundidad, wraps);
        nuevo.cromosoma = new Integer[cromosoma.length];
        for (int i = 0; i < nuevo.cromosoma.length; i++) {
            nuevo.cromosoma[i] = cromosoma[i];
        }
        nuevo.setRuta(this.ruta);
        nuevo.gramatica = gramatica;
        nuevo.resultado = resultado;
        nuevo.pasosVacios = pasosVacios;
        nuevo.giros = giros;
        nuevo.pasos = pasos;
        nuevo.wraps = wraps;
        return nuevo;
    }

    @Override
    public void setRuta(List<Point> ruta) {
        this.ruta = ruta;
    }

    @Override
    public Casa getCasa() {
        return casa;
    }

    @Override
    public List<Point> getRuta() {
        return ruta;
    }

    @Override
    public String getResult() {
        return resultado;
    }

    @Override
    public void setResult(String result) {
        resultado = result;
    }

    @Override
    public List<Arbol> getNodosTerminales() {
        return List.of();
    }

    @Override
    public void actualizaNodosTerminales() {

    }

    @Override
    public String obtenerPrograma() {
        return gramatica.mapear(cromosoma, maxProfundidad, tam, wraps);
    }

    @Override
    public Pair<Integer, Integer> getPasos() {
        return new Pair<>(this.pasos, this.pasosVacios);
    }

    @Override
    public void setPasos(int pasos, int pasosVacios) {
        this.pasos = pasos;
        this.pasosVacios = pasosVacios;
    }

    private static int contarPalabrasClave(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        int count = 0;
        String[] partes = input.split("[(),]"); // Dividir por paréntesis o comas

        for (String parte : partes) {
            parte = parte.trim(); // Eliminar espacios en blanco
            if (PALABRAS_CLAVE.contains(parte)) {
                count++;
            }
        }

        return count;
    }

    @Override
    public String toString() {
        return resultado;
    }

}
