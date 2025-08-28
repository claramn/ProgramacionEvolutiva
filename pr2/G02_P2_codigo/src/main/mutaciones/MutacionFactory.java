package main.mutaciones;

public class MutacionFactory {

    public static Mutacion<?> getMutacion(int n) {
        switch (n) {
            case 0: return new Inserccion();
            case 1: return new Intercambio();
            case 2: return new Inversion();
            case 3: return new Heuristica();
            case 4: return new InvencionPropia();
            default: return null;
        }
    }
}
