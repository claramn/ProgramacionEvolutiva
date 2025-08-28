package main.mutaciones;

import main.individuo.Individuo;
import main.utils.Arbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MutacionPermutacion extends Mutacion<Arbol> {
    @Override
    public void mutar(Individuo<Arbol> ind) {
        Random r = new Random();
        Arbol a = ind.getCromosoma();

        List<Arbol> funcionesConHijos = new ArrayList<>();
        recolectarFuncionesConMultiplesHijos(a, funcionesConHijos);

        if(funcionesConHijos.isEmpty()) return;
        Arbol nodo = funcionesConHijos.get(r.nextInt(funcionesConHijos.size()));
        List<Arbol> hijos = nodo.getHijs();
        Collections.shuffle(hijos);
        nodo.setHijos(hijos);
        a.actualizaTotalNodo();
    }

    private void recolectarFuncionesConMultiplesHijos(Arbol arbol, List<Arbol> funcionesConHijos) {
        if (!arbol.esHoja() && arbol.getHijs().size() >= 2) {
            funcionesConHijos.add(arbol);
        }

        if (!arbol.esHoja()) {
            for (Arbol hijo : arbol.getHijs()) {
                recolectarFuncionesConMultiplesHijos(hijo, funcionesConHijos);
            }
        }
    }

}
