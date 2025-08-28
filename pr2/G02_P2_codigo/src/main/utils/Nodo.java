package main.utils;

import java.awt.*;

public class Nodo {
    Point pos;
    double g, f;

    public Point getPos() {
        return pos;
    }

    public double getG() {
        return g;
    }

    public double getF() {
        return f;
    }

    public Nodo(Point pos, double g, double f) {
        this.pos = pos;
        this.g = g;
        this.f = f;
    }

}
