package main.mutaciones;

import main.individuo.Individuo;

import java.util.ArrayList;
import java.util.List;

public class Heuristica extends Mutacion<Integer> {

    private static final double PROBABILIDAD_MUTACION_GEN = 0.15;

    private static final double NUM_GENES_MUTACION = 4;

    @Override
    public void mutar(Individuo<Integer> individuoRuta) {

        Integer[] cromosomaOriginal = individuoRuta.getCromosoma().clone();
        List<Integer> genesSeleccionados = new ArrayList<>();
        List<Integer> indicesSeleccionados = new ArrayList<>();

        int cont = 0;
        for (int i = 0; i < cromosomaOriginal.length; i++) {
            if (Math.random() < PROBABILIDAD_MUTACION_GEN) {
                genesSeleccionados.add(cromosomaOriginal[i]);
                indicesSeleccionados.add(i);
                cont++;
            }
            if (cont == NUM_GENES_MUTACION) {
                break;
            }
        }

        if (genesSeleccionados.size() <= 1) {
            return;
        }

        List<List<Integer>> permutaciones = permutar(new ArrayList<>(genesSeleccionados));

        Individuo<Integer> best = individuoRuta.clone();
        double bestFitness = best.getFitness();

        // Evaluar cada permutación y aplicar la mejora al cromosoma
        for (List<Integer> permutacion : permutaciones) {
            Integer[] nuevoCromosoma = aplicarPermutacion(cromosomaOriginal, indicesSeleccionados, permutacion);
            Individuo<Integer> nuevoIndividuo = individuoRuta.clone();
            nuevoIndividuo.setCromosoma(nuevoCromosoma);

            double fitnessActual = nuevoIndividuo.getFitness();
            if (fitnessActual < bestFitness) {
                best = nuevoIndividuo;
                bestFitness = fitnessActual;
            }
        }

        individuoRuta.setCromosoma(best.getCromosoma());
    }

    /**
     * Genera todas las permutaciones posibles de la lista de elementos.
     * Esta función funciona de manera recursiva usando listas.
     *
     * @param elementos La lista de elementos a permutar.
     * @return Una lista de permutaciones, donde cada permutación es una lista de enteros.
     */
    public static List<List<Integer>> permutar(List<Integer> elementos) {
        if (elementos.isEmpty()) {
            List<List<Integer>> resultado = new ArrayList<>();
            resultado.add(new ArrayList<>());
            return resultado;
        }
        List<List<Integer>> permutaciones = new ArrayList<>();
        Integer primerElemento = elementos.remove(0);
        List<List<Integer>> subPermutaciones = permutar(elementos);
        for (List<Integer> subPermutacion : subPermutaciones) {
            for (int i = 0; i <= subPermutacion.size(); i++) {
                List<Integer> nuevaPermutacion = new ArrayList<>(subPermutacion);
                nuevaPermutacion.add(i, primerElemento);
                permutaciones.add(nuevaPermutacion);
            }
        }
        elementos.add(0, primerElemento);
        return permutaciones;
    }

    /**
     * Aplica una permutación al cromosoma del individuo.
     *
     * @param cromosomaOriginal    El cromosoma original.
     * @param indicesSeleccionados Las posiciones de los genes que serán intercambiados.
     * @param permutacion          La permutación que se aplicará a los genes seleccionados.
     * @return Un nuevo cromosoma con la permutación aplicada.
     */
    private Integer[] aplicarPermutacion(Integer[] cromosomaOriginal, List<Integer> indicesSeleccionados,
                                         List<Integer> permutacion) {
        Integer[] nuevoCromosoma = cromosomaOriginal.clone();
        for (int i = 0; i < indicesSeleccionados.size(); i++) {
            nuevoCromosoma[indicesSeleccionados.get(i)] = permutacion.get(i);
        }
        return nuevoCromosoma;
    }
}
