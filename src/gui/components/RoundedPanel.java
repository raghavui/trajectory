package gui.components;

import util.UIConstants;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/**
 * A JPanel with rounded corners and an optional thin border.
 * Used for all "cards" in the application.
 */
public class RoundedPanel extends JPanel {

    private Color fillColor;
    private Color borderColor;
    private final int radius;

    public RoundedPanel(LayoutManager layout) {
        this(layout, UIConstants.CARD, UIConstants.BORDER, UIConstants.RADIUS);
    }

    public RoundedPanel(LayoutManager layout, Color fillColor, Color borderColor, int radius) {
        super(layout);
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.radius = radius;
        setOpaque(false);   // we paint the background ourselves
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fillColor);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
