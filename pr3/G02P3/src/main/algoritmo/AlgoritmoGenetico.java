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
import main.utils.Arbol;
import main.utils.Bloating;
import main.utils.Pair;
import main.ventana.DTORequest;
import main.ventana.DTOResponse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.*; // Para clases atómicas
import java.util.stream.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.AtomicReference;


public class AlgoritmoGenetico<T> {
    private final int MAX_PROFUNDIDAD;
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
    private int[] bloating;
    private int[] tams;
    private double[] distanciaRecorrida;
    private double mediaTam;
    private double mejorFitness;
    private List<Point> mejorRuta;
    private int numWrap;
    private double mayorCantidadComida;
    private double[] comidaRecogida;
    //puede haber más cosas
    private int metodoInicio;
    private int tamTorneo;
    private int minProf;
    private Bloating tipoBloat;
    private int pasos;

    public AlgoritmoGenetico(DTORequest dtoRequest) {
        this.tamPoblacion = dtoRequest.getTammPoblacion();
        this.poblacion = new Individuo[tamPoblacion];
        this.fitness = new double[tamPoblacion];
        this.maxGeneraciones = dtoRequest.getNumGeneraciones();
        this.probCruce = dtoRequest.getCruce();
        this.probMutacion = dtoRequest.getMutacion();
        this.elMejor = null;
        this.tipoSeleccion = dtoRequest.getTipoSseleccion();
        this.metodoInicio = dtoRequest.getInicializacion();
        this.tipoFitness = dtoRequest.getTipoGramaticaOArbol();       // ESTO ES LO Q IDENTIFICA SI ES ARBOL O GRAMATICA
        if(tipoFitness == 0){
            this.tipoCruce =0;
        }
        else this.tipoCruce = 1;
        this.numWrap = dtoRequest.getWraps();
        if(this.tipoFitness == 0) {
            this.tipoMutacion = dtoRequest.getTipoMutacion();
        }
        else this.tipoMutacion = 4;
        this.escalado = dtoRequest.isEscalado();
        this.mediaGeneracion = new double[maxGeneraciones];
        this.mejorGeneracion = new double[maxGeneraciones];
        this.mejorAbsoluto = new double[maxGeneraciones];

        this.pasos = dtoRequest.getNumPasos();
        this.min = false; // siempre maximiza
        this.elitismo = dtoRequest.getElitismo() != 0;
        this.tamElit = this.elitismo ? (int) Math.ceil(tamPoblacion * dtoRequest.getElitismo() / 100) : 0;
        this.elite = new Individuo[tamElit];
        this.numCruzados =0;
        this.numMutados =0;
        this.mediaTam = 0;

        this.MAX_PROFUNDIDAD = dtoRequest.getProfMax();
        this.bloating = dtoRequest.getBloating();//new Bloating(MAX_PROFUNDIDAD);
        if(bloating[0] !=0) tipoBloat = new Bloating(MAX_PROFUNDIDAD);
        this.tams = new int[tamPoblacion];
        this.mejorFitness = 0;
        this.mejorRuta = new ArrayList<>();
        this.distanciaRecorrida = new double[tamPoblacion];
        this.mayorCantidadComida = 0;
        this.comidaRecogida = new double[tamPoblacion];
        this.tamTorneo = dtoRequest.getTamTorneo();
        this.minProf = dtoRequest.getProfMin();
    }

    public DTOResponse run() {
        initPoblacion(); //Iniciar poblacion y evaluar poblacion
        generacionActual = 0;
        this.evaluar(0);
        mediaTam = calcularMediaTam();
        while (generacionActual < this.maxGeneraciones) {

            if (escalado) this.escaladoLineal();

            if (elitismo) seleccionarElite();


            int[] seleccionados = procesoSeleccion(); //Seleccion
            this.procesoCruze(seleccionados); //Cruce
            this.procesoMutacion();

            if (elitismo) reintegrarElite();

            mediaTam = calcularMediaTam();
            this.evaluar(generacionActual);
            if( bloating[1] == 1)  aplicarTarpeian( this.poblacion,mediaTam);


           generacionActual++;
        }

        if(this.tipoFitness == 0) elMejor.setResult(elMejor.obtenerPrograma());

        Pair<Double, Double> interval = IndividuoFactory.getIntervalo(this.tipoFitness);
        return new DTOResponse(this.mediaGeneracion, this.mejorGeneracion, this.mejorAbsoluto, interval, this.elMejor, this.numCruzados, this.numMutados);
    }

    private void initPoblacion() {

        var mut = MutacionFactory.getMutacion(this.tipoMutacion);
        var fit = FitnessFactory.getFitness(this.tipoFitness, this.pasos);
        var map = MapaFactory.getMapa();
        for (int i = 0; i < tamPoblacion; i++) {
            this.poblacion[i] = (Individuo<T>) IndividuoFactory.getIndividuo(map,fit,mut,MAX_PROFUNDIDAD,this.tipoFitness,this.metodoInicio,this.minProf,this.numWrap);
            if(this.tipoFitness == 0) {
                ((Arbol) poblacion[i].getCromosoma()).actualizaTotalNodo();
                tams[i] = ((Arbol) poblacion[i].getCromosoma()).getTotalNodos();
            }
            else{
                poblacion[i].getFitness();
                tams[i] =  poblacion[i].getTamGenes();
            }
        }

        this.elMejor = this.poblacion[0];
        this.elMejor.getFitness();
    }

    private int[] procesoSeleccion() {
        double fit = this.min ? Arrays.stream(fitness).max().orElse(0) : Arrays.stream(fitness).min().orElse(0);
        Seleccion sel = SeleccionFactory.getAlgSeleccion(this.tipoSeleccion, this.fitness, this.tamPoblacion, this.min, fit,tamTorneo);
        assert sel != null;

        int[] seleccionados = sel.getSeleccion();

        return seleccionados;
    }

    private void procesoCruze(int[] seleccionados) {

        Cruce cruce = CruceFactory.getCruce(this.tipoCruce, this.elMejor.getTamGenes(), bloating[3]);
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
            T c1 = this.poblacion[seleccionadosCruce[i]].getCromosoma();
            T c2 = this.poblacion[seleccionadosCruce[i + 1]].getCromosoma();
            cruce.cruzar(c1, c2,this.tipoBloat);
            this.poblacion[seleccionadosCruce[i]].setCromosoma(c1);
            this.poblacion[seleccionadosCruce[i + 1]].setCromosoma(c2);
            if(this.tipoFitness == 0) {
                tams[seleccionadosCruce[i]] = ((Arbol) poblacion[seleccionadosCruce[i]].getCromosoma()).getTotalNodos();
                tams[seleccionadosCruce[i + 1]] = ((Arbol) poblacion[seleccionadosCruce[i + 1]].getCromosoma()).getTotalNodos();
            }
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

    private void evaluar(int generacion) {
        // Inicialización para maximización
        this.fitnessTotal = 0;
        double mejorGeneracion = Double.NEGATIVE_INFINITY; // cambio para maximización
        double covarianza = 0,varianza = 0;
        if(this.tipoFitness == 0) {
            // Pre-cálculo de estadísticas
            double mediaTam = calcularMediaTam();
            varianza = calcularVarianza(mediaTam);
            covarianza = calcularCovarianza(mediaTam);
        }
        for (int i = 0; i < tamPoblacion; i++) {
            double f = this.poblacion[i].getFitness();

            int comidaRecolectada = (int)f;
            comidaRecogida[i] = comidaRecolectada;
            int comidaTotal = 89;
            int pasosTotales = this.poblacion[i].getPasos().getFirst();
            int pasosVacios = this.poblacion[i].getPasos().getSecond();
            distanciaRecorrida[i] = pasosTotales;

            f = comidaRecolectada;
            if (this.tipoFitness == 0 && bloating[2] == 1) {
                f = tipoBloat.penalizarFitness((Individuo<Arbol>)poblacion[i], calcularMediaTam(), covarianza, varianza);
            }
            // bonificación fuerte si alcanza todo
           // if(f == 89) f*=100;

            if(Double.isNaN(f)) f=0;
            this.fitness[i] = f;
            fitnessTotal += f;

            if (mejorGeneracion < f) {
                mejorGeneracion = f;
            }

                if (f > this.mejorFitness) {
                    elMejor = this.poblacion[i].clone();
                    mejorFitness = f;
                    mejorRuta = this.poblacion[i].getRuta();
                }

        }

        this.mejorGeneracion[generacionActual] = mejorGeneracion;
        this.mejorAbsoluto[generacionActual] = this.mejorFitness;
        this.mediaGeneracion[generacionActual] = fitnessTotal / tamPoblacion;
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

        //
        Arrays.sort(indices, (i,j) -> Double.compare(fitness[j],fitness[i]));

        for (int i = 0; i < tamElit; i++) {
            elite[i] = poblacion[indices[i]].clone();
        }
    }


    private void reintegrarElite() {

        Integer[] indices = new Integer[tamPoblacion];
        for (int i = 0; i < tamPoblacion; i++) {
            indices[i] = i;
        }

        Arrays.sort(indices, (i, j) -> Double.compare(fitness[i], fitness[j]));

        for (int i = 0; i < tamElit; i++) {
            int idxWorst = indices[i]; // los primeros son los peores según el criterio usado
            poblacion[idxWorst] = elite[i].clone();
            fitness[idxWorst] = elite[i].getFitness(); // actualizamos también el fitness
        }
    }

    private double calcularMediaTam(){
        double sum = 0;
        for(int i = 0; i < tamPoblacion; i++){
            //tams[i] = ((Arbol) poblacion[i].getCromosoma()).getTotalNodos();
            if(this.tipoFitness == 1) tams[i] = poblacion[i].getTamGenes();
            sum += tams[i];
        }
        return sum / tamPoblacion;
    }

    //mide la dispersion del tamaño de los arboles con respecto a su tamaño promedio
    private double calcularVarianza(double mediaTam){
        return Arrays.stream(tams).mapToDouble(tam->{
            double diferencia = tam - mediaTam;
            return diferencia * diferencia;
        }).average().orElse(0);
    }

    //mide la relacion entre el tam d los arboles y el fitness. indica si los atboles mas grandes tienden a tener un mejor o peor fitness
    private double calcularCovarianza(double mediaTam){
        double mediaFitness = getMediaGeneracion();
        double sum = 0;
        for(int i=0; i <poblacion.length; i++){
            double tamaño = tams[i];
            sum+= (tamaño-mediaTam) * (fitness[i]-mediaFitness);
        }
        return sum/poblacion.length;
    }


    public void aplicarTarpeian(Individuo<T>[] poblacion,double mediaTam) {
        for (int i = 0; i < poblacion.length; i++) {
            if(Math.random() < 0.5 && tams[i] > mediaTam) {
                fitness[i] -= fitness[i] * 0.6;
            }
        }
    }
}


