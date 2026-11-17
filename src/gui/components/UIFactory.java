package gui.components;

import model.SkillStatus;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Factory methods that create consistently styled Swing components,
 * so every screen uses the same fonts, colours and spacing.
 */
public final class UIFactory {

    private UIFactory() {
    }

    // ---------------- Labels ----------------

    public static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    public static JLabel heading(String text) {
        return label(text, UIConstants.FONT_HEADING, UIConstants.TEXT);
    }

    public static JLabel secondary(String text) {
        return label(text, UIConstants.FONT_BODY, UIConstants.TEXT_SECONDARY);
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = label(text, UIConstants.FONT_SMALL_BOLD, UIConstants.TEXT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return label;
    }

    /**
     * A button that looks like a blue text link.
     */
    public static javax.swing.JButton linkButton(String text) {
        javax.swing.JButton button = new javax.swing.JButton(text);
        button.setFont(UIConstants.FONT_SMALL_BOLD);
        button.setForeground(UIConstants.PRIMARY);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Adds components one below the other (the panel must use a vertical BoxLayout).
     * Each component is left aligned; fields and rows are stretched to the full width.
     */
    public static void stackVertically(javax.swing.JPanel panel, Component... components) {
        for (Component component : components) {
            if (component instanceof JComponent) {
                JComponent jc = (JComponent) component;
                jc.setAlignmentX(Component.LEFT_ALIGNMENT);
                if (jc instanceof JTextField || jc instanceof javax.swing.JPanel
                        || jc instanceof JComboBox || jc instanceof javax.swing.AbstractButton) {
                    jc.setMaximumSize(new Dimension(Integer.MAX_VALUE, jc.getPreferredSize().height));
                }
            }
            panel.add(component);
        }
    }

    // ---------------- Input fields ----------------

    private static Border fieldBorder(Color lineColor) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    /**
     * Gives a field a light border that turns blue while the user is typing in it.
     */
    private static void applyFieldStyle(JComponent field) {
        field.setFont(UIConstants.FONT_BODY);
        field.setForeground(UIConstants.TEXT);
        field.setBackground(UIConstants.CARD);
        field.setBorder(fieldBorder(UIConstants.BORDER));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(fieldBorder(UIConstants.PRIMARY));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(fieldBorder(UIConstants.BORDER));
            }
        });
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        applyFieldStyle(field);
        field.setPreferredSize(new Dimension(260, 40));
        return field;
    }

    public static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        applyFieldStyle(field);
        field.setPreferredSize(new Dimension(260, 40));
        return field;
    }

    public static JTextArea textArea(int rows) {
        JTextArea area = new JTextArea(rows, 20);
        applyFieldStyle(area);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    public static <T> JComboBox<T> comboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(UIConstants.FONT_BODY);
        combo.setBackground(UIConstants.CARD);
        combo.setForeground(UIConstants.TEXT);
        combo.setPreferredSize(new Dimension(260, 40));
        return combo;
    }

    // ---------------- Scroll panes ----------------

    public static JScrollPane scrollPane(Component view) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        styleScrollBar(scroll.getVerticalScrollBar());
        styleScrollBar(scroll.getHorizontalScrollBar());
        return scroll;
    }

    private static void styleScrollBar(JScrollBar bar) {
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(10, 10));
        bar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(0xC9C9C4);
                trackColor = UIConstants.BACKGROUND;
            }

            @Override
            protected javax.swing.JButton createDecreaseButton(int orientation) {
                return zeroButton();
            }

            @Override
            protected javax.swing.JButton createIncreaseButton(int orientation) {
                return zeroButton();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
                // transparent track
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4, 8, 8);
                g2.dispose();
            }

            private javax.swing.JButton zeroButton() {
                javax.swing.JButton button = new javax.swing.JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
    }

    // ---------------- Tables ----------------

    /**
     * Applies the application's table style: taller rows, no grid, soft selection colour.
     */
    public static void styleTable(JTable table) {
        table.setFont(UIConstants.FONT_BODY);
        table.setForeground(UIConstants.TEXT);
        table.setBackground(UIConstants.CARD);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(UIConstants.NEUTRAL_LIGHT);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(UIConstants.PRIMARY_LIGHT);
        table.setSelectionForeground(UIConstants.TEXT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focus, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(t, value, false, false, row, column);
                cell.setFont(UIConstants.FONT_SMALL_BOLD);
                cell.setForeground(UIConstants.TEXT_SECONDARY);
                cell.setBackground(new Color(0xFAFAF8));
                cell.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                return cell;
            }
        });

        DefaultTableCellRenderer padded = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focus, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(t, value, selected, false, row, column);
                cell.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return cell;
            }
        };
        table.setDefaultRenderer(Object.class, padded);
    }

    /**
     * Renderer that colours a "Status" column (Completed = green, In Progress = amber, ...).
     */
    public static DefaultTableCellRenderer statusRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focus, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(t, value, selected, false, row, column);
                cell.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                cell.setFont(UIConstants.FONT_SMALL_BOLD);
                String text = value == null ? "" : value.toString();
                if (text.equals(SkillStatus.COMPLETED.getLabel())) {
                    cell.setForeground(UIConstants.SUCCESS);
                } else if (text.equals(SkillStatus.IN_PROGRESS.getLabel()) || text.equals("Pending")) {
                    cell.setForeground(UIConstants.WARNING);
                } else if (text.equals("Overdue")) {
                    cell.setForeground(UIConstants.DANGER);
                } else {
                    cell.setForeground(UIConstants.NEUTRAL);
                }
                cell.setText("\u25CF  " + text);   // small dot before the text
                return cell;
            }
        };
    }

    /**
     * Wraps a table in a rounded card with a scroll pane.
     */
    public static RoundedPanel tableCard(JTable table) {
        RoundedPanel card = new RoundedPanel(new java.awt.BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UIConstants.CARD);
        styleScrollBar(scroll.getVerticalScrollBar());
        card.add(scroll, java.awt.BorderLayout.CENTER);
        return card;
    }
}
