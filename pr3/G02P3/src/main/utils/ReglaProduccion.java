package main.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReglaProduccion {
    //clase para implementar gramaticas
    private final String noTerminal;    //lado izq. pej <expr>
    private final List<List<String>> producciones; //lados derecuos posibles

    public ReglaProduccion(String noTerminal, List<String> produccionesString) {
        this.noTerminal = noTerminal;
        producciones = new ArrayList<>();

        //parsea cada produccion en componentes
        for(String produccion : produccionesString) {
            this.producciones.add(parsearProduccion(produccion));
        }
    }

    /*
    divide una produccion en simbolos terminales y no terminales
    ej "PROG2 <expr> <expr> "  -> ["PROG2", "<expr>", "<expr>"]
     */
    private List<String> parsearProduccion(String produccion) {
        List<String> simbolos = new ArrayList<>();
        String[] partes = produccion.split(" ");    //TODO esto ns si es \\s+

        for(String parte : partes) {
            if(!parte.trim().isEmpty()){
                simbolos.add(parte);
            }
        }
        return simbolos;
    }

    public String getNoTerminal() {
        return noTerminal;
    }

    public List<List<String>> getProducciones() {
        return producciones;
    }

    public List<String> getProduccionRandom(){
        Random r = new Random();
        return producciones.get(r.nextInt());
    }
}
