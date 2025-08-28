package main.cruces;

import java.util.ArrayList;
import java.util.List;

public class CruceERX extends Cruce<Integer> {

    /**
     * Constructor que inicializa el tamaño del cromosoma.
     *
     * @param tamCromosoma Tamaño del cromosoma (número de genes).
     */
    public CruceERX(int tamCromosoma) {
        super(tamCromosoma);
    }

    @Override
    public void cruzar(Integer[] c1, Integer[] c2) {
        Integer[] child1 = buildChild(rutasIndividuos(c1, c2), c1[0]);
        Integer[] child2 = buildChild(rutasIndividuos(c2, c1), c2[0]);

        System.arraycopy(child1, 0, c1, 0, this.tamCromosoma);
        System.arraycopy(child2, 0, c2, 0, this.tamCromosoma);

    }

    private Integer[] buildChild(List<List<Integer>> adj, int start) {
        int n = this.tamCromosoma;
        Integer[] child = new Integer[n];
        boolean[] used = new boolean[n + 1]; // Suponemos ciudades numeradas de 1 a n
        child[0] = start;
        used[start] = true;
        int current = start;

        for (int i = 1; i < n; i++) {
            List<Integer> vecinosDisponibles = new ArrayList<>();
            // Recolectamos los vecinos de la ciudad actual que aún no han sido usados
            for (int vecino : adj.get(current - 1)) {
                if (!used[vecino]) {
                    vecinosDisponibles.add(vecino);
                }
            }

            int next;
            if (vecinosDisponibles.isEmpty()) {
                // Si no hay vecinos disponibles, elige la primera no usada (o implementa una selección aleatoria)
                next = pickUnused(used);
            } else {
                // Determinar el grado mínimo (tamaño de la lista de vecinos) entre los vecinos disponibles
                int minDegree = Integer.MAX_VALUE;
                for (int vecino : vecinosDisponibles) {
                    int degree = adj.get(vecino - 1).size();
                    if (degree < minDegree) {
                        minDegree = degree;
                    }
                }
                // Recopilar todos los vecinos que tienen ese grado mínimo
                List<Integer> candidatos = new ArrayList<>();
                for (int vecino : vecinosDisponibles) {
                    if (adj.get(vecino - 1).size() == minDegree) {
                        candidatos.add(vecino);
                    }
                }
                // Se elige aleatoriamente entre los candidatos empatados
                int randomIndex = (int) (Math.random() * candidatos.size());
                next = candidatos.get(randomIndex);
            }

            child[i] = next;
            used[next] = true;
            current = next;
        }
        return child;

        /** List<List<Integer>> rutas = rutasIndividuos(c1, c2);

         Integer[] child1 = new Integer[this.tamCromosoma];
         Integer[] child2 = new Integer[this.tamCromosoma];

         //Se construye el hijo 1 tomando como primer elemento el del padre 2

         child1[0] = c2[0];
         int current = c2[0];
         for (int i = 1; i < this.tamCromosoma; i++) {
         int chosen;
         List<Integer> vecinos = rutas.get(current - 1);
         if (vecinos.size() > 1) {
         chosen = vecinos.get(0);
         boolean rand = true;
         for (int j = 1; j < vecinos.size(); j++) {
         //Si salen 2 tamaños iguales y uno diferente mayor a los iguales, se queda con el primer tamaño igual
         // Mirar si sirve esta alatoridad de seleccionar siempre el primero
         if (rutas.get(vecinos.get(j)).size() < rutas.get(chosen).size()) {
         chosen = vecinos.get(j);
         rand = false;
         }
         }
         } else {
         chosen = vecinos.get(0);
         }
         child1[i] = chosen;
         current = chosen;
         }

         //Hay que hacer lo analogo con el segundo hijo, pero comprobar que se pueden crear individuos factibles
         */
    }

    private List<List<Integer>> rutasIndividuos(Integer[] c1, Integer[] c2) {
        int n = c1.length;
        List<List<Integer>> tabla = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            tabla.add(new ArrayList<>());
        }
        agregarVecinos(tabla, c1);
        agregarVecinos(tabla, c2);
        return tabla;
    }

    private void agregarVecinos(List<List<Integer>> tabla, Integer[] c) {
        int n = c.length;
        for (int i = 0; i < n; i++) {
            int ciudad = c[i];
            int izq = c[(i - 1 + n) % n];
            int der = c[(i + 1) % n];
            if (!tabla.get(ciudad - 1).contains(izq)) {
                tabla.get(ciudad - 1).add(izq);
            }
            if (!tabla.get(ciudad - 1).contains(der)) {
                tabla.get(ciudad - 1).add(der);
            }
        }
    }

    private int pickUnused(boolean[] used) {
        // Escoge la primera ciudad no usada
        for (int i = 1; i < used.length; i++) {
            if (!used[i]) return i;
        }
        return 1;
    }

    private int pickRandomUnused(boolean[] used) {
        List<Integer> unused = new ArrayList<>();
        for (int i = 1; i < used.length; i++) {
            if (!used[i]) {
                unused.add(i);
            }
        }
        if (!unused.isEmpty()) {
            int idx = (int) (Math.random() * unused.size());
            return unused.get(idx);
        }
        return 1;
    }
}
