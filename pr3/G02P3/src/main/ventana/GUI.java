package main.ventana;

import main.algoritmo.AlgoritmoGenetico;
import main.individuo.Individuo;
import main.mapa.MapaFactory;
import org.math.plot.Plot2DPanel;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;

public class GUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private final String[] tipoRepresentacion = {"Árbol", "Gramática"};
    private final String[] seleccion = {"Ruleta", "Torneo determinístico", "Torneo probabilístico", "Estocastico Universal", "Truncamiento", "Restos", "Ranking"};
    private final String[] mutacionArbol = {"Terminal", "Funcional", "Mutación de subArbol", "Mutacion de Permutacion"};
    private final String[] mutacionGramatica = {"Uniforme", "Monopunto"};
    private final String[] metodo_inicializacion = {"Completa", "Creciente", "Ramped and Half"};
    private final String[] cruces = {"Intercambio de Subarboles"};
    private final String[] bloatingOptions = {"Metodo Tarpeian", "Penalizacion bien fundamentada", "poda de arboles"};

    private JComboBox<String> tipoRepresentacion_combo, inicializacion_combo, seleccion_combo, seleccion_mut, cruces_combo;
    private JSpinner poblacion, generacion, cruce_spin, muta_spin, elitismo_spin, pasos_spin, profundidadMin_spin, profundidadMax_spin,tamTorneo_spin, wraps_spin;
    private JCheckBox bloatingCheck, elitismoCheck, escaladoCheck;
    private JCheckBox[] bloatingOptionChecks;
    private JButton run;
    private JTextArea resultsArea;
    private JPanel leftPanel, resultPanel;
    private JTabbedPane tabbedPane;
    private Plot2DPanel plot2D;
    private MapaPanel mapaPanel;
    private JSplitPane mainSplitPane;
    private JSplitPane leftSplitPane;
    private JLabel tamTorneoLabel, wrapsLabel, profundidadMinLabel, profundidadMaxLabel, inicializacionLabel;

    public GUI() {
        super("Programación Evolutiva - Problema de la Hormiga");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        // Configuración de fuentes y colores
        Font labelFont = new Font("Dialog", Font.BOLD, 13);
        Font controlFont = new Font("Dialog", Font.PLAIN, 13);
        Font buttonFont = new Font("Dialog", Font.BOLD, 14);
        Color panelColor = new Color(240, 240, 245);
        Color buttonColor = new Color(70, 130, 180);

        // Panel de controles
        leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(panelColor);

        // Panel que contiene los controles con un GridBagLayout para mejor control
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(panelColor);
        configPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;

        // Inicialización de componentes
        tipoRepresentacion_combo = createStyledComboBox(tipoRepresentacion, controlFont);
        tipoRepresentacion_combo.addActionListener(e -> updateComponentVisibility());

        inicializacion_combo = createStyledComboBox(metodo_inicializacion, controlFont);
        poblacion = createStyledSpinner(new SpinnerNumberModel(100, 10, 1000, 10), controlFont);
        generacion = createStyledSpinner(new SpinnerNumberModel(100, 10, 1000, 10), controlFont);
        cruce_spin = createStyledSpinner(new SpinnerNumberModel(60, 0, 100, 5), controlFont);
        muta_spin = createStyledSpinner(new SpinnerNumberModel(5, 0, 100, 1), controlFont);
        elitismo_spin = createStyledSpinner(new SpinnerNumberModel(2, 0, 100, 1), controlFont);
        pasos_spin = createStyledSpinner(new SpinnerNumberModel(400, 100, 1000, 50), controlFont);
        profundidadMin_spin = createStyledSpinner(new SpinnerNumberModel(2, 1, 4, 1), controlFont);
        profundidadMax_spin = createStyledSpinner(new SpinnerNumberModel(4, 2, 8, 1), controlFont);
        tamTorneo_spin = createStyledSpinner(new SpinnerNumberModel(3, 3, 10, 1), controlFont);
        wraps_spin = createStyledSpinner(new SpinnerNumberModel(3, 1, 10, 1), controlFont);
        elitismo_spin.setEnabled(false);
        tamTorneo_spin.setEnabled(false);

        seleccion_combo = createStyledComboBox(seleccion, controlFont);
        seleccion_combo.addActionListener(e -> {
            int selectedIndex = seleccion_combo.getSelectedIndex();
            tamTorneo_spin.setEnabled(selectedIndex == 1 || selectedIndex == 2);
            tamTorneoLabel.setEnabled(selectedIndex == 1 || selectedIndex == 2);
        });

        seleccion_mut = createStyledComboBox(mutacionArbol, controlFont);
        cruces_combo = createStyledComboBox(cruces, controlFont);

        // CheckBox para Bloating
        bloatingCheck = new JCheckBox("Usar Bloating");
        bloatingCheck.setFont(controlFont);
        bloatingCheck.setBackground(panelColor);
        bloatingCheck.addItemListener(e -> {
            boolean selected = bloatingCheck.isSelected();
            for (JCheckBox option : bloatingOptionChecks) {
                option.setEnabled(selected);
            }
        });

        // Opciones de Bloating
        bloatingOptionChecks = new JCheckBox[bloatingOptions.length];
        for (int i = 0; i < bloatingOptions.length; i++) {
            bloatingOptionChecks[i] = new JCheckBox(bloatingOptions[i]);
            bloatingOptionChecks[i].setFont(controlFont);
            bloatingOptionChecks[i].setBackground(panelColor);
            bloatingOptionChecks[i].setEnabled(false);
        }

        elitismoCheck = new JCheckBox("Usar elitismo");
        elitismoCheck.setFont(controlFont);
        elitismoCheck.setBackground(panelColor);
        elitismoCheck.addItemListener(e ->
                elitismo_spin.setEnabled(elitismoCheck.isSelected()));

        escaladoCheck = new JCheckBox("Usar escalado");
        escaladoCheck.setFont(controlFont);
        escaladoCheck.setBackground(panelColor);

        // Crear etiquetas para poder habilitar/deshabilitar
        profundidadMinLabel = new JLabel("Profundidad Mínima:");
        profundidadMaxLabel = new JLabel("Profundidad Máxima:");
        tamTorneoLabel = new JLabel("Tamaño del torneo:");
        wrapsLabel = new JLabel("Número de wraps:");
        inicializacionLabel = new JLabel("Método Inicialización:");

        // Configurar fuentes para las etiquetas
        profundidadMinLabel.setFont(labelFont);
        profundidadMaxLabel.setFont(labelFont);
        tamTorneoLabel.setFont(labelFont);
        wrapsLabel.setFont(labelFont);
        inicializacionLabel.setFont(labelFont);

        // Añadir componentes con GridBagLayout
        addLabelAndComponentGBC(configPanel, "Tipo de representación:", tipoRepresentacion_combo, labelFont, gbc, 0);

        // Método de inicialización (solo para árbol)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.3;
        configPanel.add(inicializacionLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        configPanel.add(inicializacion_combo, gbc);

        // Profundidad mínima (solo para árbol)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        configPanel.add(profundidadMinLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        configPanel.add(profundidadMin_spin, gbc);

        // Profundidad máxima (solo para árbol)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        configPanel.add(profundidadMaxLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        configPanel.add(profundidadMax_spin, gbc);

        // Wraps (solo para gramática)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        configPanel.add(wrapsLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        configPanel.add(wraps_spin, gbc);
        wrapsLabel.setVisible(false);
        wraps_spin.setVisible(false);

        addLabelAndComponentGBC(configPanel, "Tamaño población:", poblacion, labelFont, gbc, 5);
        addLabelAndComponentGBC(configPanel, "Número generaciones:", generacion, labelFont, gbc, 6);
        addLabelAndComponentGBC(configPanel, "Porcentaje cruce:", cruce_spin, labelFont, gbc, 7);
        addLabelAndComponentGBC(configPanel, "Porcentaje mutación:", muta_spin, labelFont, gbc, 8);
        addLabelAndComponentGBC(configPanel, "Método selección:", seleccion_combo, labelFont, gbc, 9);

        // Tamaño torneo
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.weightx = 0.3;
        configPanel.add(tamTorneoLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        configPanel.add(tamTorneo_spin, gbc);

        addLabelAndComponentGBC(configPanel, "Método cruce:", cruces_combo, labelFont, gbc, 11);
        addLabelAndComponentGBC(configPanel, "Método mutación:", seleccion_mut, labelFont, gbc, 12);
        addLabelAndComponentGBC(configPanel, "Número pasos:", pasos_spin, labelFont, gbc, 13);

        // Bloating CheckBox
        gbc.gridx = 0;
        gbc.gridy = 14;
        configPanel.add(bloatingCheck, gbc);

        // Opciones de Bloating
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.gridwidth = 2;
        JPanel bloatingOptionsPanel = new JPanel(new GridLayout(bloatingOptions.length, 1));
        bloatingOptionsPanel.setBackground(panelColor);
        for (JCheckBox option : bloatingOptionChecks) {
            bloatingOptionsPanel.add(option);
        }
        configPanel.add(bloatingOptionsPanel, gbc);
        gbc.gridwidth = 1;

        // Agregar checkboxes
        gbc.gridx = 0;
        gbc.gridy = 16;
        configPanel.add(elitismoCheck, gbc);

        gbc.gridx = 1;
        configPanel.add(elitismo_spin, gbc);

        gbc.gridx = 0;
        gbc.gridy = 17;
        configPanel.add(escaladoCheck, gbc);

        // Resto del código sin cambios...

        // Añadir panel de configuración con scroll
        JScrollPane configScrollPane = new JScrollPane(configPanel);
        configScrollPane.setBorder(null);
        configScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        leftPanel.add(configScrollPane, BorderLayout.CENTER);

        // Botón de ejecución
        run = new JButton("Iniciar");
        run.setFont(buttonFont);
        run.setIcon(new ImageIcon(getClass().getResource("/icons/run.png")));
        run.setBackground(buttonColor);
        run.setForeground(Color.BLACK);
        run.setFocusPainted(false);
        run.setBorder(new EmptyBorder(8, 15, 8, 15));
        run.setCursor(new Cursor(Cursor.HAND_CURSOR));
        run.addActionListener(e -> run());

        // Efecto hover para el botón
        run.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                run.setBackground(buttonColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                run.setBackground(buttonColor);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setBackground(panelColor);
        buttonPanel.add(run);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel de resultados
        resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(panelColor);
        resultsArea = new JTextArea(8, 30);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultsArea.setEditable(false);
        resultsArea.setMargin(new Insets(10, 10, 10, 10));
        resultsArea.setBackground(new Color(252, 252, 252));

        JScrollPane resultsAreaScrollPane = new JScrollPane(resultsArea);
        resultsAreaScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        resultPanel.add(resultsAreaScrollPane, BorderLayout.CENTER);
        resultPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Resultados",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Dialog", Font.BOLD, 13)
        ));

        // Inicializar las vistas: gráfica y mapa
        plot2D = new Plot2DPanel();
        plot2D.setPreferredSize(new Dimension(600, 600));
        plot2D.getAxis(0).setLabelText("Generación");
        plot2D.getAxis(1).setLabelText("Fitness");
        plot2D.setBackground(Color.WHITE);

        // Inicializar el mapa
        mapaPanel = new MapaPanel(MapaFactory.getMapa());
        mapaPanel.setPreferredSize(new Dimension(600, 600));

        // Panel con pestañas para las vistas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Dialog", Font.PLAIN, 13));
        tabbedPane.addTab("Gráfica", plot2D);
        tabbedPane.addTab("Mapa", mapaPanel);

        // Estructura de paneles divididos para redimensionamiento adecuado
        leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPanel, resultPanel);
        leftSplitPane.setDividerLocation(450);
        leftSplitPane.setResizeWeight(0.0); // El panel de resultados crece al redimensionar
        leftSplitPane.setDividerSize(5);
        leftSplitPane.setContinuousLayout(true);

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, tabbedPane);
        mainSplitPane.setDividerLocation(350); // Un poco más ancho para que se vean bien los controles
        mainSplitPane.setResizeWeight(0.0);
        mainSplitPane.setDividerSize(5);
        mainSplitPane.setContinuousLayout(true);

        setContentPane(mainSplitPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Actualizar visibilidad inicial
        updateComponentVisibility();
    }

    private void updateComponentVisibility() {
        boolean isArbol = tipoRepresentacion_combo.getSelectedIndex() == 0;

        // Actualizar componentes de inicialización (solo para árbol)
        inicializacionLabel.setVisible(isArbol);
        inicializacion_combo.setVisible(isArbol);

        // Actualizar componentes de profundidad (solo para árbol)
        profundidadMinLabel.setVisible(isArbol);
        profundidadMin_spin.setVisible(isArbol);
        profundidadMaxLabel.setVisible(isArbol);
        profundidadMax_spin.setVisible(isArbol);

        // Actualizar componentes de wraps (solo para gramática)
        wrapsLabel.setVisible(!isArbol);
        wraps_spin.setVisible(!isArbol);

        // Actualizar métodos de mutación
        seleccion_mut.removeAllItems();
        if (isArbol) {
            for (String item : mutacionArbol) {
                seleccion_mut.addItem(item);
            }
        } else {
            for (String item : mutacionGramatica) {
                seleccion_mut.addItem(item);
            }
        }
    }

    // Métodos auxiliares sin cambios...
    private JComboBox<String> createStyledComboBox(String[] items, Font font) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(font);
        comboBox.setBackground(Color.WHITE);
        ((JLabel)comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        return comboBox;
    }

    private JSpinner createStyledSpinner(SpinnerModel model, Font font) {
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(font);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();
            textField.setBackground(Color.WHITE);
            textField.setFont(font);
            textField.setHorizontalAlignment(JTextField.LEFT);
        }
        return spinner;
    }

    private void addLabelAndComponentGBC(JPanel panel, String labelText, JComponent component, Font labelFont, GridBagConstraints gbc, int rowIndex) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setHorizontalAlignment(SwingConstants.LEFT);

        gbc.gridx = 0;
        gbc.gridy = rowIndex;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }

    private void run() {
        try {
            int tipoRep = tipoRepresentacion_combo.getSelectedIndex();
            int func = tipoRep == 0 ? inicializacion_combo.getSelectedIndex() : 0; // Default to 0 for Gramática
            int tamPobla = (Integer) poblacion.getValue();
            int numGeneraciones = (Integer) generacion.getValue();
            double cruc = ((Integer) cruce_spin.getValue()) / 100.0;
            double porcent = ((Integer) muta_spin.getValue()) / 100.0;
            int selec = seleccion_combo.getSelectedIndex();
            int mut = seleccion_mut.getSelectedIndex();
            double elitismo = elitismoCheck.isSelected() ? ((Integer) elitismo_spin.getValue()) : 0;
            boolean escalado = escaladoCheck.isSelected();
            int numPasos = (Integer) pasos_spin.getValue();
            int profMin = tipoRep == 0 ? (Integer) profundidadMin_spin.getValue() : 0;
            int profMax = tipoRep == 0 ? (Integer) profundidadMax_spin.getValue() : 0;
            int tamTorneo = (Integer) tamTorneo_spin.getValue();
            int wraps = tipoRep == 1 ? (Integer) wraps_spin.getValue() : 0;

            // Determinar el método de bloating seleccionado
            int[] bloating = new int[4]; // Por defecto "No Bloating"
            if (bloatingCheck.isSelected()) {
                bloating[0] = 1;
                for (int i = 0; i < bloatingOptionChecks.length; i++) {
                    if (bloatingOptionChecks[i].isSelected()) {
                        bloating[i+1] = 1; // +1 porque 0 es "No Bloating"
                        break;
                    }
                }
            }

            run.setEnabled(false);
            run.setText("Ejecutando...");
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            int[] finalBloating = bloating;
            SwingWorker<DTOResponse, Void> worker = new SwingWorker<>() {
                @Override
                protected DTOResponse doInBackground() throws Exception {
                    DTORequest dtoRequest = new DTORequest(tipoRep, func,
                            tamPobla, numGeneraciones, cruc, porcent, selec, mut, finalBloating,
                            elitismo, escalado, numPasos, profMin, profMax, tamTorneo, wraps);
                    AlgoritmoGenetico<Integer> ag = new AlgoritmoGenetico<>(dtoRequest);
                    return ag.run();
                }

                @Override
                protected void done() {
                    try {
                        DTOResponse dtoResponse = get();
                        actualizaGUI(dtoResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(GUI.this, "Error durante la ejecución: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        run.setEnabled(true);
                        run.setText("Iniciar");
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };

            worker.execute();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error en la captura de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
        plot2D.addLinePlot("Mejor Absoluto", Color.RED, x, dtoResponse.mejorAbsoluto);
        plot2D.addLinePlot("Mejor de la Generación", Color.BLUE, x, dtoResponse.mejorGeneracion);
        plot2D.addLinePlot("Media", Color.GREEN, x, dtoResponse.mediaGeneracion);
        plot2D.getAxis(0).setLabelText("Generación");
        plot2D.getAxis(1).setLabelText("Fitness");
        plot2D.setAutoBounds();
        plot2D.setFixedBounds(1, dtoResponse.intervalo.getFirst(), dtoResponse.intervalo.getSecond());
        plot2D.addLegend("SOUTH");
        plot2D.setAutoBounds();

        // Actualizar el área de resultados con formato mejorado
        StringBuilder sb = new StringBuilder();
        sb.append("RESULTADOS DE LA EJECUCIÓN\n");
        sb.append("==========================\n\n");
        sb.append(String.format("Fitness: %.2f\n", dtoResponse.elMejor.getFitness()));
        sb.append(String.format("Nº Casillas Recorridas: %d\n", dtoResponse.elMejor.getRuta().size() - 1));
        sb.append(String.format("Nº Total de Cruces: %d\n", dtoResponse.numCruce));
        sb.append(String.format("Nº Total de Mutaciones: %d\n\n", dtoResponse.numMutaciones));
        sb.append("Cromosoma del individuo:\n");
        sb.append(dtoResponse.elMejor.getCromosoma().toString());
        sb.append(dtoResponse.elMejor.toString());
        resultsArea.setText(sb.toString());
        resultsArea.setCaretPosition(0);

        // Actualizar el mapa
        actualizarMapa(dtoResponse.elMejor);
    }

    private void actualizarMapa(Individuo ind) {
        if (mapaPanel != null) {
            mapaPanel.setRuta(ind);
        }
    }
}