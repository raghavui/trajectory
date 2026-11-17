package gui.components;

import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A small dashboard card showing one statistic, e.g. "Completed Skills: 8".
 */
public class StatCard extends RoundedPanel {

    private final JLabel valueLabel;
    private final JLabel subtitleLabel;

    public StatCard(String title, Color accentColor) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(UIConstants.FONT_SMALL_BOLD);
        titleLabel.setForeground(UIConstants.TEXT_SECONDARY);

        valueLabel = new JLabel("-");
        valueLabel.setFont(UIConstants.FONT_STAT);
        valueLabel.setForeground(UIConstants.TEXT);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 2, 0));

        subtitleLabel = new JLabel(" ");
        subtitleLabel.setFont(UIConstants.FONT_SMALL);
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);

        text.add(titleLabel);
        text.add(valueLabel);
        text.add(subtitleLabel);

        add(new AccentBar(accentColor), BorderLayout.WEST);
        add(text, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    /**
     * Smaller font for long text values such as a career name.
     */
    public void setValue(String value, boolean longText) {
        valueLabel.setFont(longText ? UIConstants.FONT_HEADING : UIConstants.FONT_STAT);
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    /**
     * Thin coloured strip on the left side of the card.
     */
    private static class AccentBar extends JPanel {
        private final Color color;

        AccentBar(Color color) {
            this.color = color;
            setOpaque(false);
            setPreferredSize(new Dimension(16, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 4, 4, getHeight() - 8, 4, 4);
            g2.dispose();
        }
    }
}
