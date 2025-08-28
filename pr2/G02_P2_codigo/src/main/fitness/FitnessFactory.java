package main.fitness;


public class FitnessFactory {

    public static Fitness<?> getFitness(int n) {
        switch (n) {
            case 0: return new FitnessBasico();
            case 1: return new FitnessPenalizacionProximidadObstaculos();
            case 2: return new FitnessPenalizacionCambiosDireccion();
            case 3: return new FitnessHeuristicaSofisticada();
            case 4: return new FitnessCosteTiempo();
            case 5: return new FitnessDistanciaEuclidiana();
            case 6: return new FitnessMultiObjetivo();
            default: return null;
        }
    }
}
