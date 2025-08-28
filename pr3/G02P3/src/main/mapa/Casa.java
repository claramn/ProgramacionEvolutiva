package main.mapa;

import main.utils.Estado;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Casa {
    private static final Point BASE = new Point(0, 0);
    private static final int TAMANO = 32; // Tamaño fijo del mapa
    private int tamanyo;
    private Point base;
    private Set<Point> food;
    private int foodEaten;

    public Casa() {
        this.tamanyo = TAMANO;
        this.base = BASE;
        this.food = new HashSet<>();
        this.foodEaten = 0;
        // Inicializamos el rastro de Santa Fe (los 89 bocados)
        inicializarRastroSantaFe();
    }

    /**
     * Inicializa el rastro de Santa Fe colocando 89 "bocados" (comida) en posiciones
     * predefinidas según el patrón clásico. Cada posición se añade a un conjunto de puntos.
     */
    private void inicializarRastroSantaFe() {
        int[][] posicionesComida = {
                {0,0}, {1,0}, {2,0}, {3,0},        // Parte superior inicial
                {3,1}, {3,2}, {3,3}, {3,4}, {3,5},
                {4,5}, {5,5}, {6,5},
                {8,5}, {9,5}, {10,5}, {11,5}, {12,5},
                {12,6}, {12,7}, {12,8}, {12,9}, {12,10},
                {12,12}, {12,13}, {12,14}, {12,15},
                {12,18}, {12,19}, {12,20}, {12,21},{12,22},{12,23},
                {11,24}, {10,24}, {9,24},{8,24},{7,24},                 // Extremo derecho
                {4,24},{3,24},{1,25},{1,26},{1,27},{1,28},
                {2,30},{3,30},{4,30},{5,30},
                {7,29},{7,28},
                {8,27},{9,27},{10,27},{11,27},{12,27},{13,27},{14,27},
                {16,26},{16,25},{16,24},{16,21},{16,19},{16,18},{16,17},
                {17,16},{20,15},{20,14},{20,11},{20,10},{20,9},{20,8},{21,5},{22,5},{24,4},{24,3},{25,2},
                {26,2},{27,2},{29,3},{29,4},{29,6},{29,9},{29,12},{28,14},{27,14},{26,14},{24,18},{27,19},
                {26,22},{23,23},{23,15}
        };

        // Limpiamos cualquier estado previo
        food.clear();
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
            }
        }

        // Configuramos las nuevas posiciones de comida
        for (int[] pos : posicionesComida) {
            int x = pos[0];
            int y = pos[1];
            food.add(new Point(x, y));
        }
    }

    /**
     * Devuelve el conjunto de posiciones donde hay comida (el rastro de Santa Fe).
     *
     * @return Un Set de Points que representan las celdas con comida.
     */
    public Set<Point> getFood() {
        return new HashSet<>(food); // Devolvemos una copia para evitar modificaciones externas
    }

    /**
     * Comprueba si hay comida en una posición específica.
     *
     * @param p La posición a comprobar
     * @return true si hay comida en la posición, false en caso contrario
     */
    public boolean hayComida(Point p) {
        return food.contains(p);
    }

    /**
     * Comprueba si hay comida delante de la posición actual según la dirección.
     *
     * @param estado El estado actual (posición y dirección)
     * @return true si hay comida delante, false en caso contrario
     */
    public boolean hayComidaDelante(Estado estado) {
        int nextX = estado.x;
        int nextY = estado.y;
        switch (estado.direccion) {
            case NORTE:
                nextY = (nextY - 1 + tamanyo) % tamanyo;
                break;
            case SUR:
                nextY = (nextY + 1) % tamanyo;
                break;
            case ESTE:
                nextX = (nextX + 1) % tamanyo;
                break;
            case OESTE:
                nextX = (nextX - 1 + tamanyo) % tamanyo;
                break;
        }
        return hayComida(new Point(nextX, nextY));
    }

    /**
     * La hormiga come la comida en la posición actual.
     *
     * @param estado El estado actual (posición y dirección)
     * @return true si se ha comido comida, false si no había
     */
    public boolean comerComida(Estado estado) {
        Point p = new Point(estado.x, estado.y);
        if (hayComida(p)) {
            food.remove(p);
            foodEaten++;
            return true;
        }
        return false;
    }

    public Point getBase() {
        return new Point(base); // Devolvemos una copia para evitar modificaciones externas
    }

    public int getFoodEaten() {
        return foodEaten;
    }

    public int getWidth() {
        return tamanyo;
    }

    public int getHeight() {
        return tamanyo;
    }

    /**
     * Reinicia el estado de la comida en el mapa.
     */
    public void repaintFood() {
        foodEaten = 0;
        inicializarRastroSantaFe();
    }

    @Override
    public Casa clone() {
        Casa casa = new Casa();
        casa.tamanyo = this.tamanyo;
        casa.base = new Point(this.base);
        casa.foodEaten = this.foodEaten;
        casa.food = new HashSet<>(this.food);
        return casa;
    }
}