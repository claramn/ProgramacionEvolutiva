package main.ventana;

public class DTORequest {

    private final int inicializacion;
    private final int tammPoblacion;
    private final int numGeneraciones;
    private final double cruce;
    private final double mutacion;
    private final int tipoSseleccion;
    private final int tipoMutacion;
    private final int[] bloating;
    private final double elitismo;
    private final boolean escalado;
    private final int numPasos;
    private final int profMin;
    private final int profMax;
    private final int tamTorneo;
    private final int wraps;
    private final int tipo;

    public DTORequest(int tipo,int func, int tamPobla, int numGeneraciones, double cruc, double porcent, int selec, int mut, int[] bloating, double elitismo, boolean escalado, int numPasos, int profMin, int profMax,int tamTorneo, int wraps) {
        this.inicializacion = func;
        this.tammPoblacion = tamPobla;
        this.numGeneraciones = numGeneraciones;
        this.cruce = cruc;
        this.mutacion = porcent;
        this.tipoSseleccion = selec;
        this.tipoMutacion = mut;
        this.bloating = bloating;
        this.elitismo = elitismo;
        this.escalado = escalado;
        this.numPasos = numPasos;
        this.profMin = profMin ;
        this.profMax = profMax;
        this.tamTorneo = tamTorneo;
        this.wraps = wraps;
        this.tipo = tipo;
    }

    public int getInicializacion() {
        return inicializacion;
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

    public int[] getBloating() {
        return bloating;
    }

    public double getElitismo() {
        return elitismo;
    }

    public boolean isEscalado() {
        return escalado;
    }

    public int getNumPasos() {
        return numPasos;
    }

    public int getProfMin() {
        return profMin;
    }

    public int getProfMax() {
        return profMax;
    }
    public int getTamTorneo() {
        return tamTorneo;
    }

    public int getWraps() {
        return wraps;
    }

    public int getTipoGramaticaOArbol() {
        return tipo;
    }
}
