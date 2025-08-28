package main.selecciones;

public class SeleccionFactory {

    private static final double TRUNCATION_FACTOR = 0.5;


    private static final double PROBABILITY_FACTOR = 0.75;

    public static Seleccion getAlgSeleccion(int n, double[] fitness, int tamPoblacion, boolean min, double mejor, int tamTorneo) {
        switch (n) {
            case 0:
                return new Ruleta(fitness, tamPoblacion, min, mejor);
            case 1:
                return new TorneoDeterministico(fitness, tamPoblacion, min,tamTorneo);
            case 2:
                return new TorneoProbabilistico(fitness, tamPoblacion, min, PROBABILITY_FACTOR,tamTorneo);
            case 3:
                return new EstocasticoUniversal(fitness, tamPoblacion, min, mejor);
            case 4:
                return new Truncamiento(fitness, tamPoblacion, min, TRUNCATION_FACTOR);
            case 5:
                return new Restos(fitness, tamPoblacion, min, mejor);
            case 6:
                return new Ranking(fitness, tamPoblacion, min, mejor);

            default:
                return null;
        }

    }

}
