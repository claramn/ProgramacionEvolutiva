package main.fitness;

import main.individuo.Individuo;
import main.mapa.Casa;
import main.utils.*;
import java.awt.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class FitnessBasico extends Fitness<Arbol> {
    private final  int MAX_PASOS;
    private List<Point> ruta;
    Terminales terminales;
    Casa casa;
    private int comidaRecogida;
    String resultado;

    public FitnessBasico(int MAX_PASOS) {
        this.MAX_PASOS = MAX_PASOS;
    }

    @Override
    public double evaluar(Individuo<Arbol> ind) {
        terminales = new Terminales();
        casa = ind.getCasa();
        ruta = new ArrayList<>(MAX_PASOS);
        ruta.add(casa.getBase());
        Set<Point> comida = new HashSet<>(casa.getFood());
        int totalComida = comida.size();
        Estado estado = new Estado(0,0, Direccion.ESTE);
        comidaRecogida = 0;
        Arbol crom = ind.getCromosoma();
        resultado = "";
        int pasos = 0;
        int[] pasosTotales = new int[]{0};
        int[] pasosVacios = new int[]{0};
        List<Point> comidasRecogida = new ArrayList<>();
        Direccion direccionAnterior = estado.direccion;

        while(pasosTotales[0] < MAX_PASOS && comidaRecogida != 89) {
            ejecutarNodo(crom, estado, direccionAnterior, comida, ruta, pasosTotales,comidasRecogida,pasosVacios);
            if(comidaRecogida == 89) break;
        }

        ind.setRuta(ruta);
        ind.setPasos(pasosTotales[0],pasosVacios[0]);
        return comidaRecogida;
    }

    //TODO idea, hacer que el fitness valore que el camino que sigas tenga el max d comida por distancia recorrida, y que los caminos que recorren muchas casillas en blanco sean penalizados
    private boolean ejecutarNodo(Arbol nodo, Estado estado, Direccion direccionAnterior,
                                 Set<Point> comida, List<Point> ruta, int[] pasosTotales, List<Point> comidasRecogida,int[] pasosVacios) {
        // Condición de parada global
        if (nodo == null || pasosTotales[0] >= MAX_PASOS || comidaRecogida >= 89) {
            return false;
        }

        switch (nodo.getValor()) {
            case "AVANZA":
                if (pasosTotales[0] >= MAX_PASOS) return false;
                Terminales.avanza(estado);
                ruta.add(new Point(estado.x, estado.y));
                if(comida.contains(new Point(estado.x, estado.y))) {
                    comidaRecogida++;
                    comida.remove(new Point(estado.x, estado.y));
                    comidasRecogida.add(new Point(estado.x, estado.y));
                }
                else pasosVacios[0]++;
              //  resultado += "(AVANZA) ";
                pasosTotales[0]++;
                break;

            case "DERECHA":
                if (pasosTotales[0] >= MAX_PASOS) return false;
                Terminales.derecha(estado);
                pasosTotales[0]++;
                break;

            case "IZQUIERDA":
                if (pasosTotales[0] >= MAX_PASOS) return false;
                Terminales.izquierda(estado);
                pasosTotales[0]++;
                break;

            case "PROG2":
               // resultado += "(PROG 2 ";
                for(Arbol hijo: nodo.getHijs()) {
                    if (!ejecutarNodo(hijo, estado, direccionAnterior, comida, ruta, pasosTotales,comidasRecogida,pasosVacios)) {
                      //  resultado += ")";
                        return false;
                    }
                }
              //  resultado += ")";
                break;

            case "PROG3":
             //   resultado += "(PROG 3 ";
                for(Arbol hijo: nodo.getHijs()) {
                    if (!ejecutarNodo(hijo, estado, direccionAnterior, comida, ruta, pasosTotales,comidasRecogida,pasosVacios)) {
                  //      resultado += ")";
                        return false;
                    }
                }
             //   resultado += ")";
                break;

            case "SICOMIDA":
             //   resultado += "(SICOMIDA ";
                if (hayComida(estado, comida)) {
                    if(nodo.getHijs().size() > 0 &&
                            !ejecutarNodo(nodo.getHijs().get(0), estado, direccionAnterior, comida, ruta, pasosTotales,comidasRecogida,pasosVacios)) {

                   //     resultado += ")";
                        return false;
                    }
                } else {
                    if (nodo.getHijs().size() > 1 &&
                            !ejecutarNodo(nodo.getHijs().get(1), estado, direccionAnterior, comida, ruta, pasosTotales,comidasRecogida,pasosVacios)) {

                     //   resultado += ")";
                        return false;
                    }
                }
                break;
        }
        return true;
    }



    public boolean hayComida(Estado estado,Set<Point> comida){
        Point delante = posDelante(estado);
        return comida.contains(delante);
    }

    private Point posDelante(Estado estado){
        switch (estado.direccion){
            case NORTE: return new Point(estado.x, estado.y-1);
            case SUR: return new Point(estado.x, estado.y+1);
            case ESTE: return new Point(estado.x+1, estado.y);
            case OESTE: return new Point(estado.x-1, estado.y);
            default: return new Point(estado.x,estado.y);
        }
    }


}

