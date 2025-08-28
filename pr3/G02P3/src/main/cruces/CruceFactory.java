package main.cruces;

public class CruceFactory {

    public static Cruce<?> getCruce(int n, int tamCromosoma, int bloating) {
        switch (n) {
            case 0: return new CruceArbol(tamCromosoma,bloating);
            case 1: return new CruceMonopunto(tamCromosoma);
            case 2: return new CruceUniforme(tamCromosoma);

            default: return null;
        }
    }
}
