package main.cruces;

public class CruceFactory {

    public static Cruce<?> getCruce(int n, int tamCromosoma) {
        switch (n) {
            case 0: return new CrucePMX(tamCromosoma);
            case 1: return new CruceOX(tamCromosoma);
            case 2: return new CruceOXPP(tamCromosoma);
            case 3: return new CruceCX(tamCromosoma);
            case 4: return new CruceCO(tamCromosoma);
            case 5: return new CruceERX(tamCromosoma);
            case 6: return new CruceHeuristicoERX(tamCromosoma);
            default: return null;
        }
    }
}
