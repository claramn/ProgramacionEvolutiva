package main.cruces;


import main.utils.Bloating;

/**
 * Clase abstracta que define el comportamiento general de un operador de cruce.
 * Se aplica a dos cromosomas representados como arrays de tipo genérico `T`.
 */
public abstract class Cruce<T> {
    protected int tamCromosoma;  // Tamaño del cromosoma (cantidad de genes)

    /**
     * Constructor que inicializa el tamaño del cromosoma.
     *
     * @param tamCromosoma Tamaño del cromosoma (número de genes).
     */
    public Cruce(int tamCromosoma) {
        this.tamCromosoma = tamCromosoma;
    }

    /**
     * Método abstracto que define la operación de cruce sobre dos cromosomas.
     *
     * @param c1 Primer cromosoma (se modifica directamente).
     * @param c2 Segundo cromosoma (se modifica directamente).
     */
    //TODO cambiar este bloating d sitio q aqui no queda muy limpio tbh
    public abstract void cruzar(T c1, T c2, Bloating bloating);

    protected  <T> boolean contains(T[] array, T element) {
        for (T e : array) {
            if (e != null && e.equals(element)) {
                return true;
            }
        }
        return false;
    }

    // Devuelve true si aún hay posiciones vacías (null) en el arreglo.
    protected boolean hayHuecos(Integer[] array) {
        for (Integer g : array) {
            if (g == null) {
                return true;
            }
        }
        return false;
    }

    protected boolean isValid(Integer[] array) {
        boolean[] seen = new boolean[21];

        for (Integer num : array) {
            // Verifica que el elemento no sea nulo
            if (num == null) {
                return false;
            }
            // Verifica que el número esté en el rango 1 a 20
            if (num < 1 || num > 20) {
                return false;
            }
            // Verifica que no se haya encontrado el número previamente (para evitar duplicados)
            if (seen[num]) {
                return false;
            }
            seen[num] = true;
        }
        return true;
    }
}
