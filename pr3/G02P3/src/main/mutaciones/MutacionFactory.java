package main.mutaciones;

public class MutacionFactory {

    public static Mutacion<?> getMutacion(int n) {
        switch (n) {
            case 0: return new Terminal();
            case 1: return new Funcional();
            case 2: return new ArbolSubarbol();
            case 3: return new MutacionPermutacion();
            case 4: return new MutacionGramatica();
            default: return null;
        }
    }
}
