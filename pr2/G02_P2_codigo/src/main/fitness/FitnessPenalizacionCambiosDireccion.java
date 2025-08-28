package main.fitness;

import main.mapa.Casa;
import java.awt.Point;
import main.individuo.Individuo;
import main.individuo.IndividuoRuta;

import java.util.List;

import static java.lang.Double.MAX_VALUE;

public class FitnessPenalizacionCambiosDireccion extends Fitness<Integer> {

    // Umbral en grados a partir del cual se empieza a penalizar el giro.
    private static final double THRESHOLD_GIROS = 15.0;
    // Factor multiplicador para la penalización de giros.
    private static final double FACTOR_PENALIZACION_GIROS = 2.0;


    @Override
    public double evaluar(Individuo<Integer> individuo) {

        double total = 0.0;
        IndividuoRuta ind = (IndividuoRuta) individuo;
        Integer[] cromosoma = ind.getCromosoma();
        Casa casa = ind.getCasa();
        int num = casa.getNumHabitaciones();

        // 1. Costo base: distancias (ida, entre habitaciones y vuelta a la base).
        double seg = casa.distanciaDesdeBase(cromosoma[0]);
        total += (seg == MAX_VALUE ? PENALTY_VALUE : seg);
        for (int i = 1; i < num; i++) {
            seg = casa.distanciaEntre(cromosoma[i - 1], cromosoma[i]);
            total += (seg == MAX_VALUE ? PENALTY_VALUE : seg);
        }
        seg = casa.distanciaDesdeBase(cromosoma[num - 1]);
        total += (seg == MAX_VALUE ? PENALTY_VALUE : seg);

        // 2. Penalización por giros: se calculará de dos formas (según el flag useFullPath).
        double giroPenalty = 0.0;
            // Usaremos la información completa del camino (lista de puntos) para cada segmento.
            // Se asume que la clase Casa dispone de métodos que devuelven la ruta completa:
            // getCaminoDesdeBase(h), getCaminoEntre(h1, h2) y getCaminoHaciaBase(h)
            List<Point> path;

            // a) Desde la base hasta la primera habitación.
            path = casa.obtenerCamino(casa.getBase(),casa.getPosicionHabitacion(cromosoma[0]));
            giroPenalty += calcularPenalizacionGirosPorPath(path);

            // b) Entre habitaciones consecutivas.
            for (int i = 1; i < num; i++) {
                path = casa.obtenerCamino(casa.getPosicionHabitacion(cromosoma[i - 1]), casa.getPosicionHabitacion(cromosoma[i]));
                giroPenalty += calcularPenalizacionGirosPorPath(path);
            }

            // c) Desde la última habitación de vuelta a la base.
            path = casa.obtenerCamino(casa.getPosicionHabitacion(cromosoma[num - 1]), casa.getBase());
            giroPenalty += calcularPenalizacionGirosPorPath(path);

        return total + giroPenalty;
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
