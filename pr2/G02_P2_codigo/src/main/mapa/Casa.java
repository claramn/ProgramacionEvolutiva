package main.mapa;

import main.utils.Nodo;
import main.utils.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;

public class Casa extends Mapa {
    private static final int NUM_HABITACIONES = 20;
    private static final int TAMANO = 15; // Cuadrícula 15x15
    private static final Point BASE = new Point(7, 7);

    private static final double PENALIZACION_OBSTACULOS = 0.5; // Penalización por obstáculos
    private static final double RADIO_PENALIZACION_GIROS = 0.2; // Radio de penalización
    private static final double PENALIZACION_GIROS = 0.5; // Penalización por giros
    private static final double VELOCIDAD = 1.0;
    private static final double PENALIZACION_VELOCIDAD = 0.5; // Penalización por giros

    private Integer lastFit; // Último valor de fit para precalcular caminos


    public Casa() {
        this.numHabitaciones = NUM_HABITACIONES;
        this.tamanyo = TAMANO;
        this.base = BASE;
        this.habitaciones = new HashMap<>();
        this.mapa = new boolean[TAMANO][TAMANO];
        // Inicializamos el conjunto de obstáculos
        this.obstaculos = new HashSet<>();

        inicializarMapa();
        inicializarHabitaciones();

        cacheCaminos = new HashMap<>();
        cacheDistancias = new HashMap<>();
    }

    private void inicializarMapa() {
        // Inicializar toda la matriz como transitable.
        for (int i = 0; i < TAMANO; i++) {
            Arrays.fill(mapa[i], true);
        }
        // Definir obstáculos directamente como Points.
        // Por ejemplo, la pared horizontal: fila 5, columnas 5 a 9.
        obstaculos.add(new Point(5, 5));
        obstaculos.add(new Point(5, 6));
        obstaculos.add(new Point(5, 7));
        obstaculos.add(new Point(5, 8));
        obstaculos.add(new Point(5, 9));
        // Pared vertical: columna 10, filas 8 a 12.
        obstaculos.add(new Point(8, 10));
        obstaculos.add(new Point(9, 10));
        obstaculos.add(new Point(10, 10));
        obstaculos.add(new Point(11, 10));
        obstaculos.add(new Point(12, 10));
        // Obstáculos individuales.
        obstaculos.add(new Point(10, 3));
        obstaculos.add(new Point(11, 4));
        // Pared vertical adicional: columna 6, filas 10 a 13.
        obstaculos.add(new Point(10, 6));
        obstaculos.add(new Point(11, 6));
        obstaculos.add(new Point(12, 6));
        obstaculos.add(new Point(13, 6));
        // Pared horizontal adicional: fila 8, columnas 1 a 4.
        obstaculos.add(new Point(8, 1));
        obstaculos.add(new Point(8, 2));
        obstaculos.add(new Point(8, 3));
        obstaculos.add(new Point(8, 4));
        // Obstáculos en esquina superior derecha.
        obstaculos.add(new Point(0, 13));
        obstaculos.add(new Point(1, 13));
        // Pared horizontal adicional: fila 3, columnas 8 a 11.
        obstaculos.add(new Point(3, 8));
        obstaculos.add(new Point(3, 9));
        obstaculos.add(new Point(3, 10));
        obstaculos.add(new Point(3, 11));

        // Marcar en la matriz las celdas con obstáculos.
        for (Point p : obstaculos) {
            mapa[p.x][p.y] = false;
        }
    }

    private void inicializarHabitaciones() {
        // Asignar las 20 habitaciones con sus coordenadas fijas.
        habitaciones.put(1, new Point(2, 2));
        habitaciones.put(2, new Point(2, 12));
        habitaciones.put(3, new Point(12, 2));
        habitaciones.put(4, new Point(12, 12));
        habitaciones.put(5, new Point(2, 7));
        habitaciones.put(6, new Point(7, 2));
        habitaciones.put(7, new Point(7, 12));
        habitaciones.put(8, new Point(12, 7));
        habitaciones.put(9, new Point(0, 7));
        habitaciones.put(10, new Point(7, 0));
        habitaciones.put(11, new Point(14, 7));
        habitaciones.put(12, new Point(7, 14));
        habitaciones.put(13, new Point(0, 0));
        habitaciones.put(14, new Point(0, 14));
        habitaciones.put(15, new Point(14, 0));
        habitaciones.put(16, new Point(14, 14));
        habitaciones.put(17, new Point(4, 4));
        habitaciones.put(18, new Point(4, 12));
        habitaciones.put(19, new Point(10, 4));
        habitaciones.put(20, new Point(10, 12));
    }

    private List<Point> reconstruirCaminoPath(Map<Point, Point> cameFrom, Point destino) {
        List<Point> path = new ArrayList<>();
        Point actual = destino;
        path.add(actual);
        while (cameFrom.containsKey(actual)) {
            actual = cameFrom.get(actual);
            path.add(0, actual);
        }
        return path;
    }

    private double heuristica(Point a, Point b) {
        return a.distance(b);
    }

    private List<Point> obtenerVecinos(Point pos) {
        List<Point> vecinos = new ArrayList<>();
        // Movimientos en 4 direcciones (Manhattan)
        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : direcciones) {
            int newX = pos.x + dir[0];
            int newY = pos.y + dir[1];
            if (newX >= 0 && newX < tamanyo &&
                    newY >= 0 && newY < tamanyo &&
                    mapa[newX][newY]) {
                vecinos.add(new Point(newX, newY));
            }
        }
        return vecinos;
    }

    public double distanciaEntre(int hab1, int hab2) {
        Pair<Integer, Integer> key = new Pair<>(Math.min(hab1, hab2), Math.max(hab1, hab2));
        if (cacheDistancias.containsKey(key)) {
            return cacheDistancias.get(key);
        }
        Point p1 = habitaciones.get(hab1);
        Point p2 = habitaciones.get(hab2);
        Pair<Double, List<Point>> result = buscarCaminoMasCortoBasico(p1, p2);
        cacheDistancias.put(key, result.getFirst());
        cacheCaminos.put(key, result.getSecond());
        return result.getFirst();
    }

    public double distanciaDesdeBase(int habitacion) {
        Pair<Integer, Integer> key = new Pair<>(0, habitacion);
        if (cacheDistancias.containsKey(key)) {
            return cacheDistancias.get(key);
        }
        Pair<Double, List<Point>> result = buscarCaminoMasCortoBasico(BASE, habitaciones.get(habitacion));
        cacheDistancias.put(key, result.getFirst());
        cacheCaminos.put(key, result.getSecond());
        return result.getFirst();
    }

    /**
     * Retorna el camino óptimo entre 'inicio' y 'destino' usando la caché (si ambos son nodos de interés).
     * Si no, se calcula "al vuelo" con A*.
     */
    public List<Point> obtenerCamino(Point inicio, Point destino) {
        int idInicio = getId(inicio);
        int idDestino = getId(destino);
        if (idInicio != -1 && idDestino != -1) {
            Pair<Integer, Integer> key = new Pair<>(Math.min(idInicio, idDestino), Math.max(idInicio, idDestino));
            if (cacheCaminos.containsKey(key)) {
                return new ArrayList<>(cacheCaminos.get(key));
            }
        }
        return buscarCaminoMasCortoBasico(inicio, destino).getSecond();
    }

    /**
     * Retorna el identificador del nodo para 'p'. Se define 0 para la base, 1–20 para las habitaciones.
     * Si 'p' no coincide, retorna -1.
     */
    private int getId(Point p) {
        if (p.equals(BASE)) return 0;
        for (Map.Entry<Integer, Point> entry : habitaciones.entrySet()) {
            if (p.equals(entry.getValue())) return entry.getKey();
        }
        return -1;
    }

    public Point getPosicionHabitacion(Integer integer) {
        return habitaciones.get(integer);
    }

    /**
     * Precalcula y guarda en caché el camino óptimo y la distancia entre cada par de nodos de interés.
     * Los nodos de interés son: 0 para la base y 1–20 para las habitaciones.
     */
    @Override
    public void precalcularCaminos(int fit) {
        if (lastFit == null) lastFit = fit;
        else if (lastFit == fit) return;
        cacheCaminos.clear();
        cacheDistancias.clear();
        lastFit = fit;
        // Construir el mapa de nodos: ID 0 = base; ID 1..20 = habitaciones.
        Map<Integer, Point> nodos = new HashMap<>();
        nodos.put(0, BASE);
        for (int i = 1; i <= NUM_HABITACIONES; i++) {
            nodos.put(i, habitaciones.get(i));
        }
        BiFunction<Point, Point, Pair<Double, List<Point>>> funcionBusqueda;
        switch (fit) {
            case 1:
                funcionBusqueda = this::buscarCaminoMasCortoObstaculos;
                break;
            case 2:
                funcionBusqueda = this::buscarCaminoMasCortoGiros;
                break;
            case 3:
                funcionBusqueda = this::buscarCaminoMasCortoHeuristicaMejorada;
                break;
            case 4:
                funcionBusqueda = this::buscarCaminoMasCortoTiempo;
                break;
            case 6:
                funcionBusqueda = this::buscarCaminoMasCortoMultiObjetivo;
                break;
            default:
                funcionBusqueda = this::buscarCaminoMasCortoBasico;
        }
        // Para cada par (i, j) con i < j, calcular y guardar camino y distancia.
        for (int i = 0; i < nodos.size(); i++) {
            for (int j = i + 1; j < nodos.size(); j++) {
                Point p1 = nodos.get(i);
                Point p2 = nodos.get(j);
                Pair<Double, List<Point>> data = funcionBusqueda.apply(p1, p2);
                Pair<Integer, Integer> key = new Pair<>(i, j);
                cacheCaminos.put(key, data.getSecond());
                cacheDistancias.put(key, data.getFirst());
            }
        }
    }

    /**
     * Implementa A* para hallar el camino óptimo entre 'inicio' y 'destino'.
     * Retorna un Pair: (distancia, camino).
     */
    private Pair<Double, List<Point>> buscarCaminoMasCortoBasico(Point inicio, Point destino) {
        PriorityQueue<Nodo> openSet = new PriorityQueue<>(Comparator.comparingDouble(Nodo::getF));
        Set<Point> closedSet = new HashSet<>();
        Map<Point, Double> gScore = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();

        gScore.put(inicio, 0.0);
        openSet.add(new Nodo(inicio, 0.0, heuristica(inicio, destino)));

        while (!openSet.isEmpty()) {
            Nodo actual = openSet.poll();
            if (actual.getPos().equals(destino)) {
                List<Point> camino = reconstruirCaminoPath(cameFrom, destino);
                double distancia = gScore.get(destino);
                return new Pair<>(distancia, camino);
            }
            closedSet.add(actual.getPos());
            for (Point vecino : obtenerVecinos(actual.getPos())) {
                if (closedSet.contains(vecino)) continue;
                double tentative = gScore.get(actual.getPos()) + 1;
                if (!gScore.containsKey(vecino) || tentative < gScore.get(vecino)) {
                    cameFrom.put(vecino, actual.getPos());
                    gScore.put(vecino, tentative);
                    double fScore = tentative + heuristica(vecino, destino);
                    openSet.add(new Nodo(vecino, tentative, fScore));
                }
            }
        }
        return new Pair<>(Double.MAX_VALUE, new ArrayList<>());
    }


    // Nuevo metodo buscarCaminoMasCortoObstaculos para penalizar la proximidad a obstáculos
    private Pair<Double, List<Point>> buscarCaminoMasCortoObstaculos(Point inicio, Point destino) {
        PriorityQueue<Nodo> openSet = new PriorityQueue<>(Comparator.comparingDouble(Nodo::getF));
        Set<Point> closedSet = new HashSet<>();
        Map<Point, Double> gScore = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();

        gScore.put(inicio, 0.0);
        openSet.add(new Nodo(inicio, 0.0, heuristica(inicio, destino)));

        while (!openSet.isEmpty()) {
            Nodo actual = openSet.poll();
            if (actual.getPos().equals(destino)) {
                List<Point> camino = reconstruirCaminoPath(cameFrom, destino);
                double distancia = gScore.get(destino);
                return new Pair<>(distancia, camino);
            }
            closedSet.add(actual.getPos());
            for (Point vecino : obtenerVecinos(actual.getPos())) {
                if (closedSet.contains(vecino)) continue;
                double cost = 1.0 + penalizacionObstaculos(vecino); // Se añade penalización
                double tentative = gScore.get(actual.getPos()) + cost;
                if (!gScore.containsKey(vecino) || tentative < gScore.get(vecino)) {
                    cameFrom.put(vecino, actual.getPos());
                    gScore.put(vecino, tentative);
                    double fScore = tentative + heuristica(vecino, destino);
                    openSet.add(new Nodo(vecino, tentative, fScore));
                }
            }
        }
        return new Pair<>(Double.MAX_VALUE, new ArrayList<>());
    }

    // Nuevo método penalizacionObstaculos para aumentar el coste según la cercanía de obstáculos
    private double penalizacionObstaculos(Point p) {
        double penalty = 0.0;
        // Ejemplo: por cada obstáculo en un radio de 2 celdas, sumamos 0.5 al coste
        for (Point obs : obstaculos) {
            if (p.distance(obs) <= RADIO_PENALIZACION_GIROS) penalty += PENALIZACION_OBSTACULOS;
        }
        return penalty;
    }

    private Pair<Double, List<Point>> buscarCaminoMasCortoGiros(Point inicio, Point destino) {
        PriorityQueue<Nodo> openSet = new PriorityQueue<>(Comparator.comparingDouble(Nodo::getF));
        Set<Point> closedSet = new HashSet<>();
        Map<Point, Double> gScore = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Point> cameDir = new HashMap<>(); // padre-> actual para dirección

        gScore.put(inicio, 0.0);
        openSet.add(new Nodo(inicio, 0.0, heuristica(inicio, destino)));
        cameDir.put(inicio, null);

        while (!openSet.isEmpty()) {
            Nodo actual = openSet.poll();
            if (actual.getPos().equals(destino)) {
                List<Point> camino = reconstruirCaminoPath(cameFrom, destino);
                double distancia = gScore.get(destino);
                return new Pair<>(distancia, camino);
            }
            closedSet.add(actual.getPos());
            for (Point vecino : obtenerVecinos(actual.getPos())) {
                if (closedSet.contains(vecino)) continue;
                double cost = 1.0;
                Point padre = cameDir.get(actual.getPos());
                if (padre != null) {
                    if (!mismaDireccion(padre, actual.getPos(), actual.getPos(), vecino)) {
                        cost += PENALIZACION_GIROS; // penalización por giro
                    }
                }
                double tentative = gScore.get(actual.getPos()) + cost;
                if (!gScore.containsKey(vecino) || tentative < gScore.get(vecino)) {
                    cameFrom.put(vecino, actual.getPos());
                    cameDir.put(vecino, actual.getPos());
                    gScore.put(vecino, tentative);
                    double fScore = tentative + heuristica(vecino, destino);
                    openSet.add(new Nodo(vecino, tentative, fScore));
                }
            }
        }
        return new Pair<>(Double.MAX_VALUE, new ArrayList<>());
    }

    // Nuevo método para comprobar si (p1->p2) y (p2->p3) son la misma dirección
    private boolean mismaDireccion(Point p1, Point p2, Point p2bis, Point p3) {
        int dx1 = p2.x - p1.x, dy1 = p2.y - p1.y;
        int dx2 = p3.x - p2bis.x, dy2 = p3.y - p2bis.y;
        return (dx1 == dx2 && dy1 == dy2);
    }

    private Pair<Double, List<Point>> buscarCaminoMasCortoHeuristicaMejorada(Point inicio, Point destino) {
        PriorityQueue<Nodo> openSet = new PriorityQueue<>(Comparator.comparingDouble(Nodo::getF));
        Set<Point> closedSet = new HashSet<>();
        Map<Point, Double> gScore = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();

        gScore.put(inicio, 0.0);
        openSet.add(new Nodo(inicio, 0.0, heuristica(inicio, destino)));

        while (!openSet.isEmpty()) {
            Nodo actual = openSet.poll();
            if (actual.getPos().equals(destino)) {
                List<Point> camino = reconstruirCaminoPath(cameFrom, destino);
                double distancia = gScore.get(destino);
                return new Pair<>(distancia, camino);
            }
            closedSet.add(actual.getPos());
            for (Point vecino : obtenerVecinos(actual.getPos())) {
                if (closedSet.contains(vecino)) continue;
                double tentative = gScore.get(actual.getPos()) + 1;
                if (!gScore.containsKey(vecino) || tentative < gScore.get(vecino)) {
                    cameFrom.put(vecino, actual.getPos());
                    gScore.put(vecino, tentative);
                    double fScore = tentative + heuristicaMejorada(vecino, destino);
                    openSet.add(new Nodo(vecino, tentative, fScore));
                }
            }
        }
        return new Pair<>(Double.MAX_VALUE, new ArrayList<>());
    }

    // Reemplaza el método heuristica en Casa.java con lo siguiente:
    private double heuristicaMejorada(Point a, Point b) {
        int dx = Math.abs(a.x - b.x);
        int dy = Math.abs(a.y - b.y);
        int manhattan = dx + dy;
        double densityPenalty = 0.0;
        if (manhattan == 0) return 0;
        double stepX = (b.x - a.x) / (double) manhattan;
        double stepY = (b.y - a.y) / (double) manhattan;
        for (int i = 1; i <= manhattan; i++) {
            int x = (int) Math.round(a.x + i * stepX);
            int y = (int) Math.round(a.y + i * stepY);
            if (x >= 0 && x < tamanyo && y >= 0 && y < tamanyo && !mapa[x][y])
                densityPenalty += 0.5;
        }
        return manhattan + densityPenalty;
    }

    private Pair<Double, List<Point>> buscarCaminoMasCortoTiempo(Point inicio, Point destino) {
        double velocity = VELOCIDAD;
        double turnPenalty = PENALIZACION_VELOCIDAD;
        PriorityQueue<Nodo> openSet = new PriorityQueue<>(Comparator.comparingDouble(Nodo::getF));
        Set<Point> closedSet = new HashSet<>();
        Map<Point, Double> gScore = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Point> cameDir = new HashMap<>(); // Store previous step for direction

        gScore.put(inicio, 0.0);
        openSet.add(new Nodo(inicio, 0.0, heuristica(inicio, destino)));
        cameDir.put(inicio, null);

        while (!openSet.isEmpty()) {
            Nodo actual = openSet.poll();
            if (actual.getPos().equals(destino)) {
                List<Point> camino = reconstruirCaminoPath(cameFrom, destino);
                double distancia = gScore.get(destino);
                return new Pair<>(distancia, camino);
            }
            closedSet.add(actual.getPos());
            for (Point vecino : obtenerVecinos(actual.getPos())) {
                if (closedSet.contains(vecino)) continue;
                double stepCost = 1.0 / velocity;
                Point prev = cameDir.get(actual.getPos());
                if (prev != null) {
                    int dx1 = actual.getPos().x - prev.x;
                    int dy1 = actual.getPos().y - prev.y;
                    int dx2 = vecino.x - actual.getPos().x;
                    int dy2 = vecino.y - actual.getPos().y;
                    if (dx1 != dx2 || dy1 != dy2) {
                        stepCost += turnPenalty;
                    }
                }
                double tentative = gScore.get(actual.getPos()) + stepCost;
                if (!gScore.containsKey(vecino) || tentative < gScore.get(vecino)) {
                    cameFrom.put(vecino, actual.getPos());
                    gScore.put(vecino, tentative);
                    cameDir.put(vecino, actual.getPos());
                    double fScore = tentative + heuristica(vecino, destino);
                    openSet.add(new Nodo(vecino, tentative, fScore));
                }
            }
        }
        return new Pair<>(Double.MAX_VALUE, new ArrayList<>());
    }


    private Pair<Double, List<Point>> buscarCaminoMasCortoMultiObjetivo(Point inicio, Point destino) {
        double velocity = VELOCIDAD;
        double turnPenalty = PENALIZACION_GIROS;
        double obstaclePenaltyPerStep = 0.3;
        PriorityQueue<Nodo> openSet = new PriorityQueue<>(Comparator.comparingDouble(Nodo::getF));
        Set<Point> closedSet = new HashSet<>();
        Map<Point, Double> gScore = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Point> cameDir = new HashMap<>();
        gScore.put(inicio, 0.0);
        openSet.add(new Nodo(inicio, 0.0, multiHeuristica(inicio, destino)));
        cameDir.put(inicio, null);
        while (!openSet.isEmpty()) {
            Nodo actual = openSet.poll();
            if (actual.getPos().equals(destino)) {
                List<Point> camino = reconstruirCaminoPath(cameFrom, destino);
                double distancia = gScore.get(destino);
                return new Pair<>(distancia, camino);
            }
            closedSet.add(actual.getPos());
            for (Point vecino : obtenerVecinos(actual.getPos())) {
                if (closedSet.contains(vecino)) continue;
                double stepCost = 1.0 / velocity;
                Point prev = cameDir.get(actual.getPos());
                if (prev != null) {
                    int dx1 = actual.getPos().x - prev.x;
                    int dy1 = actual.getPos().y - prev.y;
                    int dx2 = vecino.x - actual.getPos().x;
                    int dy2 = vecino.y - actual.getPos().y;
                    if (dx1 != dx2 || dy1 != dy2) stepCost += turnPenalty;
                }
                for (Point obs : obstaculos) {
                    if (vecino.distance(obs) < 1.5) stepCost += obstaclePenaltyPerStep;
                }
                double tentative = gScore.get(actual.getPos()) + stepCost;
                if (!gScore.containsKey(vecino) || tentative < gScore.get(vecino)) {
                    cameFrom.put(vecino, actual.getPos());
                    gScore.put(vecino, tentative);
                    cameDir.put(vecino, actual.getPos());
                    double fScore = tentative + multiHeuristica(vecino, destino);
                    openSet.add(new Nodo(vecino, tentative, fScore));
                }
            }
        }
        return new Pair<>(Double.MAX_VALUE, new ArrayList<>());
    }

    private double multiHeuristica(Point a, Point b) {
        int dx = Math.abs(a.x - b.x);
        int dy = Math.abs(a.y - b.y);
        int manhattan = dx + dy;
        double obstacleCost = 0.0;
        int steps = manhattan;
        double stepX = (b.x - a.x) / (steps == 0 ? 1 : (double) steps);
        double stepY = (b.y - a.y) / (steps == 0 ? 1 : (double) steps);
        for (int i = 1; i <= steps; i++) {
            int x = (int) Math.round(a.x + i * stepX);
            int y = (int) Math.round(a.y + i * stepY);
            Point p = new Point(x, y);
            for (Point obs : obstaculos) {
                if (p.distance(obs) < 1.5) obstacleCost += 0.3;
            }
        }
        return manhattan + obstacleCost;
    }


}
