package main.ventana;

import main.individuo.Individuo;
import main.mapa.Casa;
import main.utils.Direccion;
import main.utils.Estado;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MapaPanel que dibuja una cuadrícula de 32×32, el rastro de comida
 * y el camino de la hormiga con colores mejorados.
 */
public class MapaPanel extends JPanel {
    private static final int GRID_SIZE = 32;
    private Casa mapa;
    private Individuo<?> mejorIndividuo;
    private List<Point> comidaConsumida = new ArrayList<>();

    // Colores mejorados con mejor contraste
    private static final Color COLOR_FONDO = new Color(240, 248, 255); // AliceBlue
    private static final Color COLOR_GRID = new Color(220, 220, 220);  // Gris claro
    private static final Color COLOR_COMIDA = new Color(34, 139, 34);  // Verde bosque
    private static final Color COLOR_RASTRO = new Color(65, 105, 225); // Azul real
    private static final Color COLOR_COMIDA_CONSUMIDA = new Color(220, 20, 60); // Rojo carmesí
    private static final Color COLOR_POSICION_INICIAL = new Color(255, 140, 0); // Naranja oscuro

    public MapaPanel(Casa mapa) {
        this.mapa = mapa;
        setBackground(COLOR_FONDO);
    }

    /**
     * Actualiza la ruta del individuo y calcula la comida consumida.
     */
    public void setRuta(Individuo<?> ind) {
        this.mejorIndividuo = ind.clone();

        // Calcular comida consumida
        // Calcular comida consumida
        comidaConsumida.clear();
        List<Point> ruta = mejorIndividuo.getRuta();
        if (ruta != null) {
            Casa casaTemp = mapa.clone(); // Clonar el mapa para no modificar el original

            // Comprobar si hay comida en la posición inicial
            Point inicial = ruta.get(0);
            if (casaTemp.hayComida(inicial)) {
                comidaConsumida.add(new Point(inicial));
                casaTemp.comerComida(new Estado(inicial.x, inicial.y, Direccion.ESTE));
            }

            // Recorrer toda la ruta y marcar comida consumida
            for (Point punto : ruta) {
                if (casaTemp.hayComida(punto)) {
                    comidaConsumida.add(new Point(punto));
                    casaTemp.comerComida(new Estado(punto.x, punto.y, Direccion.ESTE));
                }
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Configuración inicial y renderizado anti-aliasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int cellSize = Math.min(panelWidth / GRID_SIZE, panelHeight / GRID_SIZE);
        int boardWidth = cellSize * GRID_SIZE;
        int boardHeight = cellSize * GRID_SIZE;
        int xOffset = (panelWidth - boardWidth) / 2;
        int yOffset = (panelHeight - boardHeight) / 2;

        // Dibujar fondo
        g2.setColor(COLOR_FONDO);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        // Dibujar cuadrícula
        g2.setColor(COLOR_GRID);
        for (int fila = 0; fila <= GRID_SIZE; fila++) {
            g2.drawLine(xOffset, yOffset + fila * cellSize,
                    xOffset + boardWidth, yOffset + fila * cellSize);
        }
        for (int col = 0; col <= GRID_SIZE; col++) {
            g2.drawLine(xOffset + col * cellSize, yOffset,
                    xOffset + col * cellSize, yOffset + boardHeight);
        }

        // Obtener todas las posiciones de comida
        List<Point> comidaTotal = new ArrayList<>(mapa.getFood());

        // Filtrar la comida que aún no ha sido consumida
        List<Point> comidaRestante = new ArrayList<>(comidaTotal);
        comidaRestante.removeAll(comidaConsumida);

        // Dibujar comida no consumida (verde)
        g2.setColor(COLOR_COMIDA);
        for (Point foodPos : comidaRestante) {
            int x = xOffset + foodPos.x * cellSize;
            int y = yOffset + foodPos.y * cellSize;
            g2.fillOval(x + cellSize/4, y + cellSize/4, cellSize/2, cellSize/2);
        }

        // Dibujar el camino de la hormiga
        if (mejorIndividuo != null) {
            List<Point> ruta = mejorIndividuo.getRuta();
            if (ruta != null && ruta.size() > 1) {
                // Línea de recorrido
                g2.setColor(COLOR_RASTRO);
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 0; i < ruta.size() - 1; i++) {
                    Point current = ruta.get(i);
                    Point next = ruta.get(i + 1);
                    int cx = xOffset + current.x * cellSize + cellSize / 2;
                    int cy = yOffset + current.y * cellSize + cellSize / 2;
                    int nx = xOffset + next.x * cellSize + cellSize / 2;
                    int ny = yOffset + next.y * cellSize + cellSize / 2;
                    g2.drawLine(cx, cy, nx, ny);
                }

                // Dibujar puntos en la ruta para mostrar claramente el camino
                for (int i = 1; i < ruta.size() - 1; i++) {
                    Point point = ruta.get(i);
                    int px = xOffset + point.x * cellSize + cellSize / 2;
                    int py = yOffset + point.y * cellSize + cellSize / 2;
                    g2.fillOval(px - 3, py - 3, 6, 6);
                }

                // Dibujar comida consumida (rojo)
                g2.setColor(COLOR_COMIDA_CONSUMIDA);
                for (Point comida : comidaConsumida) {
                    int x = xOffset + comida.x * cellSize;
                    int y = yOffset + comida.y * cellSize;
                    g2.fillOval(x + cellSize/4, y + cellSize/4, cellSize/2, cellSize/2);
                }

                // Dibujar posición inicial y final de la hormiga
                if (!ruta.isEmpty()) {
                    // Posición inicial
                    Point inicio = ruta.get(0);
                    int x = xOffset + inicio.x * cellSize;
                    int y = yOffset + inicio.y * cellSize;
                    g2.setColor(COLOR_POSICION_INICIAL);
                    g2.fillRect(x + cellSize/4, y + cellSize/4, cellSize/2, cellSize/2);

                    // Posición final
                    Point fin = ruta.get(ruta.size() - 1);
                    x = xOffset + fin.x * cellSize;
                    y = yOffset + fin.y * cellSize;
                    g2.setColor(COLOR_RASTRO.darker());
                    g2.fillRect(x + cellSize/4, y + cellSize/4, cellSize/2, cellSize/2);
                }
            }
        }

        // Añadir leyenda
        g2.setFont(new Font("Dialog", Font.PLAIN, 12));
        int legendY = yOffset + boardHeight + 20;

        // Comida
        g2.setColor(COLOR_COMIDA);
        g2.fillOval(xOffset, legendY, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawString("Comida", xOffset + 15, legendY + 10);

        // Comida consumida
        g2.setColor(COLOR_COMIDA_CONSUMIDA);
        g2.fillOval(xOffset + 100, legendY, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawString("Comida consumida", xOffset + 115, legendY + 10);

        // Camino de la hormiga
        g2.setColor(COLOR_RASTRO);
        g2.drawLine(xOffset + 250, legendY + 5, xOffset + 280, legendY + 5);
        g2.setColor(Color.BLACK);
        g2.drawString("Camino", xOffset + 285, legendY + 10);

        // Posición inicial
        g2.setColor(COLOR_POSICION_INICIAL);
        g2.fillRect(xOffset + 370, legendY, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawString("Inicio", xOffset + 385, legendY + 10);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}