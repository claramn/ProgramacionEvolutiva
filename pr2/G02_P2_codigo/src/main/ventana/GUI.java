package main.ventana;

import main.algoritmo.AlgoritmoGenetico;
import main.individuo.Individuo;
import main.mapa.MapaFactory;
import org.math.plot.Plot2DPanel;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private final String[] seleccion = {"Ruleta", "Torneo determinístico", "Torneo probabilístico", "Estocastico Universal", "Truncamiento", "Restos", "Ranking"};
    private final String[] mutacion = {"Inserccion", "Intercambio", "Inversion", "Heuristica", "Invencion Propia (*) "};
    private final String[] fittness = {"Basico", "Penalización por proximidad a obstáculos", "Penalización por cambios de dirección", "Uso de una heurística más sofisticada", "Costo de velocidad o tiempo", "Comparación con la distancia euclidiana", "Multicriterio (Multiobjetivo)"};
    private final String[] cruces = {"Emparejamiento parcial (PMX)", "Cruce por Orden (OX)", "Cruce por orden con posiciones prioritarias (OXPP)", "Ciclos (CX)", "Codificación Ordinal (CO)", "Recombinación de rutas (ERX)", "Invencion Propia(*)"};
    private JComboBox<String> fittness_combo, seleccion_combo, cruce_combo, seleccion_mut;
    private JSpinner poblacion, generacion, cruce_spin, muta_spin, elitismo_spin;
    private JCheckBox elitismoCheck, escaladoCheck;
    private JButton run;
    private JTextArea resultsArea;
    private JPanel leftPanel, resultPanel;
    // Pestañas para las vistas: gráfica y mapa
    private JTabbedPane tabbedPane;
    private Plot2DPanel plot2D;
    private MapaPanel mapaPanel;

    public GUI() {
        super("INICIO");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        Font font = new Font("SansSerif", Font.BOLD, 14);
        leftPanel = new JPanel(new GridLayout(13, 2, 5, 5));
        fittness_combo = new JComboBox<>(fittness);
        fittness_combo.setFont(font);

        poblacion = new JSpinner(new SpinnerNumberModel(100, 0, 1000, 1));
        generacion = new JSpinner(new SpinnerNumberModel(100, 0, 1000, 1));
        cruce_spin = new JSpinner(new SpinnerNumberModel(60, 0, 100, 1));
        muta_spin = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1));
        elitismo_spin = new JSpinner(new SpinnerNumberModel(2, 0, 100, 1));
        elitismo_spin.setEnabled(false);
        seleccion_combo = new JComboBox<>(seleccion);
        cruce_combo = new JComboBox<>(cruces);
        seleccion_mut = new JComboBox<>(mutacion);
        elitismoCheck = new JCheckBox("Usar elitismo");
        elitismoCheck.addItemListener(e -> elitismo_spin.setEnabled(elitismoCheck.isSelected()));
        escaladoCheck = new JCheckBox("Usar escalado");

        poblacion.setFont(font);
        generacion.setFont(font);
        cruce_spin.setFont(font);
        muta_spin.setFont(font);
        seleccion_combo.setFont(font);
        cruce_combo.setFont(font);
        seleccion_mut.setFont(font);
        fittness_combo.setFont(font);
        elitismoCheck.setFont(font);
        escaladoCheck.setFont(font);

        leftPanel = new JPanel(new BorderLayout(5, 5));
        JPanel configPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        configPanel.add(new JLabel("Función:")).setFont(font);
        configPanel.add(fittness_combo);
        configPanel.add(new JLabel("Tamaño población:")).setFont(font);
        configPanel.add(poblacion);
        configPanel.add(new JLabel("Número generaciones:")).setFont(font);
        configPanel.add(generacion);
        configPanel.add(new JLabel("Porcentaje cruce:")).setFont(font);
        configPanel.add(cruce_spin);
        configPanel.add(new JLabel("Porcentaje mutación:")).setFont(font);
        configPanel.add(muta_spin);
        configPanel.add(new JLabel("Método selección:")).setFont(font);
        configPanel.add(seleccion_combo);
        configPanel.add(new JLabel("Método cruce:")).setFont(font);
        configPanel.add(cruce_combo);
        configPanel.add(new JLabel("Método mutación:")).setFont(font);
        configPanel.add(seleccion_mut);
        configPanel.add(elitismoCheck);
        configPanel.add(elitismo_spin);
        configPanel.add(escaladoCheck);

        leftPanel.add(configPanel, BorderLayout.CENTER);


        run = new JButton("Iniciar");
        run.setIcon(new ImageIcon(getClass().getResource("/icons/run.png")));
        run.addActionListener(e -> run());
        leftPanel.add(run, BorderLayout.SOUTH);

        // Inicializar las dos vistas: gráfica y mapa
        plot2D = new Plot2DPanel();
        plot2D.setPreferredSize(new Dimension(600, 600));
        plot2D.getAxis(0).setLabelText("Generación");
        plot2D.getAxis(1).setLabelText("Fitness");

        // Inicializar el mapa con ruta vacía (posteriormente se actualizará)
        mapaPanel = new MapaPanel(MapaFactory.getMapa());

        // Usamos un JTabbedPane para cambiar de vista (al estilo navegador)
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Gráfica", plot2D);
        tabbedPane.addTab("Mapa", mapaPanel);

        resultPanel = new JPanel(new BorderLayout());
        resultsArea = new JTextArea(5, 30);
        resultsArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        resultsArea.setEditable(false);
        resultsArea.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane resultsAreaScrollPane = new JScrollPane(resultsArea);
        resultPanel.add(resultsAreaScrollPane, BorderLayout.CENTER);
        resultPanel.setBorder(BorderFactory.createTitledBorder("Resultados"));

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.add(leftPanel, BorderLayout.CENTER);
        leftContainer.add(resultPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftContainer, tabbedPane);
        splitPane.setDividerLocation(320);

        setContentPane(splitPane);
        pack();
        setVisible(true);
    }

    private void run() {
        try {
            int func = fittness_combo.getSelectedIndex();
            int tamPobla = (Integer) poblacion.getValue();
            int numGeneraciones = (Integer) generacion.getValue();
            double cruc = ((Integer) cruce_spin.getValue()) / 100.0;
            double porcent = ((Integer) muta_spin.getValue()) / 100.0;
            int selec = seleccion_combo.getSelectedIndex();
            int mut = seleccion_mut.getSelectedIndex();
            int cruzSelec = cruce_combo.getSelectedIndex();
            double elitismo = elitismoCheck.isSelected() ? ((Integer) elitismo_spin.getValue()) : 0;

            DTORequest dtoRequest = new DTORequest(func, tamPobla, numGeneraciones, cruc, porcent, selec, mut, cruzSelec, elitismo, escaladoCheck.isSelected());
            AlgoritmoGenetico<Integer> ag = new AlgoritmoGenetico<>(dtoRequest);
            DTOResponse dtoResponse = ag.run();
            actualizaGUI(dtoResponse);

            // Actualizar la ruta en el mapa (se asume que getOptimalRoute() retorna List<Point>)
            if (dtoResponse.getOptimalRoute() != null) {
                actualizarMapa(dtoResponse.elMejor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error en la captura de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizaGUI(DTOResponse dtoResponse) {
        // Actualizar la gráfica
        plot2D.removeAllPlots();
        plot2D.repaint();
        double[] x = new double[dtoResponse.mediaGeneracion.length];
        for (int i = 0; i < dtoResponse.mediaGeneracion.length; i++) {
            x[i] = i;
        }
        plot2D.addLinePlot("Mejor Absoluto", x, dtoResponse.mejorAbsoluto);
        plot2D.addLinePlot("Mejor de la Generacion", x, dtoResponse.mejorGeneracion);
        plot2D.addLinePlot("Media", x, dtoResponse.mediaGeneracion);
        plot2D.getAxis(0).setLabelText("Generacion");
        plot2D.getAxis(1).setLabelText("Fitness");
        plot2D.setFixedBounds(1, dtoResponse.intervalo.getFirst(), dtoResponse.intervalo.getSecond());
        plot2D.addLegend("SOUTH");
        plot2D.setAutoBounds();

        String textoSalida = "Fitness: " + dtoResponse.elMejor.getFitness() + "\n" + "Nº Casillas Recorridas:" + (dtoResponse.elMejor.getRuta().size() - 1) + "\n"+
                "Nº Total de Cruces: " + dtoResponse.numCruce + "\n" + "Nº Total de Mutaciones: " + dtoResponse.numMutaciones + "\n";
        resultsArea.setText(textoSalida);
        int cont = 1;
        for (double cromosoma : dtoResponse.elMejor.getFenotipos()) {
            resultsArea.append("Habitacion " + (cont++) + ": " + cromosoma + "\n");
        }
        resultsArea.setCaretPosition(0);
    }

    private void actualizarMapa(Individuo ind) {
        if (mapaPanel != null) {
            mapaPanel.setRuta(ind);
        }
    }
}
