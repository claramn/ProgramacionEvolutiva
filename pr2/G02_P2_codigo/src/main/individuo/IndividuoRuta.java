package main.individuo;

import main.fitness.Fitness;
import main.mapa.Casa;
import main.mutaciones.Mutacion;

import java.awt.*;
import java.util.*;
import java.util.List;

public class IndividuoRuta extends Individuo<Integer> {

    private Random rand;

    /**
     * Constructor.
     *
     * @param casa             Instancia de Casa con la información de habitaciones y caminos.
     * @param fitnessEvaluator Evaluador de fitness.
     * @param mutacion         Operador de mutación.
     */
    public IndividuoRuta(Casa casa, Fitness<Integer> fitnessEvaluator, Mutacion<Integer> mutacion) {
        this.casa = casa;
        this.rand = new Random();
        this.d = casa.getNumHabitaciones();  // 20 genes
        this.cromosoma = new Integer[d];
        this.fitnessEvaluator = fitnessEvaluator;
        this.mutacionEvaluator = mutacion;

        // Inicializar el cromosoma con los números 1 a 20 (orden natural)
        for (int i = 0; i < d; i++) {
            this.cromosoma[i] = i + 1;
        }
        // Mezclar aleatoriamente la permutación
        shuffleChromosome();
    }

    /**
     * Mezcla el cromosoma usando el algoritmo de Fisher–Yates.
     * Invalida la ruta cache ya que el cromosoma ha cambiado.
     */
    private void shuffleChromosome() {
        for (int i = d - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = this.cromosoma[i];
            this.cromosoma[i] = this.cromosoma[j];
            this.cromosoma[j] = temp;
        }
    }
    /**
     * Retorna la ruta completa que debe seguir el individuo, como una lista de Points.
     * La ruta se construye desde la base, pasando por las habitaciones en el orden del cromosoma,
     * y regresando a la base. Si la ruta ya fue calculada, se devuelve la versión cache.
     *
     * @return Lista de Points que conforman el camino.
     */
    public List<Point> getRuta() {
        List<Point> ruta = new ArrayList<>();
        // Comenzamos en la base
        Point current = casa.getBase();
        ruta.add(current);

        // Recorrer cada habitación en el orden del cromosoma
        for (int i = 0; i < d; i++) {
            int id = this.cromosoma[i];
            Point destino = casa.getCoordenada(id);
            // Obtener el segmento entre el punto actual y el destino
            List<Point> segmento = casa.obtenerCamino(current, destino);
            if (segmento != null && !segmento.isEmpty()) {
                if (!current.equals(segmento.get(0))) {
                    Collections.reverse(segmento);
                }
                ruta.addAll(segmento.subList(1, segmento.size()));
                current = destino;
            }
        }

        // Agregar el tramo final: desde el último destino hasta la base
        List<Point> segmentoFinal = casa.obtenerCamino(current, casa.getBase());
        if (segmentoFinal != null && !segmentoFinal.isEmpty()) {
            if (!current.equals(segmentoFinal.get(0))) {
                Collections.reverse(segmentoFinal);
            }
            ruta.addAll(segmentoFinal.subList(1, segmentoFinal.size()));
        }
        return ruta;
    }

    @Override
    public Casa getCasa() {
        return casa;
    }


    @Override
    public double getFitness() {
        return fitnessEvaluator.evaluar(this);
    }

    /**
     * Aplica la mutación al individuo.
     */
    @Override
    public void mutacion() {
         this.mutacionEvaluator.mutar(this);
    }

    @Override
    public double getFenotipo(int n) {
        return this.cromosoma[n].doubleValue();
    }

    @Override
    public Individuo<Integer> clone() {
        IndividuoRuta nuevo = new IndividuoRuta(this.casa, this.fitnessEvaluator, this.mutacionEvaluator);
        nuevo.cromosoma = this.cromosoma.clone();
        nuevo.d = this.d;
        return nuevo;
    }
}
