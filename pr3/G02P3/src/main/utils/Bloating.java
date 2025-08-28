package main.utils;

import main.individuo.Individuo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Bloating {
    private final int maxProfundidad;
    private final String[] TERMINALES = {"AVANZA", "DERECHA", "IZQUIERDA"};
    public Bloating(int maxProfundidad) {
        this.maxProfundidad = maxProfundidad;
    }

    /*
      penaliza el fitness d un individuo segun su tamaño
      covarianza entre tamaño y fitness
     */
    public double penalizarFitness(Individuo<Arbol> individuo, double mediaTamaño, double covarianza, double varianza) {
        double coef = covarianza / varianza;
        coef = Double.isNaN (coef) ? 0 :coef; // coeficiente de penalización
        int tamaño = individuo.getCromosoma().getTotalNodos();
        //esto es asi pq es maximizacion, si fuera minimizacion seria resta en vez d suma
        return individuo.getFitness() - coef * (  tamaño - mediaTamaño);
    }

    /**
     * Limita el tamaño o profundidad de un árbol durante su creación o modificación.
     *
     * @param arbol Árbol a verificar.
     */
    public void limitarTamaño(Arbol arbol) {
        if (arbol.getAltura() > maxProfundidad) {
            podarArbol(arbol, maxProfundidad,0);
        }
    }


    private void podarArbol(Arbol arbol, int maxProfundidad, int profActual) {
        if(profActual == maxProfundidad && !Arrays.asList(TERMINALES).contains(arbol.getValor())) {    //si alcanzamos la prof maxima, convertimos en hoja
            /*Random rand  = new Random();
            Arbol aux = new Arbol(TERMINALES[rand.nextInt(TERMINALES.length)],maxProfundidad);
            aux.setHijos(new ArrayList<>());
            arbol.cambiarNodo(arbol,hijo,aux);
            return;*/
            Random rand = new Random();
            arbol.setValor(TERMINALES[rand.nextInt(TERMINALES.length)]); // Asignar un terminal aleatorio
            arbol.setHijos(new ArrayList<>()); // Eliminar los hijos
        }
        //podamos recursivamente
        List<Arbol> hijos = arbol.getHijs();
        for(Arbol hijo : hijos){
            podarArbol(hijo,maxProfundidad,profActual+1);
        }
       arbol.actualizaTotalNodo(); //actualizamos la estructura del arbol dsps d podar
    }

    /*
     selecciona el mejor individuo en caso de empate en fitness, priorizando el tamaño más pequeño.
     */
    public int desempatarPorTamaño(int ind1, int ind2,double fitness1, double fitness2, int tam1,int tam2) {
        if (fitness1 == fitness2) {
            return tam1 < tam2 ? ind1 : ind2;
        }
        return fitness1 < fitness2 ? ind1 : ind2;
    }

}