package main.mutaciones;

import main.individuo.Individuo;

public abstract class Mutacion<T> {

    public abstract void mutar(Individuo<T> individuoRuta);
}
