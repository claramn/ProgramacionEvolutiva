package main.fitness;

import main.individuo.Individuo;
import main.individuo.IndividuoRuta;
import main.mapa.Casa;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.MAX_VALUE;

public class FitnessCosteTiempo extends Fitness<Integer> {

    // Valor de penalización para segmentos inválidos.
    private static final double PENALTY_VALUE = 1000.0;
    // Velocidad constante (unidades de distancia por unidad de tiempo)
    private static final double VELOCITY = 10.0;
    // Umbral en grados para considerar que un giro es pronunciado.
    private static final double THRESHOLD_GIROS = 15.0;
    // Factor que multiplica el exceso de ángulo para calcular el tiempo extra (por ejemplo, en segundos).
    private static final double FACTOR_PENALIZACION_GIROS = 0.5;

    @Override
    public double evaluar(Individuo<Integer> individuo) {
        IndividuoRuta ind = (IndividuoRuta) individuo;
        Integer[] cromosoma = ind.getCromosoma();
        Casa casa = ind.getCasa();

        // Construimos el camino completo (lista de puntos) que va de la base,
        // pasando por todas las habitaciones en el orden del cromosoma, y regresa a la base.
        List<Point> fullPath = new ArrayList<>();

        // 1. Desde la Base hasta la primera habitación.
        List<Point> caminoSegmento = casa.obtenerCamino(casa.getBase(), casa.getPosicionHabitacion(cromosoma[0]));
        fullPath.addAll(caminoSegmento);

        // 2. Entre cada par de habitaciones consecutivas.
        for (int i = 1; i < cromosoma.length; i++) {
            caminoSegmento = casa.obtenerCamino(casa.getPosicionHabitacion(cromosoma[i - 1]), casa.getPosicionHabitacion(cromosoma[i]));
            // Si el último punto del fullPath y el primer punto de caminoSegmento son iguales, evitamos duplicados:
            if (!fullPath.isEmpty() && !caminoSegmento.isEmpty() &&
                    fullPath.get(fullPath.size() - 1).equals(caminoSegmento.get(0))) {
                caminoSegmento.remove(0);
            }
            fullPath.addAll(caminoSegmento);
        }

        // 3. Desde la última habitación hasta la Base.
        caminoSegmento = casa.obtenerCamino(casa.getPosicionHabitacion(cromosoma[0]), casa.getBase());
        if (!fullPath.isEmpty() && !caminoSegmento.isEmpty() &&
                fullPath.get(fullPath.size() - 1).equals(caminoSegmento.get(0))) {
            caminoSegmento.remove(0);
        }
        fullPath.addAll(caminoSegmento);

        // Calcular la distancia total recorrida a lo largo del fullPath.
        double totalDistance = 0.0;
        for (int i = 1; i < fullPath.size(); i++) {
            totalDistance += fullPath.get(i - 1).distance(fullPath.get(i));
        }

        // Tiempo base: distancia total / velocidad.
        double tiempoBase = (totalDistance == MAX_VALUE ? PENALTY_VALUE : totalDistance) / VELOCITY;

        // Calcular la penalización por giros utilizando la lista de puntos completa.
        double tiempoExtraGiros = calcularPenalizacionGirosPorPath(fullPath);

        return tiempoBase + tiempoExtraGiros;
    }

    /**
     * Calcula el ángulo de giro (en grados) formado en un vértice, dada una tripleta de puntos.
     * Se define como el ángulo entre el vector (p2 - p1) y (p3 - p2). Si el camino es recto, el ángulo es 0°.
     *
     * @param p1 Primer punto.
     * @param p2 Punto vértice.
     * @param p3 Tercer punto.
     * @return Ángulo en grados.
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
        // Aseguramos que cosTheta esté dentro de [-1, 1]
        cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));
        double thetaRad = Math.acos(cosTheta);
        return Math.toDegrees(thetaRad);
    }

    /**
     * Calcula la penalización dada un ángulo de giro. Si el ángulo supera el umbral definido,
     * se penaliza proporcionalmente al exceso.
     *
     * @param angulo Ángulo en grados.
     * @return Penalización.
     */
    private double calcularPenalizacionGiros(double angulo) {
        if (angulo > THRESHOLD_GIROS) {
            return FACTOR_PENALIZACION_GIROS * (angulo - THRESHOLD_GIROS);
        }
        return 0.0;
    }

    /**
     * Calcula la penalización por giros para un camino completo dado como lista de puntos.
     * Se recorre la lista y por cada vértice (en una tripleta consecutiva) se calcula la penalización.
     *
     * @param path Lista de puntos que representa el camino (por ejemplo, cada casilla recorrido).
     * @return Penalización total por giros en el camino.
     */
    private double calcularPenalizacionGirosPorPath(List<Point> path) {
        double pen = 0.0;
        if (path.size() < 3) {
            return pen;
        }
        for (int i = 1; i < path.size() - 1; i++) {
            double ang = calcularTurno(path.get(i - 1), path.get(i), path.get(i + 1));
            pen += calcularPenalizacionGiros(ang);
        }
        return pen;
    }
}
