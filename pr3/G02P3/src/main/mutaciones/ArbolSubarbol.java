package main.mutaciones;

import main.individuo.Individuo;
import main.utils.Arbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//pilla un nodo random y quita el subarbol que tiene y lo reemplaza por uno random
public class ArbolSubarbol extends Mutacion<Arbol> {

    @Override
    public void mutar(Individuo<Arbol> ind) {
        Arbol crom = ind.getCromosoma();
        Random r = new Random();
        List<Arbol> nodos = new ArrayList<>();
        recolectarNoTerminales(crom, nodos);
        Arbol nodoSeleccionado = nodos.get(r.nextInt(nodos.size()));
        Arbol subarbol = new Arbol("", crom.getMaxProfundidad(),crom.getMinProfundidad());
        subarbol.inicializacionCompleta(nodoSeleccionado.getAltura(),crom.getMaxProfundidad()); //TODO ns que alturas exactamente se tienen q poner, intuyo q es asi pero not sure
        crom.cambiarNodo(crom,nodoSeleccionado,subarbol);
        crom.actualizaTotalNodo();
    }

    private static void recolectarNoTerminales(Arbol a, List<Arbol> funciones) {
        if (!a.esHoja() && esFuncion(a.getValor())) {
            funciones.add(a);
        }
        for (Arbol hijo : a.getHijs()) {
            recolectarNoTerminales(hijo, funciones);
        }
    }

    private static boolean esFuncion(String valor) {
        return valor.equals("SICOMIDA") || valor.equals("PROG2") ||valor.equals("PROG3");
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


    //esta funcion la hago pq no se si solo por referencia se modifican las cosas y asi m aseguro pero maybe no hace falta
    private boolean reemplazarSubarbol(Arbol actual,Arbol original, Arbol nuevo) {
        if(actual == original){
            actual.setValor(nuevo.getValor());
            actual.setHijos(nuevo.getHijs());
            return true;
        }
        for(int i=0; i<actual.getHijs().size(); i++) {
            if(actual.getHijs().get(i) == original ){
                actual.getHijs().set(i, nuevo);
                return true;
            }
            if(reemplazarNodo(actual.getHijs().get(i), original, nuevo)) return true;
        }
        return false;
    }


}