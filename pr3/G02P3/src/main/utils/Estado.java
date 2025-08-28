package main.utils;

public class Estado {
    public int x;
    public int y;
    public Direccion direccion;

    public Estado(int x, int y, Direccion direccion) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Estado{" + "x=" + x + ", y=" + y + ", direccion=" + direccion + '}';
    }
}
