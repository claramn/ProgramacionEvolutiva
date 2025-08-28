package main.utils;
import java.util.ArrayList;
import java.util.List;

class Nodo<T> {
    T valor;
    List<Nodo<T>> hijos;

    public Nodo(T valor) {
        this.valor = valor;
        this.hijos = new ArrayList<>();
    }

    public boolean agregarHijo(Nodo<T> hijo) {
        if (hijos.size() < 3) { // Máximo ternario
            hijos.add(hijo);
            return true;
        }
        return false;
    }
}