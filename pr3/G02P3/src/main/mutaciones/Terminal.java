package main.mutaciones;
import main.utils.Arbol;
import main.individuo.Individuo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

//cambia dos terminales al azar
public class Terminal extends Mutacion<Arbol>{
    private int poblacion;


    @Override
    public void mutar(Individuo ind) {
        Random r = new Random();
        List<Arbol>terminales = new ArrayList<>();
        Arbol crom = (Arbol) ind.getCromosoma();
       ((Arbol) ind.getCromosoma()).recolectarTerminales(terminales);
        if(terminales.isEmpty()) return;
        Arbol terminal = terminales.get(r.nextInt(terminales.size()));
        Arbol terminal2 = terminales.get(r.nextInt(terminales.size()));
        //ESTO NS PQ A VECES CREA UN BUCLE INFINITO asi q comentao
        /*while(terminal.getValor() == terminal2.getValor()){
            terminal2 = terminales.get(r.nextInt(terminales.size()));
        }*/
        Arbol temp = new Arbol(terminal);
        terminal.setNodo(terminal2);
        terminal2.setNodo(temp);
    }

}
