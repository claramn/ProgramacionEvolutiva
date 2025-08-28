package main.ventana;

import main.individuo.Individuo;
import main.mapa.Mapa;
import main.utils.Arrow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MapaPanel que dibuja la cuadrícula, obstáculos, habitaciones y la ruta
 * celda a celda, pintando cada celda de la ruta con un pequeño rectángulo.
 */
public class MapaPanel extends JPanel {
    private static final int GRID_SIZE = 15;  // Cuadrícula de 15x15
    private Mapa mapa;
    private Individuo<?> mejorIndividuo; // Para obtener la ruta

    private Map roomsOrderMap = null;

    private Map<Point, List<Arrow>> cellArrows;

    public MapaPanel(Mapa map) {
        this.mapa = map;
        this.cellArrows = new HashMap<>();
    }

    /**
     * Método para actualizar el individuo y forzar repintado.
     */

    public void setRuta(Individuo ind) {
        this.mejorIndividuo = ind.clone();
        roomsOrderMap = ind.getRoomsVisitOrder();
        cellArrows.clear(); // Reiniciar
        // Recorremos la ruta y para cada par consecutivo (p1 -> p2) creamos la flecha
        for (int i = 0; i < mejorIndividuo.getRuta().size() - 1; i++) {
            Point p1 = mejorIndividuo.getRuta().get(i);
            Point p2 = mejorIndividuo.getRuta().get(i + 1);
            // Orden de visita = i+1 (o como quieras numerarlo)
            int order = i + 1;
            Arrow arrow = createArrow(p1, p2, order);

            // Añadir la flecha a la celda p1
            cellArrows.computeIfAbsent(p1, k -> new ArrayList<>()).add(arrow);
        }
        repaint();
    }

    /**
     * Crea una flecha a partir de p1 -> p2 y un número de orden.
     */
    private Arrow createArrow(Point p1, Point p2, int order) {
        int dx = p2.y - p1.y;  // y = columna
        int dy = p2.x - p1.x;  // x = fila
        return new Arrow(dx, dy, order);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int cellSize = Math.min(panelWidth / GRID_SIZE, panelHeight / GRID_SIZE);

        int boardWidth = cellSize * GRID_SIZE;
        int boardHeight = cellSize * GRID_SIZE;
        int xOffset = (panelWidth - boardWidth) / 2;
        int yOffset = (panelHeight - boardHeight) / 2;

        // Fondo degradado
        GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, panelWidth, panelHeight, new Color(255, 255, 200));
        g2.setPaint(gp);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        // Dibujar cuadrícula
        g2.setColor(new Color(220, 220, 220));
        for (int fila = 0; fila < GRID_SIZE; fila++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int x = xOffset + col * cellSize;
                int y = yOffset + fila * cellSize;
                g2.drawRect(x, y, cellSize, cellSize);
            }
        }

        // Dibujar obstáculos (color naranja)
        g2.setColor(new Color(255, 102, 0));
        for (Point p : mapa.getObstaculos()) {
            int x = xOffset + p.y * cellSize;
            int y = yOffset + p.x * cellSize;
            g2.fillRect(x, y, cellSize, cellSize);
        }

        // Dibujar habitaciones (óvalos azules con identificador)
        for (Integer id : mapa.getHabitaciones().keySet()) {
            Point pos = mapa.getHabitaciones().get(id);
            int x = xOffset + pos.y * cellSize;
            int y = yOffset + pos.x * cellSize;
            g2.setColor(new Color(173, 216, 230));
            int margin = cellSize / 10;
            g2.fillOval(x + margin, y + margin, cellSize - 2 * margin, cellSize - 2 * margin);
            g2.setColor(Color.BLUE);
            g2.drawOval(x + margin, y + margin, cellSize - 2 * margin, cellSize - 2 * margin);
            g2.setColor(Color.BLACK);
            String idStr = id.toString();
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(idStr);
            int textHeight = fm.getAscent();
            g2.drawString(idStr, x + (cellSize - textWidth) / 2, y + (cellSize + textHeight) / 2 - 2);
        }

        // Dibujar la base (círculo rojo con "B")
        Point base = mapa.getBase();
        int xBase = xOffset + base.y * cellSize;
        int yBase = yOffset + base.x * cellSize;
        g2.setColor(Color.RED);
        int baseMargin = cellSize / 8;
        g2.fillOval(xBase + baseMargin, yBase + baseMargin, cellSize - 2 * baseMargin, cellSize - 2 * baseMargin);
        g2.setColor(Color.WHITE);
        String baseStr = "B";
        FontMetrics fmBase = g2.getFontMetrics();
        int baseTextWidth = fmBase.stringWidth(baseStr);
        int baseTextHeight = fmBase.getAscent();
        g2.drawString(baseStr, xBase + (cellSize - baseTextWidth) / 2, yBase + (cellSize + baseTextHeight) / 2 - 2);

        // Dibujar la ruta del mejor individuo, celda a celda, con una flecha en cada celda que indique la dirección.
        if (mejorIndividuo != null) {
            List<Point> ruta = mejorIndividuo.getRuta();
            if (ruta != null && ruta.size() > 1) {
                // Color de la flecha: verde con cierta transparencia (más opaco)
                Color arrowColor = new Color(0, 204, 0, 180);
                g2.setColor(arrowColor);
                g2.setStroke(new BasicStroke(2));
                for (int i = 0; i < ruta.size() - 1; i++) {
                    Point current = ruta.get(i);
                    Point next = ruta.get(i + 1);
                    // Calcular el centro de la celda actual
                    int cx = xOffset + current.y * cellSize + cellSize / 2;
                    int cy = yOffset + current.x * cellSize + cellSize / 2;

                    // Determinar la dirección: diferencia en fila y columna
                    int dx = next.y - current.y;  // Nota: la columna es "y" en Point
                    int dy = next.x - current.x;  // la fila es "x"

                    // Calcular el final de la flecha dentro de la celda: por ejemplo, la flecha se dibuja a 1/3 del cellSize en la dirección indicada.
                    int arrowLength = cellSize / 3;
                    int ex = cx + dx * arrowLength;
                    int ey = cy + dy * arrowLength;

                    // Dibujar la flecha en la celda actual
                    drawArrowInCell(g2, cx, cy, ex, ey);
                }
            }
        }

        // Ahora dibujar las flechas en cada celda
        for (Map.Entry<Point, List<Arrow>> entry : cellArrows.entrySet()) {
            Point cell = entry.getKey();
            List<Arrow> arrows = entry.getValue();

            // Calcular el centro de la celda
            int cx = xOffset + cell.y * cellSize + cellSize / 2;
            int cy = yOffset + cell.x * cellSize + cellSize / 2;

            for (Arrow arrow : arrows) {
                drawArrowInCell(g2, cx, cy, arrow, cellSize);
            }
        }

        // Dibujar la leyenda
        dibujarLeyenda(g2);
    }

    /**
     * Dibuja una flecha dentro de la celda, partiendo del centro (x1, y1) hacia (x2, y2).
     * Se dibuja una línea principal y dos líneas laterales para formar la cabeza de la flecha.
     */
    private void drawArrowInCell(Graphics2D g2, int x1, int y1, int x2, int y2) {
        // Dibujar la línea principal
        g2.drawLine(x1, y1, x2, y2);
        // Calcular el ángulo de la línea
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 6;
        // Calcular los dos puntos para la cabeza de la flecha
        int xArrow1 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
        int xArrow2 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));
        // Dibujar las líneas de la cabeza de la flecha
        g2.drawLine(x2, y2, xArrow1, yArrow1);
        g2.drawLine(x2, y2, xArrow2, yArrow2);
    }

    private void dibujarLeyenda(Graphics2D g2) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int legendWidth = 160;
        int legendHeight = 130;
        int xInicio = panelWidth - legendWidth - 10;
        int yInicio = panelHeight - legendHeight - 10;

        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(xInicio, yInicio, legendWidth, legendHeight, 15, 15);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(xInicio, yInicio, legendWidth, legendHeight, 15, 15);

        int margen = 10;
        int cuadroTam = 15;
        int y = yInicio + margen + cuadroTam;

        // Leyenda: Base
        g2.setColor(Color.RED);
        g2.fillRect(xInicio + margen, y, cuadroTam, cuadroTam);
        g2.setColor(Color.BLACK);
        g2.drawString("Base", xInicio + margen + cuadroTam + 5, y + cuadroTam - 2);
        y += cuadroTam + 10;

        // Leyenda: Habitación
        g2.setColor(new Color(173, 216, 230));
        g2.fillOval(xInicio + margen, y, cuadroTam, cuadroTam);
        g2.setColor(Color.BLUE);
        g2.drawOval(xInicio + margen, y, cuadroTam, cuadroTam);
        g2.setColor(Color.BLACK);
        g2.drawString("Habitación", xInicio + margen + cuadroTam + 5, y + cuadroTam - 2);
        y += cuadroTam + 10;

        // Leyenda: Obstáculo
        g2.setColor(new Color(255, 102, 0));
        g2.fillRect(xInicio + margen, y, cuadroTam, cuadroTam);
        g2.setColor(Color.BLACK);
        g2.drawString("Obstáculo", xInicio + margen + cuadroTam + 5, y + cuadroTam - 2);
        y += cuadroTam + 10;

        // Leyenda: Ruta óptima (flecha)
        g2.setColor(new Color(0, 204, 0, 180));
        int centerX = xInicio + margen + cuadroTam/2;
        int centerY = y + cuadroTam/2;
        int endX = centerX + 10;
        int endY = centerY; // ejemplo horizontal
        drawArrowInCell(g2, centerX, centerY, endX, endY);
        g2.setColor(Color.BLACK);
        g2.drawString("Ruta óptima", xInicio + margen + cuadroTam + 5, y + cuadroTam - 2);
    }

    /**
     * Dibuja la flecha dentro de la celda. El color es más opaco y lleva un número
     * indicando el orden de visita.
     */
    private void drawArrowInCell(Graphics2D g2, int cx, int cy, Arrow arrow, int cellSize) {
        // flecha opaca en verde
        Color arrowColor = new Color(0, 200, 0, 180);
        g2.setColor(arrowColor);
        g2.setStroke(new BasicStroke(2));

        // Calcular hacia dónde apunta la flecha.
        // dx, dy ∈ { -1, 0, +1}, suponiendo que la ruta es ortogonal
        int arrowLength = cellSize / 3; // longitud
        int ex = cx + arrow.getDx() * arrowLength;
        int ey = cy + arrow.getDy() * arrowLength;

        // Dibuja la línea principal
        g2.drawLine(cx, cy, ex, ey);

        // Dibuja la cabeza de la flecha
        double angle = Math.atan2(ey - cy, ex - cx);
        int arrowSize = 6;
        int xArrow1 = (int) (ex - arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = (int) (ey - arrowSize * Math.sin(angle - Math.PI / 6));
        int xArrow2 = (int) (ex - arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = (int) (ey - arrowSize * Math.sin(angle + Math.PI / 6));
        g2.drawLine(ex, ey, xArrow1, yArrow1);
        g2.drawLine(ex, ey, xArrow2, yArrow2);

        // Dibujar el número de orden en la flecha
        String orderStr = String.valueOf(arrow.getOrder());
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(orderStr);
        int th = fm.getAscent();
        // Podemos dibujarlo cerca del ex, ey
        int labelX = ex - tw / 2;
        int labelY = ey - th / 2;
        g2.setColor(Color.BLACK);
        g2.drawString(orderStr, labelX, labelY);
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}
