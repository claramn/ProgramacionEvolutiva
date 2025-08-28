package main.ventana;

public class DTORequest {

    private final int func;
    private final int tammPoblacion;
    private final int numGeneraciones;
    private final double cruce;
    private final double mutacion;
    private final int tipoSseleccion;
    private final int tipoMutacion;
    private final int tipoCruce;
    private final double elitismo;
    private final boolean escalado;

    public DTORequest(int func, int tamPobla, int numGeneraciones, double cruc, double porcent, int selec, int mut, int cruzSelec, double elitismo, boolean escalado) {
        this.func = func;
        this.tammPoblacion = tamPobla;
        this.numGeneraciones = numGeneraciones;
        this.cruce = cruc;
        this.mutacion = porcent;
        this.tipoSseleccion = selec;
        this.tipoMutacion = mut;
        this.tipoCruce = cruzSelec;
        this.elitismo = elitismo;
        this.escalado = escalado;
    }

    public int getFunc() {
        return func;
    }

    public int getTammPoblacion() {
        return tammPoblacion;
    }

    public int getNumGeneraciones() {
        return numGeneraciones;
    }

    public double getCruce() {
        return cruce;
    }

    public double getMutacion() {
        return mutacion;
    }

    public int getTipoSseleccion() {
        return tipoSseleccion;
    }

    public int getTipoMutacion() {
        return tipoMutacion;
    }

    public int getTipoCruce() {
        return tipoCruce;
    }

    public double getElitismo() {
        return elitismo;
    }

    public boolean isEscalado() {return escalado;}
}

