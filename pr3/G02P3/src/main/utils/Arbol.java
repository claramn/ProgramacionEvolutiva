package main.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static main.individuo.Individuo.getFunc;
import static main.individuo.Individuo.getTerm;

public class Arbol {
    private int minProfundidad;
    private String valor;
    private List<Arbol> hijos;
    private int totalNodos;
    private int maxProfundidad;
    private int altura;
    private static final String[] TERMINALES = {"AVANZA", "DERECHA", "IZQUIERDA"};
    private static final String[] FUNCIONES = {"SICOMIDA", "PROG2", "PROG3"};

    // Objeto Random para toda la clase
    private static final Random random = new Random();


    public Arbol(String v, int maxProfundidad, int minProfundidad) {
        this.valor = v;
        hijos = new ArrayList<>();
        this.totalNodos = 1;
        this.maxProfundidad = maxProfundidad;
        this.altura = 0;
        this.minProfundidad = minProfundidad;
    }

    // Constructor de copia (clon profundo)
    public Arbol(Arbol a) {
        this.valor = a.getValor();
        this.totalNodos = a.totalNodos;
        this.maxProfundidad = a.maxProfundidad;
        this.altura = a.altura;
        this.hijos = new ArrayList<>();
        for (Arbol hijo : a.getHijs()) {
            this.hijos.add(new Arbol(hijo));  // copia profunda de cada hijo
        }
    }

    /**
     * Devuelve el subárbol cuyo índice (en recorrido inorden) es 'index'.
     */
    public Arbol at(int index) {
        if(index < 0 || index >= totalNodos) throw new IndexOutOfBoundsException("indice fuera d rango");
        if(index == 0) return this;
        int indAct = 1;
        for(Arbol hijo: hijos){
            int tamHijo = hijo.getTotalNodos();
            if(index < indAct + tamHijo){
                return hijo.at(index-indAct);
            }
            indAct += tamHijo;
        }
        throw new IndexOutOfBoundsException("Índice no encontrado");
    }



    /**
     * Convierte el árbol a un ArrayList de Strings representando la fórmula en inorden.
     */
    public ArrayList<String> toArray(){
        ArrayList<String> formula = new ArrayList<>();
        rellenaArrayList(formula);
        return formula;
    }

    private void rellenaArrayList(ArrayList<String> formula) {
        if (!this.esHoja()) {
            formula.add("(");
            formula.add(this.valor);
            for(Arbol hijo : hijos) {
                hijo.rellenaArrayList(formula);
            }
            formula.add(")");
        } else {
            formula.add(this.valor);
        }
    }

    public String getValor() {
        return this.valor;
    }

    public int getTotalNodos() {
        // this.actualizaTotalNodo();
         return this.totalNodos;
    }


    public List<Arbol> getHijs() {
        return this.hijos;
    }

    public int getMaxProfundidad() {
        return this.maxProfundidad;
    }

    public boolean esHoja() {
        return this.hijos.isEmpty();
    }

    public void setValor(String v) {
        this.valor = v;
    }



    public void addHijo(Arbol hijo){
        this.hijos.add(hijo);
    }

    public void setHijos(List<Arbol> nuevosHijos){
        this.hijos = new ArrayList<>(nuevosHijos);
    }


    /**
     * Actualiza el número total de nodos en el árbol.
     *  TODO revisar esto pq ns si esta bn asi lo d los no terminales y terminales
     */
    public void actualizaTotalNodo() {
       Pair<Integer,Integer> sol =actualizaTotalNodoYProfundidad(0);
       this.totalNodos = sol.getFirst();
       this.altura = sol.getSecond();
    }
    //devuelve num nodos y profundidad
    private Pair<Integer,Integer> actualizaTotalNodoYProfundidad(int profundidadActual) {
        //this.altura = profundidadActual; // Actualiza la profundidad del nodo actual
        if (this.esHoja()) {
            this.altura = 0;
            this.totalNodos = 1;
            return new Pair<>(1,0);        //TODO esto quizaas deberia ser 1 pero idk
        } else {
            int total = 1;
            int maximaProfundidadHijos = 0;

            for (Arbol hijo : hijos) {
                Pair<Integer,Integer> profHijo = hijo.actualizaTotalNodoYProfundidad(profundidadActual + 1);
                total += profHijo.getFirst();
                maximaProfundidadHijos = Math.max(maximaProfundidadHijos, profHijo.getSecond());
            }
            int alturaActual = maximaProfundidadHijos+1;
            this.altura = alturaActual;
            this.totalNodos = total;
            return new Pair<>(total,alturaActual);
        }
    }


    /**
     * Copia el contenido del nodo a partir del nodo dado. TODO esto ns si es realmente util o sobra
     */
    public void setNodo(Arbol a) {
        this.valor = a.getValor();
        this.totalNodos = a.totalNodos;
        this.maxProfundidad = a.maxProfundidad;
        this.altura = a.altura;
        this.hijos = new ArrayList<>();
        setHijos(a.hijos);
    }
    /**
     * Inicialización Completa: Se usan funciones hasta la máxima profundidad y, en la última capa, únicamente terminales.
    TODO esto hay q revisarlo y entenderlo cus i didnt do this
     */

    /**
     * Inicialización completa del árbol
     * @param profundidadActual Profundidad actual en la recursión
     * @param profundidadMaxima Profundidad máxima permitida para el árbol
     */
    public void inicializacionCompleta(int profundidadActual, int profundidadMaxima) {
        Random rand = new Random();
        if (profundidadActual < profundidadMaxima) {
            // Seleccionar una función aleatoria
            this.valor = FUNCIONES[rand.nextInt(FUNCIONES.length)];

            // Crear los hijos según la función seleccionada
            int numHijos = getNumHijos(this.valor);
            this.hijos = new ArrayList<>(numHijos);

            for (int i = 0; i < numHijos; i++) {
                Arbol hijo = new Arbol("",profundidadMaxima,this.minProfundidad);
                hijo.inicializacionCompleta(profundidadActual + 1, profundidadMaxima);
                this.hijos.add(hijo);
            }
        } else {
            // En profundidad máxima, usar terminales
            this.valor = TERMINALES[rand.nextInt(TERMINALES.length)];
            this.hijos = Collections.emptyList(); // Terminales no tienen hijos
        }
    }

    /**
     * Obtiene el número de hijos requeridos por cada función
     */
    private int getNumHijos(String funcion) {
        switch (funcion) {
            case "SICOMIDA":
            case "PROG2":
                return 2;
            case "PROG3":
                return 3;
            default:
                return 0; // Para terminales
        }
    }

    /**
     * Inicialización Creciente: Se permite colocar terminales antes de llegar a la máxima profundidad de forma aleatoria.
     */
    public void inicializacionCreciente(int profundidadActual, int profundidadMaxima) {
        if (profundidadActual < profundidadMaxima) {
            // Antes de la profundidad máxima, podemos elegir entre función o terminal
            boolean usarTerminal = profundidadActual >= minProfundidad && random.nextBoolean();

            if (usarTerminal) {
                // Usar terminal
                this.valor = TERMINALES[random.nextInt(TERMINALES.length)];
                this.hijos.clear(); // Aseguramos que no tenga hijos
            } else {
                // Usar función
                this.valor = FUNCIONES[random.nextInt(FUNCIONES.length)];

                // Crear hijos según la función seleccionada
                int numHijos = getNumHijos(this.valor);
                this.hijos = new ArrayList<>(numHijos);

                for (int i = 0; i < numHijos; i++) {
                    Arbol hijo = new Arbol("", profundidadMaxima, this.minProfundidad);
                    hijo.inicializacionCreciente(profundidadActual + 1, profundidadMaxima);
                    this.hijos.add(hijo);
                }
            }
        } else {
            // En profundidad máxima, usar solo terminales
            this.valor = TERMINALES[random.nextInt(TERMINALES.length)];
            this.hijos = new ArrayList<>(); // Terminales no tienen hijos
        }
    }

    /**
     * Inicialización Ramped and Half
     */
    public  Arbol inicializacionRampedAndHalf(int maxProfundidad, int minProfundidad) {
        // Elegir una profundidad entre minProfundidad y maxProfundidad
        int profundidadElegida = random.nextInt(maxProfundidad - minProfundidad + 1) + minProfundidad;

        // Elegir entre inicialización completa o creciente
        boolean usarCompleta = random.nextBoolean();

        // Crear el árbol raíz con una función
        String funcionRaiz = FUNCIONES[random.nextInt(FUNCIONES.length)];
        Arbol arbol = new Arbol(funcionRaiz, maxProfundidad, minProfundidad);

        // Inicializar según el método elegido
        if (usarCompleta) {
            arbol.inicializacionCompleta(0, profundidadElegida);
        } else {
            arbol.inicializacionCreciente(0, profundidadElegida);
        }

        return arbol;
    }

    public int getAltura() {
        return this.altura;
    }

    public int getMinProfundidad() {
        return this.minProfundidad;
    }


    public void recolectarTerminales(List<Arbol> c) {
        if (this.esHoja()) {
            c.add(this);
        } else {
            for(Arbol hijo : hijos) {
                hijo.recolectarTerminales(c);
            }
        }
    }


    // Método auxiliar para recolectar todos los nodos terminales
    private void recogerTerminales(List<Arbol> terminales) {
        if(this.esHoja()) terminales.add(this);
        else {
            for (Arbol hijo : hijos) {
                hijo.recogerTerminales(terminales);
            }
        }
    }

    public void cambiarNodo(Arbol c1, Arbol c2, Arbol c3) {
        if(c1 == c2) {
            c1 = new Arbol(c3);
            return;
        }
        else{
            for(Arbol hijo : hijos) {
                hijo.cambiarNodo(hijo, c2, c3);
            }
        }
    }

    @Override
    public String toString() {
        return buildVisual(this, "", true);
    }

    private String buildVisual(Arbol node, String prefix, boolean isLast) {
        if (node == null) return prefix + (isLast ? "└── " : "├── ") + "[null]\n";

        StringBuilder sb = new StringBuilder();
        sb.append(prefix)
                .append(isLast ? "└── " : "├── ")
                .append(node.getValor())
                .append("\n");

        List<Arbol> hijos = node.getHijs();
        for (int i = 0; i < hijos.size(); i++) {
            boolean last = (i == hijos.size() - 1);
            Arbol hijo = hijos.get(i);
            sb.append(buildVisual(hijo, prefix + (isLast ? "    " : "│   "), last));
        }
        return sb.toString();
    }
}
