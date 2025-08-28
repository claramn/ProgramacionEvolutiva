package main.mapa;

public class MapaFactory {

    private static Mapa instance;

    public static Mapa getMapa() {
        if (instance == null) {
            instance = new Casa();
        }
        return instance;
    }
}
