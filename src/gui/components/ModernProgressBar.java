package gui.components;

import util.UIConstants;

import javax.swing.JProgressBar;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A JProgressBar with a flat rounded look.
 * It is still a real JProgressBar, so setValue(int) works as usual.
 */
public class ModernProgressBar extends JProgressBar {

    private final Color barColor;
    private final int barHeight;

    public ModernProgressBar() {
        this(UIConstants.PRIMARY, 10);
    }

    public ModernProgressBar(Color barColor, int barHeight) {
        super(0, 100);
        this.barColor = barColor;
        this.barHeight = barHeight;
        setOpaque(false);
        setBorderPainted(false);
        setStringPainted(false);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, barHeight);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, barHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int y = (getHeight() - barHeight) / 2;
        g2.setColor(UIConstants.NEUTRAL_LIGHT);
        g2.fillRoundRect(0, y, getWidth(), barHeight, barHeight, barHeight);
        int filled = (int) Math.round(getWidth() * getPercentComplete());
        if (filled > 0) {
            g2.setColor(getValue() >= getMaximum() ? UIConstants.SUCCESS : barColor);
            g2.fillRoundRect(0, y, Math.max(filled, barHeight), barHeight, barHeight, barHeight);
        }
        g2.dispose();
    }
}
