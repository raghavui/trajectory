package gui.components;

import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A flat, rounded button that replaces the default grey Swing button.
 * The {@link Style} decides its colours.
 */
public class ModernButton extends JButton {

    public enum Style { PRIMARY, SECONDARY, SUCCESS, DANGER, DARK }

    private final Style style;
    private boolean hovered;

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        setFont(UIConstants.FONT_SUBHEADING);
        setForeground(getTextColor());
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Event handling: change colour when the mouse moves over the button
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    private Color getBaseColor() {
        switch (style) {
            case SECONDARY:
                return UIConstants.CARD;
            case SUCCESS:
                return UIConstants.SUCCESS;
            case DANGER:
                return UIConstants.DANGER_LIGHT;
            case DARK:
                return UIConstants.SIDEBAR_HOVER;
            default:
                return UIConstants.PRIMARY;
        }
    }

    private Color getHoverColor() {
        switch (style) {
            case SECONDARY:
                return UIConstants.NEUTRAL_LIGHT;
            case SUCCESS:
                return UIConstants.SUCCESS.darker();
            case DANGER:
                return new Color(0xFAD4D4);
            case DARK:
                return new Color(0x333333);
            default:
                return UIConstants.PRIMARY_DARK;
        }
    }

    private Color getTextColor() {
        switch (style) {
            case SECONDARY:
                return UIConstants.TEXT;
            case DANGER:
                return UIConstants.DANGER;
            default:
                return Color.WHITE;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width, Math.max(size.height, 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = !isEnabled() ? UIConstants.NEUTRAL_LIGHT : (hovered ? getHoverColor() : getBaseColor());
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        if (style == Style.SECONDARY) {
            g2.setColor(UIConstants.BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (style != null) {
            setForeground(enabled ? getTextColor() : UIConstants.TEXT_SECONDARY);
        }
    }
}
