package main.mutaciones;

import main.individuo.Individuo;
import main.utils.Arbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Funcional extends Mutacion<Arbol>{

    //TODO revisar si esto realmente va bn pq yo creo q si pero no me he parado a investigar a fondo
    //se parece mucho a cuando tienes q coger una funcion en cruce asi q es practicamente lo mismo
    @Override
    public void mutar(Individuo<Arbol> ind) {
        Arbol crom = ind.getCromosoma();
        List<Arbol> funciones = new ArrayList<>();
        Random rand = new Random();
        recolectarFunciones(crom, funciones);

        if (funciones.isEmpty() ) {
            System.out.println("no hay nodos función suficientes para cruzar");
            return;
        }

        Arbol nodo1 = funciones.get(rand.nextInt(funciones.size()));
        Arbol temp = nodo1;
        if(nodo1.getValor()== "SICOMIDA") nodo1.setValor("PROG2");
        else if(nodo1.getValor() == "PROG2") nodo1.setValor("SICOMIDA");
        reemplazarNodo(crom,temp,nodo1);
    }

    private static void recolectarFunciones(Arbol a, List<Arbol> funciones) {
        if (!a.esHoja() && esFuncion(a.getValor())) {
            funciones.add(a);
        }
        for (Arbol hijo : a.getHijs()) {
            recolectarFunciones(hijo, funciones);
        }
    }

    //no incluimos PROG3 pq no podemos cambiarla por ninguna otra funcion ya q no hay otra q tenga 3 hijos
    private static boolean esFuncion(String valor) {
        return valor.equals("SICOMIDA") || valor.equals("PROG2");
    }

    //lo mismo q en cruce
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
