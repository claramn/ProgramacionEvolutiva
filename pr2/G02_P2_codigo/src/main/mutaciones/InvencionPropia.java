package main.mutaciones;

import main.individuo.Individuo;

import java.util.Random;

/*
nuestro robot ha limpiado unas manchas de vino en la cocina, y esta un poco afectado por ello, por lo que cuando intenta
recorrer la casa, a veces da la vuelta completa y empieza de nuevo, a veces intercambia habitaciones al azar pq le parecen cercanas,
y a veces elige la ruta mas corta entre dos habitaciones pero se olvida y las pone despues. Con ciertas probabilidades, el robot
realiza cada una de estas acciones, realizando asi la mutacion final
 */
public class InvencionPropia extends Mutacion<Integer> {
    private final static double p1 = 0.2;   //cambia un fragmento del cromosoma al reves
    private final static double p2 = 0.1;   //mueve una habitacion al inicio o final
    private final static double p3 = 0.3;   //encuetra dos habitaciones cercanas y las intercambia
    private final static double p4 = 0.15;   //si una habitacion esta muy lejos, la acerca moviendo otras aleatoriamente
    private final static double p5 = 0.05;   //si el fitness no mejora, deshace todo

    @Override
    public void mutar(Individuo<Integer> ind) {
        Random rand = new Random();
        double fitnessAntes = ind.getFitness();
        Integer[] crom = ind.getCromosoma();
        Integer[] cromOriginal = ind.getCromosoma();
        if (rand.nextDouble() < p1) {
            int a = rand.nextInt(crom.length);
            int b = rand.nextInt(crom.length);
            while (a == b) b = rand.nextInt(crom.length);
            if (a > b) {
                int temp = a;
                a = b;
                b = temp;
            }
            while (a < b) {
                int temp = crom[a];
                crom[a] = crom[b];
                crom[b] = temp;
                a++;
                b--;
            }
        }
        if (rand.nextDouble() < p2) {
            int id = rand.nextInt(crom.length);
            int habitacion = crom[id];
            for (int i = id; i < crom.length - 1; i++) {
                crom[i] = crom[i + 1];
            }
            boolean alInicio = rand.nextBoolean();  //elegir si insertamos al inicio o al final
            if (alInicio) {     //desplazar a la derecha si estamos en la posicion 0
                for (int i = crom.length - 1; i > 0; i--) {
                    crom[i] = crom[i - 1];
                }
                crom[0] = habitacion; // insertar en la primera posicion
            } else {
                crom[crom.length - 1] = habitacion; // insertar en la ultima posición
            }
        }
        if (rand.nextDouble() < p3) {
            int h1 = rand.nextInt(crom.length);
            int h2 = (h1 + 1) % crom.length;
            int temp = crom[h1];
            crom[h1] = crom[h2];
            crom[h2] = temp;
        }
        if (rand.nextDouble() < p4) {
            int h1 = rand.nextInt(crom.length);
            int h2 = rand.nextInt(crom.length);
            while (h1 == h2) {
                h2 = rand.nextInt(crom.length);
            }
            if (h1 > h2) {
                int temp = h1;
                h1 = h2;
                h2 = temp;
            }
            int habitacion = crom[h1];
            // eliminar la habitacion
            for (int i = h1; i < crom.length - 1; i++) {
                crom[i] = crom[i + 1];
            }
            // insertar habitacion en h2
            for (int i = crom.length - 1; i > h2; i--) {
                crom[i] = crom[i - 1];
            }
            crom[h2] = habitacion; //  colocar la habitación en su nueva posición
        }
        ind.setCromosoma(crom);
        if (rand.nextDouble() < p5) {
            double fitnessActual = ind.getFitness();
            if (fitnessActual > fitnessAntes) {     //si el fitness es peor, no cambiamos nada
                ind.setCromosoma(cromOriginal);
            }
        }
    }
}