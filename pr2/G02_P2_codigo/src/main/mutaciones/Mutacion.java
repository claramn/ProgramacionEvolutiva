package main.mutaciones;

import main.individuo.Individuo;
import main.individuo.IndividuoRuta;

public abstract class Mutacion<T> {

    public abstract void mutar(Individuo<T> individuoRuta);
}
