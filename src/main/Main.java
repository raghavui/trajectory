package main;

import gui.WelcomeFrame;
import service.AppServices;
import util.UIConstants;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Starting point of Trajectory (Career Skill Learning Path Planner).
 *
 * Run this class to start the application. All data is stored in the "data"
 * folder inside the project folder (it is created automatically if missing).
 */
public class Main {

    public static void main(String[] args) {
        applyLookAndFeel();

        // Create every service once; all windows share these objects
        AppServices services = new AppServices("data");

        // Swing windows must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new WelcomeFrame(services).setVisible(true));
    }

    /**
     * Uses the operating system's native look for dialogs and drop-downs,
     * and sets our own fonts and colours for message boxes.
     */
    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If the system look is not available, Swing's default look is used instead
            System.err.println("Could not apply system look and feel: " + e.getMessage());
        }
        UIManager.put("OptionPane.messageFont", UIConstants.FONT_BODY);
        UIManager.put("OptionPane.buttonFont", UIConstants.FONT_BODY);
        UIManager.put("ToolTip.font", UIConstants.FONT_SMALL);
        UIManager.put("ComboBox.font", UIConstants.FONT_BODY);
    }
}
