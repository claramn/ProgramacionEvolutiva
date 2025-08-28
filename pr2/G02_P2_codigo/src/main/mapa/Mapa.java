package main.mapa;

import main.utils.Pair;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Mapa {

    protected static Map<Pair<Integer, Integer>, Double> cacheDistancias;
    protected int numHabitaciones;
    protected int tamanyo; // Tamaño de la casa (cuadrícula)
    protected Point base;
    // Las habitaciones se identifican por su id (1..20) y se asocian a una coordenada.
    protected Map<Integer, Point> habitaciones;
    // La matriz 'mapa' indica si una celda es transitable (true) o contiene un obstáculo (false)
    protected boolean[][] mapa;
    // Obstáculos: conjunto de puntos que representan celdas bloqueadas
    protected Set<Point> obstaculos;
    protected Map<Pair<Integer, Integer>, List<Point>> cacheCaminos;

    public static Double getDistanciaEntreHabitaciones(int hab1, int hab2) {
        return cacheDistancias.get(new Pair<>(Math.min(hab1, hab2), Math.max(hab1, hab2)));
    }

    public int getNumHabitaciones() {
        return numHabitaciones;
    }

    public Point getCoordenada(int habitacion) {
        return habitaciones.get(habitacion);
    }

    public Point getBase() {
        return base;
    }

    public Set<Point> getObstaculos() {
        return obstaculos;
    }

    public Map<Integer, Point> getHabitaciones() {
        return habitaciones;
    }

    public void precalcularCaminos(int fit) {
    }

}
