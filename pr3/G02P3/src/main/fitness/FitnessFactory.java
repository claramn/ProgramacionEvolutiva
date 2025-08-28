package main.fitness;


public class FitnessFactory {

    public static Fitness<?> getFitness(int n,int pasos) {
        switch (n) {
            case 0: return new FitnessBasico(pasos);
            case 1: return new FitnessGramatica(pasos);
            default: return null;
        }
    }
}
