package gui;

import gui.components.UIFactory;
import service.AppServices;
import util.UIConstants;
import util.ValidationException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Parent class of all windows (Welcome, Login, Register, Dashboard).
 * It contains the window settings and helper methods that every window needs,
 * so the same code is not repeated in each window.
 */
public abstract class BaseFrame extends JFrame {

    protected final AppServices services;

    protected BaseFrame(String title, AppServices services, int width, int height) {
        super(title + " | Trajectory");
        this.services = services;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setMinimumSize(new Dimension(Math.min(width, 900), Math.min(height, 600)));
        setLocationRelativeTo(null);   // centre on screen
        getContentPane().setBackground(UIConstants.BACKGROUND);
    }

    /**
     * Opens another window and closes this one.
     */
    protected void switchTo(JFrame nextFrame) {
        nextFrame.setVisible(true);
        dispose();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a friendly message for any exception.
     * ValidationException = user mistake; IOException = file problem.
     */
    public void handleError(Exception e) {
        if (e instanceof ValidationException) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Please check your input",
                    JOptionPane.WARNING_MESSAGE);
        } else if (e instanceof java.io.IOException) {
            showError("Could not read or save data files.\nDetails: " + e.getMessage());
        } else {
            showError("Something went wrong: " + e.getMessage());
        }
    }

    /**
     * Creates the dark branding panel used on the left side of the Welcome, Login and Register screens.
     */
    protected JPanel createBrandPanel(String heading, String description, String[] highlights) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, UIConstants.SIDEBAR, getWidth(), getHeight(), new Color(0x1E2A4A)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // soft decorative circles
                g2.setColor(new Color(37, 99, 235, 45));
                g2.fillOval(getWidth() - 220, -90, 320, 320);
                g2.setColor(new Color(22, 163, 74, 30));
                g2.fillOval(-120, getHeight() - 200, 280, 280);
                g2.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(56, 48, 48, 48));

        JLabel logo = UIFactory.label("Trajectory", UIConstants.FONT_HEADING, Color.WHITE);
        JLabel logoSub = UIFactory.label("CAREER SKILL LEARNING PATH PLANNER", UIConstants.FONT_SMALL_BOLD,
                new Color(0x93B4F8));

        panel.add(logo);
        panel.add(logoSub);
        panel.add(javax.swing.Box.createVerticalGlue());

        // Each line of the heading becomes its own big label
        for (String line : heading.split("\n")) {
            JLabel headingLine = UIFactory.label(line, UIConstants.FONT_HERO.deriveFont(34f), Color.WHITE);
            headingLine.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(headingLine);
        }
        panel.add(javax.swing.Box.createVerticalStrut(16));
        // A JLabel with simple HTML can wrap long text onto several lines
        JLabel descriptionLabel = UIFactory.label("<html><div style='width:380px'>" + description + "</div></html>",
                UIConstants.FONT_BODY, new Color(0xC8C8C8));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(descriptionLabel);
        panel.add(javax.swing.Box.createVerticalStrut(28));
        for (String highlight : highlights) {
            JLabel item = UIFactory.label("\u25CF   " + highlight, UIConstants.FONT_BODY, Color.WHITE);
            item.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(item);
        }
        panel.add(javax.swing.Box.createVerticalGlue());
        JLabel footer = UIFactory.label("Works fully offline \u2022 Your data stays on this computer",
                UIConstants.FONT_SMALL, new Color(0x8A8A8A));
        panel.add(footer);

        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    /**
     * A read-only text area that wraps long text over multiple lines (JLabel cannot wrap).
     */
    protected static JTextArea createWrappedText(String text, java.awt.Font font, Color color) {
        JTextArea area = new JTextArea(text);
        area.setFont(font);
        area.setForeground(color);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(null);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        return area;
    }
}
