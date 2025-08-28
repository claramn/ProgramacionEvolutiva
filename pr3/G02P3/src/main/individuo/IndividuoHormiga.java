package main.individuo;

import main.fitness.Fitness;
import main.mapa.Casa;
import main.mutaciones.Mutacion;
import main.utils.Arbol;
import main.utils.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IndividuoHormiga extends Individuo<Arbol> {

    private Random rand;
    // Profundidad máxima del árbol, puede definirse o pasarse por parámetro
    private int maxProfundidad;
    private int minProfundidad;
    private int metodoInicio;
    private List<Point> ruta;
    private String resultado;
    List<Arbol> nodosTerminales;
    List<Arbol> nodosNoTerminales;

    /**
     * Constructor del individuo con árbol.
     * @param casa Instancia de Casa (el entorno en el que se mueve la hormiga).
     * @param fitnessEvaluator Evaluador de fitness.
     * @param mutacionEvaluator Operador de mutación.
     * @param maxProfundidad Profundidad máxima permitida para el árbol.
     * @param metodoCompleto true para usar inicialización completa; false para inicialización creciente.
     */
    public IndividuoHormiga(Casa casa, Fitness<Arbol> fitnessEvaluator,
                                 Mutacion<Arbol> mutacionEvaluator, int maxProfundidad,
                                 int metodoCompleto, int minProfundidad) {
        this.casa = casa;
        this.fitnessEvaluator = fitnessEvaluator;
        this.mutacionEvaluator = mutacionEvaluator;
        this.rand = new Random();
        this.maxProfundidad = maxProfundidad;
        this.minProfundidad = minProfundidad;
        this.pasosVacios = 0;
        this.giros = 0;
        this.metodoInicio = metodoCompleto;
        ruta = new ArrayList<>();
        // Inicialización del árbol (cromosoma) utilizando un nodo raíz con una función aleatoria.
        // Nota: Los métodos getFunc y getTerm deben estar implementados para obtener
        //       símbolos de funciones y terminales según la práctica.
        String simboloRaiz = getFunc(rand.nextInt(3));
        this.cromosoma = new Arbol(simboloRaiz, maxProfundidad,minProfundidad);
        nodosTerminales = new ArrayList<>();
        nodosNoTerminales = new ArrayList<>();

        // Se inicializa el árbol según el método elegido:
        if (metodoInicio == 0) {
            this.cromosoma.inicializacionCompleta(0, maxProfundidad);
        } else if(metodoInicio == 1) {
            this.cromosoma.inicializacionCreciente(0, maxProfundidad);
        }
        else this.cromosoma = this.cromosoma.inicializacionRampedAndHalf( maxProfundidad,minProfundidad);
        cromosoma.actualizaTotalNodo();
        cromosoma.recolectarTerminales(nodosTerminales);
    }

    /**
     * Aplica el operador de mutación al árbol.
     */
    @Override
    public void mutacion() {
        // La mutación deberá trabajar sobre el árbol (por ejemplo, mutar un subárbol aleatorio).
        // Se delega al operador de mutación.
        this.mutacionEvaluator.mutar(this);
    }

    /**
     * Calcula el fitness evaluando la estrategia resultante de ejecutar el árbol.
     * Por ejemplo, podría simularse el recorrido de la hormiga en el tablero.
     */
    @Override
    public double getFitness() {
        return fitnessEvaluator.evaluar(this);
    }

    @Override
    public int getTamGenes() {
        return 0;
    }

    @Override
    public Arbol getCromosoma() {
        return this.cromosoma;
    }

    @Override
    public void setCromosoma(Arbol c1) {
        cromosoma = c1;
    }


    @Override
    public double[] getFenotipos() {
        return new double[0];
    }

    /**
     * Crea una copia profunda del individuo, clonando el árbol.
     */
    @Override
    public Individuo<Arbol> clone() {
        IndividuoHormiga nuevo = new IndividuoHormiga(this.casa, this.fitnessEvaluator,
                this.mutacionEvaluator, this.maxProfundidad, metodoInicio,this.minProfundidad);
        // Se asume que la clase Arbol dispone de un constructor de copia.
        nuevo.cromosoma = new Arbol(this.cromosoma);
        nuevo.maxProfundidad = this.maxProfundidad;
        nuevo.setRuta(this.ruta);
        nuevo.setResult(this.getResult());
        nuevo.resultado = this.resultado;
        nuevo.nodosTerminales = this.nodosTerminales;
        nuevo.nodosNoTerminales = this.nodosNoTerminales;
        nuevo.giros = this.giros;
        nuevo.pasosVacios = this.pasosVacios;
        nuevo.pasos = this.pasos;
        return nuevo;
    }

    /**
     * Retorna la ruta (camino) que sigue la hormiga al ejecutar la estrategia representada
     * por el árbol. Aquí deberás implementar la simulación de la ejecución del programa.
     */
    @Override
    public List<Point> getRuta() {
        // Aquí se simula la ejecución del árbol, generando la secuencia de movimientos
        // que realiza la hormiga. Por ejemplo:
        // 1. Iniciar en la posición (0,0) con dirección Este.
        // 2. Ejecutar cada operación del árbol (AVANZA, DERECHA, IZQUIERDA, etc.).
        // 3. Actualizar la posición y dirección, y almacenar los puntos recorridos.
        // Se devuelve la lista de puntos (ruta).
        return ruta;
    }
    @Override
    public void setRuta(List<Point> ruta) {
        this.ruta = ruta;
    }
    @Override
    public Casa getCasa(){
        return this.casa;
    }
    public String getResult() {
        return resultado;
    }
    @Override
    public void setResult(String result) {
        this.resultado = result;
    }

    @Override
    public List<Arbol> getNodosTerminales(){
        return nodosTerminales;
    }

    @Override
    public void actualizaNodosTerminales(){
        nodosTerminales.clear();
        nodosNoTerminales.clear();
        cromosoma.recolectarTerminales(nodosTerminales);
    }


    @Override
    public Pair<Integer,Integer> getPasos() {
        return new Pair<>(this.pasos,this.pasosVacios);
    }

    @Override
    public void setPasos(int pasos, int pasosVacios) {
        this.pasos = pasos;
        this.pasosVacios = pasosVacios;
    }
    @Override
    public String obtenerPrograma() {
        String res = obtenerProgramaRecursivo(this.cromosoma);
        return res;
    }

    private String obtenerProgramaRecursivo(Arbol arbol) {
        if (arbol == null) {
            return "";
        }

        String sb = "";
        sb +=arbol.getValor();

        if (!arbol.esHoja()) {
            sb +="(";
            List<Arbol> hijos = arbol.getHijs();
            for (int i = 0; i < hijos.size(); i++) {
               sb += obtenerProgramaRecursivo(hijos.get(i));
                if (i < hijos.size() - 1) {
                    sb += ",";
                }
            }
            sb +=(")");
        }

        return sb;
    }
}
