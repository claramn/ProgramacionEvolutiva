package main.mapa;

import main.utils.Pair;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representa un mapa de 32×32 sin habitaciones ni obstáculos.
 * Se utiliza para calcular distancias y rutas.
 */
public class Mapa {
    protected int tamanyo;
    protected static final int TAMANO = 32; // Tamaño fijo del mapa
    protected Point base;
    protected boolean[][] mapa;
    protected Set<Point> food;


    public Point getBase() {
        return base;
    }
}
