package main.utils;

public interface Operacion {
    /**
     * Ejecuta la operación sobre el estado dado y retorna el nuevo estado.
     *
     * @param estado Estado actual de la hormiga.
     * @return Nuevo estado tras la operación.
     */
    Estado ejecutar(Estado estado);
}
