package main.fitness;

import main.individuo.Individuo;
import main.individuo.IndividuoRuta;
import main.mapa.Casa;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FitnessMultiObjetivo extends Fitness<Integer> {
    // Pesos para cada criterio (puedes ajustarlos o parametrizarlos)
    private static final double WEIGHT_DISTANCE = 1.0;
    private static final double WEIGHT_GIROS = 1.0;
    private static final double WEIGHT_OBSTACULOS = 1.0;


    // Constantes para la penalización de giros.
    private static final double THRESHOLD_GIROS = 15.0;  // grados
    private static final double FACTOR_GIRO = 2.0;         // costo extra por grado excedido

    // Constantes para la penalización por obstáculos.
    private static final double OBSTACLE_THRESHOLD = 5.0;  // si se está a menos de 5 unidades
    private static final double FACTOR_OBSTACLE = 5.0;    // factor de penalización

    @Override
    public double evaluar(Individuo<Integer> individuo) {
        // Convertir al tipo específico de individuo que tiene la información de la casa y el cromosoma.
        IndividuoRuta ind = (IndividuoRuta) individuo;
        Casa casa = ind.getCasa();
        Integer[] cromosoma = ind.getCromosoma();

        // Construir el fullPath: desde la base -> cada habitación en el orden del cromosoma -> volver a la base.
        List<Point> fullPath = new ArrayList<>();

        // 1. Desde la base hasta la primera habitación.
        List<Point> segmento =casa.obtenerCamino(casa.getBase(), casa.getPosicionHabitacion(cromosoma[0]));
        fullPath.addAll(segmento);

        // 2. Entre habitaciones consecutivas.
        for (int i = 1; i < cromosoma.length; i++) {
            segmento = casa.obtenerCamino(casa.getPosicionHabitacion(cromosoma[i - 1]), casa.getPosicionHabitacion(cromosoma[i]));
            // Evitar duplicados: si el último punto del fullPath es igual al primero del segmento, se elimina.
            if (!fullPath.isEmpty() && !segmento.isEmpty() && fullPath.get(fullPath.size() - 1).equals(segmento.get(0))) {
                segmento.remove(0);
            }
            fullPath.addAll(segmento);
        }

        // 3. Desde la última habitación de vuelta a la base.
        segmento = casa.obtenerCamino(casa.getPosicionHabitacion(cromosoma[cromosoma.length - 1]), casa.getBase());
        if (!fullPath.isEmpty() && !segmento.isEmpty() && fullPath.get(fullPath.size() - 1).equals(segmento.get(0))) {
            segmento.remove(0);
        }
        fullPath.addAll(segmento);

        // Calcular cada uno de los componentes:
        double totalDistance = calcularTotalDistancia(fullPath);
        double giroPenalty = calcularPenalizacionGirosPorPath(fullPath);
        double obstaclePenalty = calcularPenalizacionObstaculosPorPath(fullPath, casa.getObstaculos());

        // Combinación lineal
        double fitness = WEIGHT_DISTANCE * totalDistance
                + WEIGHT_GIROS * giroPenalty
                + WEIGHT_OBSTACULOS * obstaclePenalty;
        return fitness;
    }

    /**
     * Calcula la distancia total recorrida para la lista de puntos.
     */
    private double calcularTotalDistancia(List<Point> path) {
        double dist = 0.0;
        for (int i = 1; i < path.size(); i++) {
            dist += path.get(i - 1).distance(path.get(i));
        }
        return dist;
    }

    /**
     * Calcula la penalización acumulada por giros a lo largo del camino.
     * Para cada vértice (punto en posición i, 1 <= i <= size-2) se calcula el ángulo formado por (i-1, i, i+1)
     * y se penaliza si excede el umbral THRESHOLD_GIROS.
     */
    private double calcularPenalizacionGirosPorPath(List<Point> path) {
        double penTotal = 0.0;
        if (path.size() < 3) return penTotal;
        for (int i = 1; i < path.size() - 1; i++) {
            double angulo = calcularTurno(path.get(i - 1), path.get(i), path.get(i + 1));
            if (angulo > THRESHOLD_GIROS) {
                penTotal += FACTOR_GIRO * (angulo - THRESHOLD_GIROS);
            }
        }
        return penTotal;
    }

    /**
     * Calcula la penalización por cercanía a obstáculos a lo largo del camino.
     * Por cada punto del path, se obtiene la distancia al obstáculo más cercano; si esta distancia es menor que
     * OBSTACLE_THRESHOLD, se añade un coste proporcional.
     */
    private double calcularPenalizacionObstaculosPorPath(List<Point> path, Set<Point> obstaculos) {
        double penTotal = 0.0;
        for (Point p : path) {
            double minDist = Double.MAX_VALUE;
            for (Point obs : obstaculos) {
                double d = p.distance(obs);
                if (d < minDist) {
                    minDist = d;
                }
            }
            if (minDist < OBSTACLE_THRESHOLD) {
                penTotal += FACTOR_OBSTACLE * (OBSTACLE_THRESHOLD - minDist);
            }
        }
        return penTotal;
    }

    /**
     * Calcula el ángulo de giro (en grados) formado en p2 por los puntos p1, p2 y p3.
     * Se define como el ángulo entre el vector (p2 - p1) y el vector (p3 - p2).
     */
    private double calcularTurno(Point p1, Point p2, Point p3) {
        double v1x = p2.x - p1.x;
        double v1y = p2.y - p1.y;
        double v2x = p3.x - p2.x;
        double v2y = p3.y - p2.y;
        double dot = v1x * v2x + v1y * v2y;
        double mag1 = Math.sqrt(v1x * v1x + v1y * v1y);
        double mag2 = Math.sqrt(v2x * v2x + v2y * v2y);
        if (mag1 == 0 || mag2 == 0) {
            return 0;
        }
        double cosTheta = dot / (mag1 * mag2);
        cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));
        double thetaRad = Math.acos(cosTheta);
        return Math.toDegrees(thetaRad);
    }
}
