package main.utils;

import java.awt.*;

public class Terminales {

    /**
     * AVANZA: Mueve la hormiga una celda hacia adelante según su dirección actual.
     *
     * @param estado Estado actual de la hormiga.
     * @return Nuevo estado tras avanzar.
     */
    public static Estado avanza(Estado estado) {
        switch (estado.direccion) {
            case NORTE:
                estado.y -= 1;
                break;
            case SUR:
                estado.y += 1;
                break;
            case ESTE:
                estado.x += 1;
                break;
            case OESTE:
                estado.x -= 1;
                break;
        }
        if(estado.x < 0) estado.x = 31;
        if(estado.x > 31) estado.x = 0;
        if(estado.y < 0) estado.y = 31;
        if(estado.y > 31) estado.y = 0;
        return estado;
    }
    public static Point calcularNuevaPosicion(Estado estado) {
        Point nuevaPos = new Point(estado.x, estado.y);

        switch (estado.direccion) {
            case ESTE:
                nuevaPos.x += 1;
                break;
            case SUR:
                nuevaPos.y += 1;
                break;
            case OESTE:
                nuevaPos.x -= 1;
                break;
            case NORTE:
                nuevaPos.y -= 1;
                break;
        }

        return nuevaPos;
    }

    /**
     * DERECHA: Gira la hormiga 90° a la derecha.
     *
     * @param estado Estado actual de la hormiga.
     * @return Nuevo estado tras girar a la derecha.
     */
    public static Estado derecha(Estado estado) {
        switch (estado.direccion) {
            case NORTE:
                estado.direccion = Direccion.ESTE;
                break;
            case ESTE:
                estado.direccion = Direccion.SUR;
                break;
            case SUR:
                estado.direccion = Direccion.OESTE;
                break;
            case OESTE:
                estado.direccion = Direccion.NORTE;
                break;
        }
        return estado;
    }

    /**
     * IZQUIERDA: Gira la hormiga 90° a la izquierda.
     *
     * @param estado Estado actual de la hormiga.
     * @return Nuevo estado tras girar a la izquierda.
     */
    public static Estado izquierda(Estado estado) {
        switch (estado.direccion) {
            case NORTE:
                estado.direccion = Direccion.OESTE;
                break;
            case OESTE:
                estado.direccion = Direccion.SUR;
                break;
            case SUR:
                estado.direccion = Direccion.ESTE;
                break;
            case ESTE:
                estado.direccion = Direccion.NORTE;
                break;
        }
        return estado;
    }
}
