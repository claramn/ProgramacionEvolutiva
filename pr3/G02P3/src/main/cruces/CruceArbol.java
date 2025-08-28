package main.cruces;

import main.utils.Arbol;
import main.utils.Bloating;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CruceArbol extends Cruce<Arbol> {
    private static final Random random = new Random();
    private int bloating;

    /**
     * Constructor que inicializa el tamaño del cromosoma.
     *
     * @param tamCromosoma Tamaño del cromosoma (número de genes).
     */
    public CruceArbol(int tamCromosoma,int bloating) {
        super(tamCromosoma);
        this.bloating = bloating;
    }

    @Override
    public void cruzar(Arbol a1, Arbol a2,Bloating bl) {
        List<Arbol> funciones1 = new ArrayList<>();
        List<Arbol> funciones2 = new ArrayList<>();

        recolectarFunciones(a1, funciones1);
        recolectarFunciones(a2, funciones2);

        if (funciones1.isEmpty() || funciones2.isEmpty()) {
            System.out.println("No hay nodos función suficientes para cruzar.");
            return;
        }

        Arbol nodo1 = funciones1.get(random.nextInt(funciones1.size()));
        Arbol nodo2 = funciones2.get(random.nextInt(funciones2.size()));

        Arbol copiaNodo1 = new Arbol(nodo1); // copia profunda
        Arbol copiaNodo2 = new Arbol(nodo2); // copia profunda

        reemplazarNodo(a1, nodo1, copiaNodo2);
        reemplazarNodo(a2, nodo2, copiaNodo1);

        a1.actualizaTotalNodo();
        a2.actualizaTotalNodo();

       if(bloating == 1){
           bl.limitarTamaño(a1);
           bl.limitarTamaño(a2);
           a1.actualizaTotalNodo();
           a2.actualizaTotalNodo();
       }

    }

    private static void recolectarFunciones(Arbol a, List<Arbol> funciones) {
        if (!a.esHoja() && esFuncion(a.getValor())) {
            funciones.add(a);
        }
        for (Arbol hijo : a.getHijs()) {
            recolectarFunciones(hijo, funciones);
        }
    }

    private static boolean esFuncion(String valor) {
        return valor.equals("SICOMIDA") || valor.equals("PROG2") || valor.equals("PROG3");
    }

    //IMPORTANTE: esta funcion la he hecho pq ns si estaba cambiandome bien los nodos pq no entiendo bien las refes en java xd
    //asiq si m copiaba una refe a una variable y la modificaba no estaba segura d q en el arbol se cambiara tb, asi q he hecho esto
    //como metodo auxiliar pa asegurarme, aunq es mas lento claro. Si se cambia directamente se puede quitar.
    private static boolean reemplazarNodo(Arbol raiz, Arbol target, Arbol nuevo) {
        List<Arbol> hijos = raiz.getHijs();
        for (int i = 0; i < hijos.size(); i++) {
            if (hijos.get(i) == target) {
                hijos.set(i, nuevo);
                return true;
            } else {
                boolean reemplazado = reemplazarNodo(hijos.get(i), target, nuevo);
                if (reemplazado) return true;
            }
        }
        return false;
    }
}