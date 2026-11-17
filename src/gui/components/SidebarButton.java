package gui.components;

import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Navigation button used in the dark sidebar. Shows a blue highlight when selected.
 */
public class SidebarButton extends JButton {

    private boolean selected;
    private boolean hovered;

    public SidebarButton(String text) {
        super(text);
        setFont(UIConstants.FONT_BODY);
        setForeground(UIConstants.SIDEBAR_TEXT);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 12));
        setAlignmentX(LEFT_ALIGNMENT);

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

    public void setSelectedItem(boolean selected) {
        this.selected = selected;
        setForeground(selected ? java.awt.Color.WHITE : UIConstants.SIDEBAR_TEXT);
        setFont(selected ? UIConstants.FONT_SUBHEADING : UIConstants.FONT_BODY);
        repaint();
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 42);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 42);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (selected) {
            g2.setColor(UIConstants.PRIMARY);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        } else if (hovered) {
            g2.setColor(UIConstants.SIDEBAR_HOVER);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
