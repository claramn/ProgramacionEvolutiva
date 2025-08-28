package main.algoritmo;

import main.cruces.Cruce;
import main.cruces.CruceFactory;
import main.fitness.FitnessFactory;
import main.individuo.Individuo;
import main.individuo.IndividuoFactory;
import main.mapa.MapaFactory;
import main.mutaciones.MutacionFactory;
import main.selecciones.Seleccion;
import main.selecciones.SeleccionFactory;
import main.utils.Pair;
import main.ventana.DTORequest;
import main.ventana.DTOResponse;

import java.util.Arrays;
import java.util.Comparator;


public class AlgoritmoGenetico<T> {

    private final int tamPoblacion;
    private final double[] fitness;
    private final int maxGeneraciones;
    private final double probCruce;
    private final double probMutacion;
    private final int tipoSeleccion;
    private final int tipoFitness;
    private final int tipoCruce;
    private final int tipoMutacion;
    private final boolean min; // indica el tipo de problema que es, maximizacion o minimizacion
    private final boolean escalado;
    private final double[] mediaGeneracion;
    private final double[] mejorGeneracion;
    private final double[] mejorAbsoluto;
    private final boolean elitismo;
    private final int tamElit;
    private final Individuo[] elite;
    private int generacionActual;
    private Individuo<T>[] poblacion;
    private double fitnessTotal;
    private Individuo<T> elMejor;
    private int numCruzados;
    private int numMutados;

    //puede haber más cosas

    public AlgoritmoGenetico(DTORequest dtoRequest) {
        this.tamPoblacion = dtoRequest.getTammPoblacion();
        this.poblacion = new Individuo[tamPoblacion];
        this.fitness = new double[tamPoblacion];
        this.maxGeneraciones = dtoRequest.getNumGeneraciones();
        this.probCruce = dtoRequest.getCruce();
        this.probMutacion = dtoRequest.getMutacion();
        this.elMejor = null;
        this.tipoSeleccion = dtoRequest.getTipoSseleccion();
        this.tipoFitness = dtoRequest.getFunc();
        this.tipoCruce = dtoRequest.getTipoCruce();
        this.tipoMutacion = dtoRequest.getTipoMutacion();
        this.escalado = dtoRequest.isEscalado();
        this.mediaGeneracion = new double[maxGeneraciones];
        this.mejorGeneracion = new double[maxGeneraciones];
        this.mejorAbsoluto = new double[maxGeneraciones];

        this.min = true; // siempre minimiza
        this.elitismo = dtoRequest.getElitismo() != 0;
        this.tamElit = this.elitismo ? (int) Math.ceil(tamPoblacion * dtoRequest.getElitismo() / 100) : 0;
        this.elite = new Individuo[tamElit];
        this.numCruzados =0;
        this.numMutados =0;
    }

    public DTOResponse run() {
        initPoblacion(); //Iniciar poblacion y evaluar poblacion
        generacionActual = 0;
        this.evaluar();
        while (generacionActual < this.maxGeneraciones) {

            if (escalado) this.escaladoLineal();

            if (elitismo) seleccionarElite();


            int[] seleccionados = procesoSeleccion(); //Seleccion
            this.procesoCruze(seleccionados); //Cruce
            this.procesoMutacion();

            if (elitismo) reintegrarElite();

            this.evaluar();

            generacionActual++;
        }

        Pair<Double, Double> interval = IndividuoFactory.getIntervalo(this.tipoFitness);
        return new DTOResponse(this.mediaGeneracion, this.mejorGeneracion, this.mejorAbsoluto, interval, this.elMejor, this.numCruzados, this.numMutados);
    }

    private void initPoblacion() {

        var mut = MutacionFactory.getMutacion(this.tipoMutacion);
        var fit = FitnessFactory.getFitness(this.tipoFitness);
        var map = MapaFactory.getMapa();
        map.precalcularCaminos(this.tipoFitness);
        for (int i = 0; i < tamPoblacion; i++) {
            this.poblacion[i] = (Individuo<T>) IndividuoFactory.getIndividuo(map,fit,mut);
        }
        this.elMejor = this.poblacion[0];
    }

    private int[] procesoSeleccion() {
        double fit = this.min ? Arrays.stream(fitness).max().orElse(0) : Arrays.stream(fitness).min().orElse(0);
        Seleccion sel = SeleccionFactory.getAlgSeleccion(this.tipoSeleccion, this.fitness, this.tamPoblacion, this.min, fit);
        assert sel != null;
        return sel.getSeleccion();
    }

    private void procesoCruze(int[] seleccionados) {

        Cruce cruce = CruceFactory.getCruce(this.tipoCruce, this.elMejor.getTamGenes());
        this.poblacion = this.copiaPoblacion(seleccionados);
        int numCruce = 0;
        int[] seleccionadosCruce = new int[tamPoblacion];
        for (int i = 0; i < tamPoblacion; i++) {
            if (this.probCruce > Math.random()) {
                seleccionadosCruce[numCruce++] = i;
            }
        }

        if (numCruce % 2 != 0) {
            numCruce--;
        }
        this.numCruzados+=numCruce;
        for (int i = 0; i < numCruce; i += 2) {
            T[] c1 = this.poblacion[seleccionadosCruce[i]].getCromosoma();
            T[] c2 = this.poblacion[seleccionadosCruce[i + 1]].getCromosoma();
            cruce.cruzar(c1, c2);
            this.poblacion[seleccionadosCruce[i]].setCromosoma(c1);
            this.poblacion[seleccionadosCruce[i + 1]].setCromosoma(c2);
        }
    }

    private void procesoMutacion() {
        for (Individuo<T> individuo : poblacion) {
            if (Math.random() < this.probMutacion){
                individuo.mutacion();
                this.numMutados++;
            }
        }
    }

    private void evaluar() {
        this.fitnessTotal = 0;
        double mejorGeneracion = Double.MAX_VALUE;

        for (int i = 0; i < tamPoblacion; i++) {
            double f = this.poblacion[i].getFitness();
            this.fitness[i] = f;
            fitnessTotal += f;

            if (mejorGeneracion > f) {
                mejorGeneracion = f;
            }

            double x = this.elMejor.getFitness();
            if (f < this.elMejor.getFitness()) {
                elMejor = this.poblacion[i].clone();
            }
        }

        this.mejorGeneracion[generacionActual] = mejorGeneracion;
        this.mejorAbsoluto[generacionActual] = this.getMejorAbsoluto();
        this.mediaGeneracion[generacionActual] = this.getMediaGeneracion();
    }

    private double getMediaGeneracion() {
        return this.fitnessTotal / tamPoblacion;
    }

    private double getMejorAbsoluto() {
        return this.elMejor.getFitness();
    }

    private Individuo<T>[] copiaPoblacion(int[] seleccionados) {
        Individuo<T>[] copia = new Individuo[tamPoblacion];
        for (int i = 0; i < tamPoblacion; i++) {
            copia[i] = this.poblacion[seleccionados[i]].clone();
        }
        return copia;
    }

    /**
     * Aplica escalado lineal al array this.fitness,
     * usando la fórmula de Whitley con factor P.
     */
    private void escaladoLineal() {

        double maxFit = Double.NEGATIVE_INFINITY;
        double sumFit = 0.0;
        boolean[] negativs = new boolean[tamPoblacion];
        for (int i = 0; i < this.fitness.length; i++) {
            double f = this.fitness[i];
            if (min) {  // Solo en minimización, guardamos si el valor original era negativo
                negativs[i] = f < 0;
            }
            double v = Math.abs(f);
            if (v > maxFit) {
                maxFit = v;
            }
            sumFit += v;
        }
        double avgFit = sumFit / tamPoblacion;

        if (Math.abs(maxFit - avgFit) < 1e-9) {
            return; // no escalamos
        }

        double P = 1.5;

        double a = ((P - 1) * avgFit) / (maxFit - avgFit);
        double b = avgFit * (1.0 - a);

        for (int i = 0; i < this.tamPoblacion; i++) {
            double fEscalado = a * Math.abs(fitness[i]) + b;
            fitness[i] = min ? (negativs[i] ? -fEscalado : fEscalado) : Math.max(fEscalado, 0);
        }
    }


    private void seleccionarElite() {

        Integer[] indices = new Integer[tamPoblacion];
        for (int i = 0; i < tamPoblacion; i++) {
            indices[i] = i;
        }

        // - Como el problema es de minimización, los mejores son los que tienen fitness menor (más negativos)
        //   por lo que ordenamos en orden ascendente.
        Arrays.sort(indices, Comparator.comparingDouble(i -> fitness[i]));

        for (int i = 0; i < tamElit; i++) {
            elite[i] = poblacion[indices[i]].clone();
        }
    }


    private void reintegrarElite() {

        Integer[] indices = new Integer[tamPoblacion];
        for (int i = 0; i < tamPoblacion; i++) {
            indices[i] = i;
        }

        // - En minimización, los peores son los que tienen fitness mayor (menos negativos)
        //   Por ello, ordenamos en orden descendente, de modo que los primeros sean los peores.

        Arrays.sort(indices, (i, j) -> Double.compare(fitness[j], fitness[i]));

        for (int i = 0; i < tamElit; i++) {
            int idxWorst = indices[i]; // Los primeros son los peores según el criterio usado
            poblacion[idxWorst] = elite[i].clone();
            fitness[idxWorst] = elite[i].getFitness(); // Actualizamos también el fitness
        }
    }
}


