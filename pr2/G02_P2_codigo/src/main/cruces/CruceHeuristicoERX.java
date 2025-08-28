package main.cruces;

import main.mapa.Casa;
import java.util.ArrayList;
import java.util.List;

public class CruceHeuristicoERX extends Cruce<Integer> {

    /**
     * Constructor.
     *
     * @param tamCromosoma Tamaño del cromosoma (por ejemplo, 20 habitaciones).
     */
    public CruceHeuristicoERX(int tamCromosoma) {
        super(tamCromosoma);
    }

    /**
     * Realiza el cruce combinando ERX con heurística basada en distancias.
     * Se construye la tabla de adyacencia a partir de ambos padres y se genera
     * la descendencia (dos hijos) seleccionando, en cada paso, el vecino disponible
     * con menor distancia desde la ciudad actual.
     *
     * @param c1 Primer padre (cromosoma válido).
     * @param c2 Segundo padre (cromosoma válido).
     */
    @Override
    public void cruzar(Integer[] c1, Integer[] c2) {
        // Construir la tabla de adyacencia combinando ambos padres.
        List<List<Integer>> adj = rutasIndividuos(c1, c2);

        // Podemos optar por generar dos hijos. Aquí, para simplificar,
        // asignaremos como hijo 1 el resultado del cruce a partir del primer gen de c1,
        // y como hijo 2 el resultado a partir del primer gen de c2.
        int start1 = c1[0];
        int start2 = c2[0];

        Integer[] child1 = buildChild(adj, start1);
        Integer[] child2 = buildChild(adj, start2);

        // Actualizar la descendencia (por ejemplo, sobrescribir a los padres)
        System.arraycopy(child1, 0, c1, 0, this.tamCromosoma);
        System.arraycopy(child2, 0, c2, 0, this.tamCromosoma);
    }

    /**
     * Construye un hijo a partir de la tabla de adyacencia y una ciudad de inicio.
     * En cada paso se selecciona el vecino (de la lista del gen actual) que minimice
     * la distancia usando Casa.getDistanciaEntreHabitaciones().
     *
     * @param adj   Tabla de vecinos (una lista para cada ciudad, considerando la numeración 1..n).
     * @param start Ciudad de inicio para el hijo.
     * @return Arreglo con la secuencia (cromosoma) del hijo.
     */
    private Integer[] buildChild(List<List<Integer>> adj, int start) {
        int n = this.tamCromosoma;
        Integer[] child = new Integer[n];
        boolean[] used = new boolean[n + 1]; // Índices: 1..n

        child[0] = start;
        used[start] = true;
        int current = start;

        for (int i = 1; i < n; i++) {
            List<Integer> vecinosDisponibles = new ArrayList<>();
            // Recopilar de la tabla los vecinos de 'current' que no han sido usados.
            for (int vecino : adj.get(current - 1)) {
                if (!used[vecino]) {
                    vecinosDisponibles.add(vecino);
                }
            }

            int next;
            if (vecinosDisponibles.isEmpty()) {
                // Si no hay vecinos disponibles, se escoge la primera ciudad no usada.
                next = pickUnused(used);
            } else {
                // Seleccionar el vecino con menor distancia, usando Casa.getDistanciaEntreHabitaciones.
                double bestDistance = Double.MAX_VALUE;
                int candidate = -1;
                for (int vecino : vecinosDisponibles) {
                    double d = Casa.getDistanciaEntreHabitaciones(current, vecino);
                    if (d < bestDistance) {
                        bestDistance = d;
                        candidate = vecino;
                    }
                }
                next = candidate;
            }

            child[i] = next;
            used[next] = true;
            current = next;
        }
        return child;
    }

    /**
     * Devuelve la primera ciudad (índice) no usada.
     */
    private int pickUnused(boolean[] used) {
        for (int i = 1; i < used.length; i++) {
            if (!used[i]) return i;
        }
        return 1; // Fallback (teóricamente no debería ocurrir)
    }

    /**
     * Construye la tabla de vecinos (lista de adyacencia) a partir de dos padres.
     * Cada ciudad (de 1 a n) tendrá, en la lista correspondiente (índice = ciudad-1),
     * los vecinos que aparecen a su izquierda y derecha en cada padre, sin duplicados.
     *
     * @param c1 Cromosoma del primer padre.
     * @param c2 Cromosoma del segundo padre.
     * @return Lista de listas con los vecinos para cada ciudad.
     */
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

    /**
     * Agrega a la tabla los vecinos de cada ciudad según el cromosoma c.
     * Se consideran el vecino izquierdo y el derecho (con conexión cíclica).
     *
     * @param tabla La tabla de vecinos.
     * @param c     El cromosoma (por ejemplo, de un padre).
     */
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
}
