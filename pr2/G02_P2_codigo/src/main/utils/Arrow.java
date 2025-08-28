package main.utils;

public class  Arrow {
    protected int dx, dy;   // dirección

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getOrder() {
        return order;
    }

    protected int order;    // número de paso

    public Arrow(int dx, int dy, int order) {
        this.dx = dx;
        this.dy = dy;
        this.order = order;
    }
}
