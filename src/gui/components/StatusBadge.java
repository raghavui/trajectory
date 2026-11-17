package gui.components;

import model.SkillStatus;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A coloured "pill" label that shows a status:
 * grey = Not Started, amber = In Progress, green = Completed.
 */
public class StatusBadge extends JLabel {

    private Color background = UIConstants.NEUTRAL_LIGHT;

    public StatusBadge() {
        setFont(UIConstants.FONT_SMALL_BOLD);
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
    }

    public StatusBadge(SkillStatus status) {
        this();
        setStatus(status);
    }

    public void setStatus(SkillStatus status) {
        setText(status.getLabel());
        setForeground(getForegroundFor(status));
        background = getBackgroundFor(status);
        repaint();
    }

    /**
     * Overloaded version for any text with custom colours (used for goal status).
     */
    public void setStatus(String text, Color foreground, Color background) {
        setText(text);
        setForeground(foreground);
        this.background = background;
        repaint();
    }

    public static Color getForegroundFor(SkillStatus status) {
        switch (status) {
            case COMPLETED:
                return UIConstants.SUCCESS;
            case IN_PROGRESS:
                return UIConstants.WARNING;
            default:
                return UIConstants.NEUTRAL;
        }
    }

    public static Color getBackgroundFor(SkillStatus status) {
        switch (status) {
            case COMPLETED:
                return UIConstants.SUCCESS_LIGHT;
            case IN_PROGRESS:
                return UIConstants.WARNING_LIGHT;
            default:
                return UIConstants.NEUTRAL_LIGHT;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
