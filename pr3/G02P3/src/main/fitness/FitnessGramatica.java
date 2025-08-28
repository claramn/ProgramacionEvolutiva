package main.fitness;

import main.individuo.Individuo;
import main.individuo.IndividuoGramatica;
import main.mapa.Casa;
import main.utils.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class FitnessGramatica extends Fitness<String> {
    private  final int MAX_PASOS;
    private List<Point> ruta;
    private Terminales terminales;
    private Casa casa;
    private int comidaRecogida;
    private String resultado;


    public FitnessGramatica(int pasos) {
        MAX_PASOS = pasos;
    }
    @Override
    public double evaluar(Individuo<String> ind ) {
        terminales = new Terminales();
        casa = ind.getCasa();
        ruta = new ArrayList<>(MAX_PASOS);
        ruta.add(casa.getBase());
        Set<Point> comida = new HashSet<>(casa.getFood());
        int totalComida = comida.size();
        Estado estado = new Estado(0,0, Direccion.ESTE);
        comidaRecogida = 0;
        String crom = ind.obtenerPrograma();
        String aux = new String(crom);
        if (crom=="ERROR"){
            ind.setResult("");
            return 0;
        }
        ind.setResult(crom);
        List<String> tokens = tokenizar(crom);
        List<String> tokens2 = new ArrayList<>(tokens);
        resultado = "";

        int[] pasosTotales = new int[]{0};
        int[] pasosVacios = new int[]{0};
        List<Point> comidasRecogida = new ArrayList<>();
        Direccion direccionAnterior = estado.direccion;
        while(pasosTotales[0] < MAX_PASOS && comidaRecogida != 89) {
            ejecutarCadena(aux,estado,comida,ruta,pasosTotales,comidasRecogida,pasosVacios);
            aux = new String(crom);
        }
        ind.setRuta(ruta);
        ind.setPasos(pasosTotales[0],pasosVacios[0]);
        return comidaRecogida;
    }


    private boolean ejecutarCadena(String expr, Estado estado, Set<Point> comida,
                                   List<Point> ruta, int[] pasosTotales, List<Point> comidasRecogida,int[] pasosVacios) {
        if (pasosTotales[0] >= MAX_PASOS || comidaRecogida >= 89) return false;

        expr = expr.trim();
        if (expr.startsWith("SICOMIDA(")) {
            String[] args = splitArgs(expr.substring(9, expr.length() - 1));
            if (hayComida(estado, comida)) {
                return ejecutarCadena(args[0], estado, comida, ruta, pasosTotales, comidasRecogida,pasosVacios);
            } else {
                return ejecutarCadena(args[1], estado, comida, ruta, pasosTotales, comidasRecogida,pasosVacios);
            }
        } else if (expr.startsWith("PROG2(") || expr.startsWith("PROG3(")) {
            int n = expr.startsWith("PROG2(") ? 2 : 3;
            String[] args = splitArgs(expr.substring(expr.indexOf('(') + 1, expr.length() - 1));
            for (int i = 0; i < n; i++) {
                if (!ejecutarCadena(args[i], estado, comida, ruta, pasosTotales, comidasRecogida,pasosVacios))
                    return false;
            }
            return true;
        } else if (expr.equals("AVANZA")) {
            Terminales.avanza(estado);
            ruta.add(new Point(estado.x, estado.y));
            if (comida.contains(new Point(estado.x, estado.y))) {
                comidaRecogida++;
                comida.remove(new Point(estado.x, estado.y));
                comidasRecogida.add(new Point(estado.x, estado.y));
            } else pasosVacios[0]++;
            pasosTotales[0]++;
            return true;
        } else if (expr.equals("DERECHA")) {
            Terminales.derecha(estado);
            pasosTotales[0]++;
            return true;
        } else if (expr.equals("IZQUIERDA")) {
            Terminales.izquierda(estado);
            pasosTotales[0]++;
            return true;
        }

        return false;
    }

    // separa argumentos de una función como PROG3(...)
    private String[] splitArgs(String input) {
        List<String> result = new ArrayList<>();
        int parens = 0, last = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '(') parens++;
            else if (c == ')') parens--;
            else if (c == ',' && parens == 0) {
                result.add(input.substring(last, i).trim());
                last = i + 1;
            }
        }
        result.add(input.substring(last).trim());
        return result.toArray(new String[0]);
    }


   

    private Deque<String> parsePrograma(String programa) {
        Deque<String> pila = new ArrayDeque<>();
        String cleaned = programa.replaceAll("[(),]", " ");
        //String cleaned = programa.replaceAll("[,]", " ");
        List<String> tokens = Arrays.asList(cleaned.trim().split("\\s+"));
        Collections.reverse(tokens);
        pila.addAll(tokens);
        return pila;
    }

    private Point posDelante(Estado estado) {
        switch (estado.direccion) {
            case NORTE:
                return new Point(estado.x, estado.y - 1);
            case SUR:
                return new Point(estado.x, estado.y + 1);
            case ESTE:
                return new Point(estado.x + 1, estado.y);
            case OESTE:
                return new Point(estado.x - 1, estado.y);
            default:
                return new Point(estado.x, estado.y);
        }
    }

    public List<String> tokenizar(String gramatica) {
        return Arrays.stream(
                        gramatica.replace("(", " ( ")
                                .replace(")", " ) ")
                                .replace(",", " , ")
                                .split("\\s+")
                )
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    public boolean hayComida(Estado estado,Set<Point> comida){
        Point delante = posDelante(estado);
        return comida.contains(delante);
    }


}
